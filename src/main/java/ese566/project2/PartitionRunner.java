package ese566.project2;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ese566.partition.*;

/**
 * Hello world!
 *
 */
public class PartitionRunner 
{
	private static final int maxTaskLoad = 100;
	
    public static void main( String[] args )
    {
    	ArrayList<Task> tasks = generateRandomTasks(500000);
    	
    	int [][] matrixSmall = {
    			{0, 1, 1, 1},
    			{1, 0, 1, 1},
    			{1, 1, 0, 1},
    			{1, 1, 1, 0}
    	};
    	
    	int n = 50;
    	int [][] matrix = generateAdjacencyMatrix(n);
    	
    	writeAdjacencyMatrix(matrix);
    	writeTaskList(tasks);
    	
        HwSwPartition partition = new HwSwPartition(n,matrix,tasks);
        try {
        	partition.partition();
        }
        catch(Exception e)
        {
        	System.out.println(e.getMessage());
        }
        
    }
    
    public static ArrayList<Task> generateRandomTasks(int numTasks)
    {
    	ArrayList<Task> tasks = new ArrayList<Task>(numTasks);
    	for(int i =0; i < numTasks; i++)
    	{
    		int swLoad = ThreadLocalRandom.current().nextInt(1, maxTaskLoad + 1);
    		int hwLoad = ThreadLocalRandom.current().nextInt(1, maxTaskLoad + 1);
    		Task t = new Task(swLoad,hwLoad,i);
    		tasks.add(t);
    	}
    	
    	return tasks;
    }
    
    public static int [][] generateAdjacencyMatrix(int nodes)
    {
    	int [][] matrix = new int[nodes][nodes];
    	
    	for(int row = 0; row < nodes; row ++)
    	{
    		for(int col = row; col < nodes; col++)
    		{
    			if(row == col)
    			{
    				matrix[row][col] = 0;
    			}
    			else
    			{
    				int connection = ThreadLocalRandom.current().nextInt(0, 2);
    				matrix[row][col] = connection;
    				matrix[col][row] = connection;
    			}
    			
    		}
    		
    		//if whole row is 0, we populate at least one edge
    		boolean noConnection = true;
    		for(int col = 0; col < nodes; col++)
    		{
    			if(matrix[row][col] == 1)
    				noConnection = false;
    		}
    		
    		if(noConnection)
    		{
    			matrix[row][row+1] = 1;
    			matrix[row+1][row] = 1;
    		}
    		
    		
    	}
    	
    	return matrix;
    }
    
    public static void writeAdjacencyMatrix(int[][] matrix)
    {
    	try
    	{
    		 BufferedWriter writer = new BufferedWriter(new FileWriter("matrix.txt"));
    	     
    		 writer.write("{");
    		 for(int row = 0; row < matrix.length; row++)
    		 {
    			 writer.write("{");
    			 for(int col = 0; col < matrix[0].length; col++)
    			 {
    				 if(col != matrix[row].length-1)
    					 writer.write(matrix[row][col] + ", ");
    				 else
    					 writer.write(matrix[row][col] + "");
    			 }
    			 
    			 if(row != matrix.length -1)
    				 writer.write("},\n");
    			 else
    				 writer.write("}");
    		 }
    	        
    	     writer.write("}");
    	        writer.close();
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
       
    }
    
    public static void writeTaskList(ArrayList<Task> tasks)
    {
    	try
    	{
    		 BufferedWriter writer = new BufferedWriter(new FileWriter("tasks.txt"));
    	        
    	        for(int i = 0; i < tasks.size(); i++)
    	        {
    	        	Task t = tasks.get(i);
    	        	writer.write("Task " + i + ": HW - " +t.getWeightHW() +" // SW - "+t.getWeightSW() +"\n");
    	        }
    	        writer.close();
    	}
    	catch(Exception e)
    	{
    		System.out.println(e.getMessage());
    	}
    }
    
}
