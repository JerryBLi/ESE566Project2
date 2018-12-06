package ese566.partition;

public class Task {

	private int weightSW;
	private int weightHW;
	private int currentWeight;
	private boolean isSW;
	private int taskNum;
	
	public Task(int weightSW, int weightHW,int taskNum)
	{
		this.weightHW = weightHW;
		this.weightSW = weightSW;
		this.taskNum = taskNum;
		isSW = false;
	}
	
	public void setIsSW(boolean isSW)
	{
		this.isSW = isSW;
		currentWeight = (isSW ? weightSW : weightHW);
	}
	
	public int getTaskNum()
	{
		return taskNum;
	}
	
	public int getCurrentWeight()
	{
		return currentWeight;
	}
	
	public int getWeightSW()
	{
		return weightSW;
	}
	
	public int getWeightHW()
	{
		return weightHW;
	}
	
	public boolean isSW()
	{
		return isSW;
	}
	
}
