package ese566.partition;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

public class HwSwPartition {

	private ArrayList<Task> tasks;
	private int [] [] adjacencyMatrix;
	private int [] [] degreeMatrix;
	
	private int matrixRows;
	private int matrixCols;
	
	private Node [] nodes;
	
	private double avgWeight;
	private double sigma;
	private double alpha; // equivalent to 1/λ
	
	//percentage of sigma for us to check whether or not to terminate
	private static final double SIGMA_PERCENTAGE = 0.1;
	
	/**
	 * Constructor to instantiate a new HwSwPartition
	 * @param numNodes number of nodes in graph
	 * @param adjacencyMatrix the adjacency matrix of the graph g_a(t)
	 * @param tasks The task list P(t)
	 */
	public HwSwPartition(int numNodes, int [] [] adjacencyMatrix, ArrayList<Task> tasks)
	{
		matrixRows = adjacencyMatrix.length;
		matrixCols = adjacencyMatrix[0].length;
		if(adjacencyMatrix == null || (matrixRows != matrixCols))
		{
			adjacencyMatrix = new int [0][0];
			matrixRows = 0;
			matrixCols = 0;
			System.out.println("Error: Bad Input - Adjacency Matrix");
		}
		
		//Create Nodes 
		nodes = new Node[numNodes];
		for(int i = 0; i < numNodes; i++)
		{
			nodes[i] = new Node(i);
		}
		
		//Set the adjacency matrix
		this.adjacencyMatrix = adjacencyMatrix;
		
		//Set the tasks
		this.tasks = tasks;
		
		//Create the degree matrix
		degreeMatrix = createDegreeMatrix(adjacencyMatrix);
		
		//calculate the alpha value
		try
		{
			alpha = calculateAlphaValue();
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			System.exit(-1);
		}
		
		
		
	}
	
	
	/**
	 * Display the optimal partition to user
	 */
	private void outputParetoFront()
	{
		for(Node n : nodes)
		{
			n.printNode();
		}
	}
	
	/**
	 * Partition the system into HW and SW tasks per node
	 * @throws Exception Exception thrown if runtime exceeds a predetermined number of cycles
	 */
	public void partition() throws Exception
	{
		System.out.println("-----------------Begin Partition-----------------");
		System.out.println("Alpha Value: " + alpha);
		int loopCounter = 1; //This is used to keep trace of the loop in case we want a max num of iterations
		
		//initialize the population
		initPop();
		
		//calculate average weight
		avgWeight = calculateAvgWeight();
		
		//perform the first round of discreteDiffusion
		discreteDiffusion();
		
		//Continue to improve and perform discrete diffusion
		while(!terminate())
		{
			implementationSelection();
			avgWeight = calculateAvgWeight();
			discreteDiffusion();
			loopCounter++;
		}
		
		//display result to user
		System.out.println("Number of interations: " + loopCounter);
		outputParetoFront();
		
	}
	
	/**
	 * Initializes the tasks randomly as either hardware or software tasks
	 * Assigns tasks randomly to nodes
	 */
	private void initPop()
	{
		for(Task t : tasks)
		{
			int hwSW = ThreadLocalRandom.current().nextInt(0, 2);
			int currentNode = ThreadLocalRandom.current().nextInt(0, nodes.length);
			boolean isSW = (hwSW == 1 ? true : false);
			
			t.setIsSW(isSW);
			nodes[currentNode].addTask(t);
		}
		
		
	}
	
	/**
	 * Initializes the tasks randomly as either hardware or software tasks
	 * Assigns ALL tasks to Node 1
	 */
	private void initPopSingle()
	{
		for(Task t : tasks)
		{
			int hwSW = ThreadLocalRandom.current().nextInt(0, 2);
			boolean isSW = (hwSW == 1 ? true : false);
			
			t.setIsSW(isSW);
			nodes[0].addTask(t);
		}
	}
	
	/**
	 * Discrete diffusion algorithm:
	 * 1) Go through each node and calculate yContinuous
	 * 2) Send or Receive tasks so that the sum of task weight <= yContinous 
	 */
	private void discreteDiffusion()
	{
		boolean [] isNodeVisited = new boolean[nodes.length];
		
		int currentNode = 0;
		
		while(!allNodesVisited(isNodeVisited))
		{
			//we're visiting the node in this iteration
			isNodeVisited[currentNode] = true;
			
			int nodeWithLargestLoadDiff = -1;
			int largestLoadDiff = 0;
			
			//find largest yCont of unvisited nodes
			for(int i =0; i < nodes.length; i++)
			{
				//if already visited, then skip
				if(isNodeVisited[i])
					continue;
				
				int currentLoadDiff = nodes[currentNode].calculateLoad() - nodes[i].calculateLoad();
				if(Math.abs(currentLoadDiff) > largestLoadDiff)
				{
					largestLoadDiff = Math.abs(currentLoadDiff);
					nodeWithLargestLoadDiff = i;
				}
			}
			
			if(nodeWithLargestLoadDiff == -1)
			{
				break;
			}
			
			//At this point we know which node is largest difference from currentNode
			
			//find yContinuous
			double yContinuous = alpha * largestLoadDiff;
			
			//do we send from currentNode to node i or vice versa?
			//if the load of currentNode - load of Node is positive, we send else, we receive
			if(nodes[currentNode].calculateLoad() - nodes[nodeWithLargestLoadDiff].calculateLoad() > 0)
			{
				nodes[currentNode].sendTasks(yContinuous, nodes[nodeWithLargestLoadDiff]);
			}
			else
			{
				nodes[nodeWithLargestLoadDiff].sendTasks(yContinuous, nodes[currentNode]);
			}
			
			currentNode = nodeWithLargestLoadDiff;
			
		}
		
		//TODO
	}
	
	/**
	 * Calculate if all nodes have been visited
	 * @param visited array of whether a node has been visited
	 * @return true is all nodes have been visited
	 */
	private boolean allNodesVisited(boolean [] visited)
	{
		for(boolean b : visited)
		{
			if(!b)
				return b;
		}
		return true;
	}
	
	/**
	 * Condition whether to stop the loop or not
	 * Calculated by abs(avgWeight - max(maxSW, maxHW)) < sigma
	 * @return boolean whether to stop the partition 
	 */
	private boolean terminate()
	{
		boolean isSatisfied = false;
		
		
		double maxWeight = calculateMaxWeight();
		
		double sigma = avgWeight * SIGMA_PERCENTAGE; //this is the end factor 
		
		if(Math.abs(avgWeight - maxWeight) < sigma)
		{
			isSatisfied = true;
		}
	
		return isSatisfied;
	}
	
	/**
	 * Equalize the HW and SW loads within each load
	 * Objective is simply minimize the sum of the software loads on a node and the sum of the hardware loads on a node
	 */
	private void implementationSelection()
	{
		for(Node n : nodes)
		{
			n.balanceHWSW(avgWeight);
		}
	}
	
	
	
	/* HELPER FUNCTIONS */
	/**
	 * Go through each node and calculate the average weight
	 * The average weight is the average of the average of HW and SW
	 * @return
	 */
	private double calculateAvgWeight()
	{
		double avgWeight = 0;
		double swAvg = 0;
		double hwAvg = 0;
		
		for(Node n : nodes)
		{
			ArrayList<Task> tasks = n.getTasks();
			for(Task t : tasks)
			{
				swAvg += (t.isSW() ? t.getCurrentWeight() : 0);
				hwAvg += (t.isSW() ? 0 : t.getCurrentWeight());
			}
			
		}
		swAvg = swAvg / nodes.length;
		hwAvg = hwAvg / nodes.length;
		avgWeight = ( swAvg + hwAvg) / 2;
		
		return avgWeight;
		
	}
	
	/**
	 * Calculate the maxWeight between HW and SW by
	 * 1) Go through all the hardware implemented nodes find maxHWWeight
	 * 2) Go through all the software implemented nodes find maxSWWeight
	 * 3) return heigher number between maxHWWeight and maxSWWeight
	 * @return
	 */
	private double calculateMaxWeight()
	{
		double maxSoftwareWeight = 0;
		double maxHardwareWeight = 0;
		
		for(Node n : nodes)
		{
			double softwareWeight = 0;
			double hardwareWeight = 0;
			ArrayList<Task> tasks = n.getTasks();
			for(Task t : tasks)
			{
				if(t.isSW())
				{
						softwareWeight += t.getCurrentWeight();
				}
				else
				{
						hardwareWeight += t.getCurrentWeight();
				}
			}
			if(hardwareWeight > maxHardwareWeight)
				maxHardwareWeight = hardwareWeight;
			if(softwareWeight > maxSoftwareWeight)
				maxSoftwareWeight = softwareWeight;
		}
		
		return Math.max(maxSoftwareWeight, maxHardwareWeight);
	}
	
	/**
	 * Creates the degree matrix from the adjacency matrix
	 * @return The degree matrix
	 */
	private int[][] createDegreeMatrix(int [][] adjMatrix)
	{
		int [][] degreeMatrix = new int[matrixRows][matrixCols];
		//calculate for each node
		for(int i = 0; i < matrixRows; i++)
		{
			int nodeDegree = 0;
			for(int j =0; j < matrixCols; j++)
			{
				if(adjacencyMatrix[i][j] == 1)
				{
					nodeDegree++;
				}
			}
			degreeMatrix[i][i] = nodeDegree;
		}
		
		return degreeMatrix;
	}
	
	/**
	 * Calculate the Laplacian matrix which is defined as L = D - B
	 * @param D the Node degrees as diagonal entries (createDegreeMatrix)
	 * @param B the adjacency matrix 
	 * @return the Laplacian matrix
	 */
	private double[][] calculateLaplacianMatrix(int[][] D, int[][] B)
	{
		double [][] laplacianMatrix = new double[matrixRows][matrixCols];
		for(int row = 0; row < matrixRows; row++)
		{
			for(int col = 0; col < matrixCols; col++)
			{
				laplacianMatrix[row][col] = (double)D[row][col] - (double)B[row][col];
			}
		}
		
		return laplacianMatrix;
	}
	
	/**
	 * The alpha value is defined as α = 1 / λ
	 * λ = a certain numbering for the non-zero eigenvalues of Laplacian-matrix L
	 * L = D - B
	 * D -> Node degrees as diagonal entries
	 * B -> adjacency matrix
	 * @return α
	 */
	private double calculateAlphaValue() throws Exception
	{
		double lambda = 0;
		
		Matrix laplacianMatrix = new Matrix(calculateLaplacianMatrix(degreeMatrix,adjacencyMatrix));
		EigenvalueDecomposition eigenValueDecomp = new EigenvalueDecomposition(laplacianMatrix);
		
		double [] eigenValues = eigenValueDecomp.getRealEigenvalues();
		
		//determine the best eigenValue to use. This value should be bigger than 1
		
		for(double eigenValue : eigenValues)
		{
			//no good, eigenvalue is too small
			if((eigenValue > -1) && (eigenValue < 1) )
			{
				continue;
			}
			//choose the first eigenValue
			else
			{
				lambda = eigenValue;
				break;
			}
		}
		
		//we havent found a match
		if(lambda == 0)
		{
			throw new Exception("Error: No lambda value found");
		}
		
		return 1 / lambda;
	}
	
	
}
