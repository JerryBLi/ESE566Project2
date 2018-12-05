package ese566.partition;

public class HwSwPartition {

	int [] [] adjacencyMatrix;
	int [] [] degreeMatrix;
	Node [] nodes;
	
	public HwSwPartition(int numNodes)
	{
		nodes = new Node[numNodes];
	}
	
	public void outputParetoFront()
	{
		
	}
	
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
	
	private void initPop()
	{
		
	}
	
	private void discreteDiffusion()
	{
		
	}
	
	private boolean terminate()
	{
		boolean isSatisfied = false;
		
		return isSatisfied;
	}
	
	private void implementationSelection()
	{
		
	}
	
	
	
	
}
