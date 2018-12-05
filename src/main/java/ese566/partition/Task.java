package ese566.partition;

public class Task {

	private int weightSW;
	private int weightHW;
	private int currentWeight;
	private boolean isSW;
	
	
	public Task(int weightSW, int weightHW)
	{
		this.weightHW = weightHW;
		this.weightSW = weightSW;
		isSW = false;
	}
	
	public void setIsSW(boolean isSW)
	{
		this.isSW = isSW;
		currentWeight = (isSW ? weightSW : weightHW);
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
