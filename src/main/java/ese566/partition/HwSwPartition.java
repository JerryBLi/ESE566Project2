package ese566.partition;

import java.util.ArrayList;

public class HwSwPartition {

	private int [] [] adjacencyMatrix;
	private int [] [] degreeMatrix;
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
		//Create Nodes 
		nodes = new Node[numNodes];
		for(int i = 0; i < numNodes; i++)
		{
			nodes[i] = new Node();
		}
		
	}
	
	
	/**
	 * Display the optimal partition to user
	 */
	public void outputParetoFront()
	{
		
	}
	
	/**
	 * Partition the system into HW and SW tasks per node
	 * @throws Exception Exception thrown if runtime exceeds a predetermined number of cycles
	 */
	public void partition() throws Exception
	{
		
		initPop();
		
		discreteDiffusion();
		
		while(!terminate())
		{
			implementationSelection();
			discreteDiffusion();
		}
		
	}
	
	/**
	 * Initializes the tasks randomly as either hardware or software tasks
	 * Assigns tasks randomly to nodes
	 */
	private void initPop()
	{
		//TODO
	}
	
	/**
	 * Initializes the tasks randomly as either hardware or software tasks
	 * Assigns ALL tasks to Node 1
	 */
	private void initPopSingle()
	{
		
	}
	
	/**
	 * Discrete diffusion algorithm:
	 * 1) Go through each node and calculate yContinuous
	 * 2) Send or Receive tasks so that the sum of task weight <= yContinous 
	 */
	private void discreteDiffusion()
	{
		//TODO
	}
	
	/**
	 * Condition whether to stop the loop or not
	 * Calculated by abs(avgWeight - max(maxSW, maxHW)) < sigma
	 * @return boolean whether to stop the partition 
	 */
	private boolean terminate()
	{
		boolean isSatisfied = false;
		
		//TODO
		
		return isSatisfied;
	}
	
	/**
	 * Equalize the HW and SW loads within each load
	 */
	private void implementationSelection()
	{
		//TODO
	}
	
	
	
	/* HELPER FUNCTIONS */
	/**
	 * Go through each node and calculate the average weight
	 * @return
	 */
	private double calculateAvgWeight()
	{
		double avgWeight = 0;
		
		for(Node n : nodes)
		{
			ArrayList<Task> tasks = n.getTasks();
			for(Task t : tasks)
			{
				avgWeight += (double)t.getCurrentWeight();
			}
			
		}
		
		avgWeight = avgWeight / nodes.length;
		
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
			ArrayList<Task> tasks = n.getTasks();
			for(Task t : tasks)
			{
				if(t.isSW())
				{
					if(t.getCurrentWeight() > maxSoftwareWeight)
						maxSoftwareWeight = t.getCurrentWeight();
				}
				else
				{
					if(t.getCurrentWeight() > maxHardwareWeight)
						maxHardwareWeight = t.getCurrentWeight();
				}
			}
		}
		
		return Math.max(maxSoftwareWeight, maxHardwareWeight);
	}
	
	/**
	 * The alpha value is defined as α = 1 / λ
	 * λ = a certain numbering for the non-zero eigenvalues of Laplacian-matrix L
	 * L = D - B
	 * D -> Node degrees as diagonal entries
	 * B -> adjacency matrix
	 * @return α
	 */
	private double calculateAlphaValue()
	{
		double lambda = 0;
		
		return 1 / lambda;
	}
	
	
}
