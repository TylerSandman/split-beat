package com.splitbeat.game;

public class Fraction {
	
	private int mNumerator;
	private int mDenominator;
	
	Fraction(){
		mNumerator = 0;
		mDenominator = 1;
	}
	
	Fraction(int n, int d){
		int gcd = Fraction.GCD(n, d);
		mNumerator = n / gcd;
		mDenominator = d / gcd;		
	}
	
	public int getNumerator(){ return mNumerator; }
	public int getDenominator(){ return mDenominator; }
	
	public static int GCD(int a, int b){
	   if (b == 0) return a;
	   return GCD(b,a % b);
	}
	
	public Fraction plus(int n, int d){
		
		int numerator1 = this.mNumerator * d;
		int numerator2 = this.mDenominator * n;
		int newDenominator = this.mDenominator * d;
		
		int newNumerator = numerator1 + numerator2;
		int gcd = Fraction.GCD(newNumerator, newDenominator);
		Fraction f = new Fraction(newNumerator / gcd, newDenominator / gcd);
		return f;
	}
	
	public Fraction plus(Fraction fraction){
		
		int n = fraction.getNumerator();
		int d = fraction.getDenominator();
		return this.plus(n, d);
	}
	
	public Fraction minus(int n, int d){
		return plus(-n, d);
	}
	
	public Fraction minus(Fraction fraction){
		int n = fraction.getNumerator();
		int d = fraction.getDenominator();
		return plus(-n, d);
	}
	
	public float toFloat(){
		return (float) mNumerator / mDenominator;
	}
}
