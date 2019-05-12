package com.example.nuclerone.TaylorMethod;

public class G2 {
	double h;
	int i;
	public G2(double h,int i) {
		this.h = h;
		this.i = i;
	}
	
	
	public double calIn() {
		G1 g1 = new G1(h,i);
		return ((h-g1.calIn())/Taylor.li[i]-h*g1.calIn());
		
	}

}
