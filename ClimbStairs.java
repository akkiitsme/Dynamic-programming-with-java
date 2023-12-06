package com.dynamic;

import java.util.Scanner;

public class ClimbStairs {
	
	//memo-ization
	public static int countpaths(int n,int[] qb) {
		if(n==0)
			return 1;
		if(n<0)
			return 0;
		if(qb[n]>0)
			return qb[n];
		
		int cp = countpaths(n-1,qb)+countpaths(n-2,qb)+countpaths(n-3,qb);
		qb[n] = cp;
		return cp;
	}

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter any number: ");
		int num = sc.nextInt();
		System.out.println("Total paths: "+countpaths(num,new int[num+1]));

	}

}
