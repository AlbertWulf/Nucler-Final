package com.example.nuclerone.TaylorMethod;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.linear.LUDecomposition; 
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
public class F2 {
	double h;
	double tn;
	double value;
	public F2(double h,double tn) {
		this.h = h;
		this.tn = tn;
		
	}
	public double calIn() {
		func = new Myfunction();
		integrator =  new SimpsonIntegrator();
		return integrator.integrate(1000,func,tn , tn+h);
	}

	 UnivariateFunction func = null;
	 UnivariateIntegrator integrator = null;
	
	 public class Myfunction implements UnivariateFunction{
		 @Override
	
	     public double value(double x) {
			 
			 //return 0.0014*Math.sin(Math.PI*x/5)*(x-h-tn)/Taylor.l;
			 if(Taylor.k==1) {
				 return Taylor.sin_a*Math.sin(Taylor.sin_b*x)*(x-h-tn)/Taylor.l;
			 }
				 else if(Taylor.k==-1) {
					 return Taylor.A*x*(x-h-tn)/Taylor.l;
				 }
				 else {return Taylor.rho*(x-h-tn)/Taylor.l;}
		 }
	 }
	
}