import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Jama.*;
import general.Super_random;
import general.Pair;

public class kmeans {

	//prints an array
	static void print_2darray(ArrayList<ArrayList<Double>> arr)
	{
		for (int i = 0; i < arr.size(); i++)
		{
			for(int j = 0; j < arr.get(i).size(); j++)
			{
				System.out.print(arr.get(i).get(j) + " ");
			}
			
			System.out.println();
		}
	}
	
	//prints an array
	static void print_2darray(double[][] arr)
	{
		for (int i = 0; i < arr.length; i++)
		{
			for(int j = 0; j < arr[0].length; j++)
			{
				System.out.print(arr[i][j] + " ");
			}
			
			System.out.println();
		}
	}
	
	static ArrayList<ArrayList<Double>> twodarrtolist(double[][] arr)
	{
		ArrayList<ArrayList<Double>> lis = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < arr.length; i++)
		{	lis.add(new ArrayList<Double>());
			for(int j = 0; j < arr[0].length; j++)
			{
				lis.get(i).add(arr[i][j]);
			}
		}
		
		return lis;
	}
	
	static ArrayList<Double> doublearr_to_arraylist(double[] arr)
	{
		ArrayList<Double> arrlist = new ArrayList<Double>();
		for(int i = 0; i < arr.length; i++)
		{
			arrlist.add(arr[i]);
		}
		
		return arrlist;
	}
	
	//computes C as specified from algorithm 2. Uses naive matrix multiplication
	static Matrix algorithm_2(Matrix A, int K_, int r, int m, int n)
	{
		double[][] r_arr = new double[n][r];
		Super_random rand = new Super_random(); 
		
		for(int i = 0; i < r_arr.length; i++)
		{
			for(int j = 0; j < r_arr[0].length; j++)
			{
				//gets a 0 or 1 with 50% chance each
				int random_num = rand.get_random_inclusize_int(0, 1);
				
				if(random_num == 0)
				{
					r_arr[i][j] = 1.0 /Math.sqrt((double)r);
				} else
				{
					r_arr[i][j] = -1.0 /Math.sqrt((double)r);
				}
			}
		}
		
		Matrix r_mat = new Matrix(r_arr);
		Matrix C = A.times(r_mat);
		
		assert(C.getRowDimension() == m);
		assert(C.getColumnDimension() == r);
		
		return C;
	}
	
	//norm(v1-v2)^2
	static double twovecsubthennormsquared(ArrayList<Double> v1, ArrayList<Double> v2)
	{
		assert(v1.size() == v2.size());
		double total = 0;
		for(int i = 0; i < v1.size(); i++)
		{
			total = total + ((v1.get(i) - v2.get(i))*(v1.get(i) - v2.get(i)));
		}
		
		return total;
	}
	
	static Pair<Integer, Double> get_clostest_center_index(ArrayList<Double> point, ArrayList<ArrayList<Double>> cluster_centers)
	{
		assert(point.size() == cluster_centers.get(0).size());
		
		int best_j = 0;
		double best_value = 2000000000;
		best_value = best_value*10000;		
		for(int j = 0; j < cluster_centers.size(); j++)
		{
			double value = twovecsubthennormsquared(point, cluster_centers.get(j));
			if(value < best_value)
			{
				best_value = value;
				best_j = j;
			}
		}
		
		return new Pair<Integer, Double>(best_j, best_value);
	}
	
	static double getLloydObjective(ArrayList<ArrayList<Double>> points, ArrayList<ArrayList<Double>> cluster_centers)
	{
		double total = 0;
		
		for(int i = 0; i < points.size(); i++)
		{
			total = total + get_clostest_center_index(points.get(i), cluster_centers).getRight();
		}
		
		return total;
	}
	
	static Pair<ArrayList<ArrayList<Double>>, Double> lloyds_alg(Matrix A, int K_)
	{
		
		int MAX_ITERARIONS = 500;
		Super_random rand = new Super_random();
		ArrayList<Integer> center_indexes = new ArrayList<Integer>();
		ArrayList<ArrayList<Double>> cluster_centers = new ArrayList<ArrayList<Double>>();
		ArrayList<ArrayList<Double>> points = twodarrtolist(A.getArray());
		int m = A.getRowDimension();
		int n = A.getColumnDimension();
		
		//randomly get centers
		while(center_indexes.size() < K_)
		{
			int index = rand.get_random_inclusize_int(0, m - 1);
			if(!center_indexes.contains(index))
			{
				center_indexes.add(index);
			}
		}
		
		for(int i = 0; i < center_indexes.size(); i++)
		{
			int index = center_indexes.get(i);
			ArrayList<Double> temp = points.get(index);
			cluster_centers.add(temp);
		}
		//System.out.println();
		//print_2darray(cluster_centers);
		
		
		
		assert(cluster_centers.size() == K_);
		
		//stores the point's indexes
		ArrayList<ArrayList<Integer>> clusters_indexes = new ArrayList<ArrayList<Integer>>();
		
		
		double best_value = 2000000000;
		best_value = best_value*10000;
		double new_best_value = 2000000000;
		new_best_value=new_best_value*10000;
		int itr = 0;
		
		do {
			clusters_indexes.clear();
			for(int i = 0; i < K_; i++)
			{
				clusters_indexes.add(new ArrayList<Integer>());
			}
			
			best_value = new_best_value;
			for(int i = 0; i < m; i++)
			{
				int j = get_clostest_center_index(points.get(i), cluster_centers).getLeft();
				clusters_indexes.get(j).add(i);
			}
			
			//iterate through centers
			cluster_centers.clear();
			for(int i = 0; i < K_; i++)
			{
				ArrayList<Double> new_center = new ArrayList<Double>();
				new_center.clear();
				//iterate for each feature
				for(int z = 0; z < n; z++)
				{
					double temp = 0;
					//iterate for each point in that cluster
					for(int j = 0; j < clusters_indexes.get(i).size(); j++)
					{
						temp = temp + points.get(clusters_indexes.get(i).get(j)).get(z);
					}	
					
					temp = temp / clusters_indexes.get(i).size();
					new_center.add(temp);
				}
				cluster_centers.add(new_center);
			}
			
			new_best_value = getLloydObjective(points, cluster_centers);
			itr++;
			System.out.println(itr + " " + new_best_value);
		} while(itr < MAX_ITERARIONS && new_best_value < best_value);
		
		if(new_best_value < best_value)
		{
			best_value = new_best_value;
		}
		
		//System.out.println();
		
		//print_2darray(cluster_centers);
		
		return new Pair<ArrayList<ArrayList<Double>>, Double>(cluster_centers, best_value);
	}
	
	static Matrix get_ind(Matrix A, int K_, ArrayList<ArrayList<Double>> cluster_centers, int m, int n)
	{
		double[][] ind_arr = new double[m][K_];
		for(int i = 0; i < ind_arr.length; i++)
		{
			for(int j = 0; j < ind_arr[0].length; j++)
			{
				ind_arr[i][j] = 0;
			}
		}
		
		ArrayList<ArrayList<Double>> A_arrlist = twodarrtolist(A.getArray());
		double[] cluster_sizes = new double[K_];
		int[] index_arr = new int[A_arrlist.size()];
		for(int i = 0; i < cluster_sizes.length; i++)
		{
			cluster_sizes[i]=0;
		}
		
		
		for(int i = 0; i < A_arrlist.size(); i++)
		{
			int j = get_clostest_center_index(A_arrlist.get(i), cluster_centers).getLeft();
			cluster_sizes[j]++;
			index_arr[i] = j;
		}
		
		for(int i = 0; i < A_arrlist.size(); i++)
		{
			int j = index_arr[i];
			ind_arr[i][j] = 1.0 / Math.sqrt(cluster_sizes[j]);
		}
		
		//System.out.println(ind_arr.length + " " + ind_arr[0].length);
		
		Matrix ind_mat = new Matrix(ind_arr);
		return ind_mat;
	}
	
	static double get_F(Matrix A, int K_, Matrix ind, int m, int n)
	{
		return Math.pow((A.minus((ind.times(ind.transpose())).times(A))).normF(),2);
	}
	
	
	//modified from https://stackoverflow.com/questions/24460480/permutation-of-an-arraylist-of-numbers-using-recursion
	public static ArrayList<ArrayList<ArrayList<Double>>> listPermutations(ArrayList<ArrayList<Double>> centers) {

	    if (centers.size() == 0) {
	    	ArrayList<ArrayList<ArrayList<Double>>> result = new ArrayList<ArrayList<ArrayList<Double>>>();
	        result.add(new ArrayList<ArrayList<Double>>());
	        return result;
	    }

	    ArrayList<ArrayList<ArrayList<Double>>> returnMe = new ArrayList<ArrayList<ArrayList<Double>>>();

	    ArrayList<Double> firstElement = centers.remove(0);

	    ArrayList<ArrayList<ArrayList<Double>>> recursiveReturn = listPermutations(centers);
	    for (ArrayList<ArrayList<Double>> li : recursiveReturn) {

	        for (int index = 0; index <= li.size(); index++) {
	        	ArrayList<ArrayList<Double>> temp = new ArrayList<ArrayList<Double>>(li);
	            temp.add(index, firstElement);
	            returnMe.add(temp);
	        }

	    }
	    return returnMe;
	}
	
	static double getAccuracy(Matrix A, Matrix Idn, ArrayList<ArrayList<Double>> centers)
	{
		ArrayList<ArrayList<Double>> arr = twodarrtolist(Idn.getArray());
		ArrayList<ArrayList<Double>> arr2 = twodarrtolist(A.getArray());
		double good = 0;
		double size = arr.size();
		double best_acc = 0;
		ArrayList<ArrayList<Double>> temp_center = new ArrayList<ArrayList<Double>>();
		for(int i = 0; i < centers.size(); i++)
		{
			temp_center.add(new ArrayList<Double>());
			for(int j =0; j < centers.get(i).size(); j++)
			{
				temp_center.get(i).add(centers.get(i).get(j));
			}
		}
		ArrayList<ArrayList<ArrayList<Double>>> centers2 = listPermutations(temp_center);
		for(int a = 0; a < centers2.size(); a++)
		{
			good = 0;
			ArrayList<ArrayList<Double>> centers3 = centers2.get(a);
			for(int i = 0; i < arr.size(); i++)
			{
				int nonzero_j = 0;
				for(int j = 0; j < arr.get(0).size(); j++)
				{
					if(arr.get(i).get(j) != 0)
					{
						nonzero_j = j;
						break;
					}
				}
				
				int best_j = get_clostest_center_index(arr2.get(i), centers3).getLeft();
				if(nonzero_j == best_j)
				{
					good++;
				}
			}
			
			if((good/size) > best_acc)
			{
				best_acc = (good/size);
			}
		}
		
		
		return best_acc;		
	}
	
	static int CENTER_MIN_RANGE = 0;
	static int CENTER_MAX_RANGE = 2000;
	static int K = 5;
	static ArrayList<ArrayList<Double>> real_centers = new ArrayList<ArrayList<Double>>();
	
	//m = number of points, n = number of dimensions
	static double[][] generateDataset(int m, int n) throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter("synth_centers", "UTF-8");
		Super_random rand = new Super_random(); 
		Random rand2 = new Random();
		
		real_centers.clear();
		
		//generate the centers
		for(int i = 0; i < K; i++)
		{
			real_centers.add(new ArrayList<Double>());
			for(int j = 0; j < n; j++)
			{
				int num = rand.get_random_inclusize_int(CENTER_MIN_RANGE, CENTER_MAX_RANGE);
				real_centers.get(i).add((double) num);
				writer.print(num + " ");
			}
			writer.println();
		}
		
		writer.close();
		
		//generate the dataset from clusters, 200 from each center using guassian distribution
		
		double[][] dataset = new double[m][n];
		
		int interval = m/K;
		int counter = 0;
		int centerindex = 0;
		for(int i = 0; i < m; i++)
		{
			if(counter == interval)
			{
				counter = 0;
				centerindex++;
			}
			
			for(int j = 0; j < n; j++)
			{
				dataset[i][j] = real_centers.get(centerindex).get(j) + rand2.nextGaussian();
			}
			
			counter++;
		}
		
		return dataset;
	}
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		
		boolean GENERATE_DATA = true;
		int M = 1000;
		int N = 2;
		
		if(GENERATE_DATA)
		{
			//1000 points, 2000 dimensions
			double [][] dataset = generateDataset(M, N);
			PrintWriter writer = new PrintWriter("synth", "UTF-8");
			for(int i = 0; i < dataset.length; i++)
			{
				for(int j = 0; j < dataset[0].length; j++)
				{
					writer.print(dataset[i][j] + " ");
				}
				writer.println();
			}
			writer.close(); 
		} else {
			double [][] dataset = new double[M][N];
			ArrayList<ArrayList<Double>> real_centers2 = new ArrayList<ArrayList<Double>>();
			File file = new File("synth"); 
		    Scanner sc = new Scanner(file);
	    
		    while(sc.hasNextDouble())
		    {
		    	for(int i = 0; i < dataset.length; i++)
				{
					for(int j = 0; j < dataset[0].length; j++)
					{
						dataset[i][j] = sc.nextDouble();
					}
				}
		    }
		    
		    File file2 = new File("synth_centers"); 
		    Scanner sc2 = new Scanner(file2);
		    while(sc2.hasNextDouble())
		    {   	
		    	for(int i = 0; i < K; i++)
		    	{
		    		real_centers2.add(new ArrayList<Double>());
		    		for(int j = 0; j < N; j++)
		    		{
		    			real_centers2.get(i).add(sc2.nextDouble());
		    		}
		    	}
		    }
		    
		    //print_2darray(real_centers2);
		    
		    System.out.println("reading done");
		    
		    //print_2darray(dataset);
		    long startTime = System.nanoTime();

		   
		    Matrix dataset_mat = new Matrix(dataset);
		    Pair<ArrayList<ArrayList<Double>>, Double> tmp = lloyds_alg(dataset_mat, K);
		    double best_value = tmp.getRight();
		    for(int i = 1; i < 5; i++)
		    {
		    	Pair<ArrayList<ArrayList<Double>>, Double> tmp2 = lloyds_alg(dataset_mat, K);
		    	if(tmp2.getRight() < best_value)
		    	{
		    		best_value = tmp2.getRight();
		    		tmp.setRight(tmp2.getRight());
		    		tmp.setLeft(tmp2.getLeft());
		    	}
		    }
		    		    
		    long endTime   = System.nanoTime();
		    long totalTime = endTime - startTime;
		    System.out.println("runtime " + totalTime/1000000000.0);
		    ArrayList<ArrayList<Double>> cluster_centers = tmp.getLeft();
		  
		    Matrix ind_mat = get_ind(dataset_mat, K, cluster_centers, M, N);
		    //print_2darray(ind_mat.getArray());
		    double f = get_F(dataset_mat, K, ind_mat, M, N);
		    System.out.println("full " + f);
		    System.out.println("acc " + getAccuracy(dataset_mat, ind_mat, real_centers2));
	
		    startTime = System.nanoTime();
		    
		    Matrix C = algorithm_2(dataset_mat, K, 1, M, N);
		    
		    Pair<ArrayList<ArrayList<Double>>, Double> tmp3 = lloyds_alg(C, K);
		    double best_value2 = tmp3.getRight();
		    for(int i = 1; i < 5; i++)
		    {
		    	Pair<ArrayList<ArrayList<Double>>, Double> tmp4 = lloyds_alg(C, K);
		    	if(tmp4.getRight() < best_value2)
		    	{
		    		best_value2 = tmp4.getRight();
		    		tmp3.setRight(tmp4.getRight());
		    		tmp3.setLeft(tmp4.getLeft());
		    	}
		    }  
		    
		    endTime   = System.nanoTime();
		    totalTime = endTime - startTime;
		    System.out.println("runtime " + totalTime/1000000000.0);
		    
		    ArrayList<ArrayList<Double>> cluster_centers2 = tmp3.getLeft();
		  
		    Matrix ind_mat2 = get_ind(C, K, cluster_centers2, M, N);
		    //print_2darray(ind_mat.getArray());
		    //System.out.println();
		    //print_2darray(ind_mat2.getArray());
		    //print_2darray(cluster_centers2);
		    double f2 = get_F(dataset_mat, K, ind_mat2, M, N);
		    System.out.println("partial " + f2);
		    System.out.println("acc " + getAccuracy(dataset_mat, ind_mat2, real_centers2));		    
		}
	}

}
