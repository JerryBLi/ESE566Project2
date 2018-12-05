package ese566.partition;

import java.util.ArrayList;

public class Node {

	private ArrayList<Task> tasks;
	
	public Node()
	{
		tasks = new ArrayList<Task>();
	}
	
	public void addTask(Task t)
	{
		tasks.add(t);
	}
	
	public void sendTasks(double yContinuous, Node n)
	{
		//TODO
		//take tasks out of this node and put it in node n
		//while there are tasks <= yContinuous
		//continually decrement yContinuous when sending Node
	}
	
	public ArrayList<Task> getTasks()
	{
		return tasks;
	}
	
}
