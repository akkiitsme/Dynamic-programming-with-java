package com.dynamic;

import java.util.Scanner;

public class FibonacciMemoization {
	
	public static int fibonacci(int n,int[] qb) {
		if(n==0||n==1)
			return n;		
		if(qb[n]!=0) {
			return qb[n];
		}
		int fibno = fibonacci(n-1,qb)+fibonacci(n-2,qb);	
		qb[n]=fibno;
		return fibno;
	}
	
	
	public static void main(String[] args) {		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter any number: ");
		int num = sc.nextInt();
		System.out.println("Fibonacci number is: "+fibonacci(num,new int[num+1]));		
	}

}
