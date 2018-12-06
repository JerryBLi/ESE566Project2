package ese566.partition;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class Node implements Comparable<Node> {

	private ArrayList<Task> tasks;

	private int currentLoad;
	
	private int numTimesNoTasksSent; //counter to keep track of how many times the sendTasks have been called but no tasks sent. If this number is too high, we should randomize the HW/SW
	
	private int nodeNum;
	
	private static final double errorMargin = 0.1;
	
	public Node(int nodeNum)
	{
		this.nodeNum = nodeNum;
		tasks = new ArrayList<Task>();
		currentLoad = 0;
		numTimesNoTasksSent = 0;
	}
	
	/**
	 * Adds a task to this Node. 
	 * @param t the task to add to this node
	 */
	public void addTask(Task t)
	{
		if(t != null)
		{
			tasks.add(t);
			currentLoad += t.getCurrentWeight();
		}
		
	}
	
	/**
	 * Send tasks from this node to a specific node. We want the number of tasks to be less than or equal to the value yContinuous
	 * After sending, we must remove the task from this node
	 * @param yContinuous the value of task weight we're trying to achieve being sent
	 * @param n the node to send the task to
	 */
	public void sendTasks(double yContinuous, Node n)
	{
		//take tasks out of this node and put it in node n
		//while there are tasks <= yContinuous
		//continually decrement yContinuous when sending Node
		
		//Let's use a crude method of finding the smallest number that can be sent and send it
		boolean taskSent = false;
		boolean canSend = false;
		do
		{
			canSend = false;
			Task taskToSend = null;
			int smallestLoad = -1;
			for(Task t : tasks)
			{
				//if the smallestLoad isn't initialized, set the smallestNode
				if((smallestLoad == -1 || t.getCurrentWeight() < smallestLoad))
				{
					smallestLoad = t.getCurrentWeight();
					taskToSend = t;
					canSend = true;
				}
					
			}
			
			//if the smallest load is largest than yCont, then there's no point in continuing
			if(smallestLoad > yContinuous)
				break;
			
			if(canSend)
			{
				
				n.addTask(taskToSend);
				tasks.remove(taskToSend);
				yContinuous -= taskToSend.getCurrentWeight();
				currentLoad -= taskToSend.getCurrentWeight();
				taskSent = true;
			}
		}while(canSend);
		
		if(!taskSent)
			numTimesNoTasksSent++;
	}
	
	/**
	 * Return the load of the current node
	 * @return the current load of the node
	 */
	public int calculateLoad()
	{		
		int load = 0;
		for(Task t : tasks)
		{
			load += t.getCurrentWeight();
		}
		return load;
	}
	
	/**
	 * Get all the tasks of the node
	 * @return
	 */
	public ArrayList<Task> getTasks()
	{
		return tasks;
	}
	
	
	/**
	 * We want to have the tasks split between hardware and software so that it's around the average weight
	 * @param avgWeight
	 */
	public void balanceHWSW(double avgWeight)
	{
		//if the number of times no task is sent is too high, we just randomize the HW/SW values
		if(numTimesNoTasksSent > 5)
		{
			for(Task t : tasks)
			{
				int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
				boolean isSW = (randomNum == 1 ? true : false);
				t.setIsSW(isSW);
			}
		}
		else
		{
			//we want the HW weight and the SW weight to be around the average weight
			double weightGoalHW = avgWeight;
			double weightGoalSW = avgWeight;
			double currentWeightHW = 0;
			double currentWeightSW = 0;
			
			//find the lower weight of HW/SW and implement as that
			for(Task t : tasks)
			{
				//set as HW node if HW is less
				if(t.getWeightHW() < t.getWeightSW())
				{
					t.setIsSW(false);
					currentWeightHW += t.getCurrentWeight();
				}
				else
				{
					t.setIsSW(true);
					currentWeightSW += t.getCurrentWeight();
				}
			}
			
			//check difference and balance
			boolean switched = true;
			while(switched)
			{
				double weightDifference = Math.abs(currentWeightHW - currentWeightSW);
				
				switched = false;
				
				//if the difference is smaller than the percentage, we call it good
				double avgTemp = currentWeightHW + currentWeightSW / 2.0; 
				if(weightDifference <= avgTemp * errorMargin)
					break;
				
				//if the HW weight is larger, we want to switch tasks to SW
				for(Task t : tasks)
				{
					if(currentWeightHW > currentWeightSW)
					{

							if(t.isSW())
								continue;
							
							if(t.getWeightSW() < weightDifference)
							{
								currentWeightHW -= t.getWeightHW();
								currentLoad -= t.getWeightHW();
								currentWeightSW += t.getWeightSW();
								currentLoad += t.getWeightSW();
								weightDifference = Math.abs(currentWeightHW - currentWeightSW);
								t.setIsSW(true);
								switched = true;
							}	
						
					}
					else
					{
		
							if(!t.isSW())
								continue;
							
							if(t.getWeightHW() < weightDifference)
							{
								currentWeightSW -= t.getWeightSW();
								currentLoad -= t.getWeightSW();
								currentWeightHW += t.getWeightHW();
								currentLoad += t.getWeightHW();
								weightDifference = Math.abs(currentWeightHW - currentWeightSW);
								t.setIsSW(false);
								switched = true;
							
						}
					}
				}

			}
			
		}

	}
	
	public void printNode()
	{
		System.out.print("Node " + nodeNum + " (Total Load: " +this.calculateLoad() +") | "
				+ "(Hardware Load: " + this.getHardwareLoad() + ") | "
				+ "(Software Load: " + this.getSoftwareLoad() + ") "
				+ "-  Tasks: ");
		
		for(Task t : tasks)
		{
			System.out.print("T"+t.getTaskNum()+"("+ (t.isSW() ? "SW - " : "HW - ") + t.getCurrentWeight() +") | ");
		}
		System.out.println();
	}
	

	/**
	 * Helper function for displaying the node data
	 * @return total hardware load
	 */
	private double getHardwareLoad()
	{
		double load = 0;
		for(Task t : tasks)
		{
			load += (t.isSW() ? 0 : t.getCurrentWeight());
		}
		
		return load;
	}
	
	/**
	 * Helper function for displaying the Node data
	 * @return total software load
	 */
	private double getSoftwareLoad()
	{
		double load = 0;
		for(Task t : tasks)
		{
			load += (t.isSW() ? t.getCurrentWeight() : 0);
		}
		
		return load;
	}
	
	public int compareTo(Node o) {
		
		if(currentLoad == o.calculateLoad())
			return 0;
		else if(currentLoad < o.calculateLoad())
			return -1;
		else
			return 1;
	}
	
}
