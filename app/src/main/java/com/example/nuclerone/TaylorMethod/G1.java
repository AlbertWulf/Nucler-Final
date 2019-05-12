package com.example.nuclerone.TaylorMethod;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;


public class G1 {
	double h;
	int i;
	double value;
	public G1(double h,int i) {
		this.h = h;
		this.i = i;
		
	}
	public double calIn() {
		return (1-Math.exp(-Taylor.li[i]*h))/Taylor.li[i];
	}

	 
}
