package com.example.nuclerone.TaylorMethod;


import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import org.apache.commons.math3.stat.descriptive.moment.GeometricMean;

import org.apache.commons.math3.stat.descriptive.moment.Kurtosis;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

import org.apache.commons.math3.stat.descriptive.moment.Skewness;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import org.apache.commons.math3.stat.descriptive.moment.Variance;

import org.apache.commons.math3.stat.descriptive.rank.Max;

import org.apache.commons.math3.stat.descriptive.rank.Min;

import org.apache.commons.math3.stat.descriptive.rank.Percentile;

import org.apache.commons.math3.stat.descriptive.summary.Product;

import org.apache.commons.math3.stat.descriptive.summary.Sum;

import org.apache.commons.math3.stat.descriptive.summary.SumOfSquares;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

/**

 * 简单使用commons Math方法

 * @author Albert Wu

 */

public class Taylor {
    public static String y;
    public static int k = 0;
    public static double[] betai;
    //= {0.000266,0.001491,0.001316,0.002849,0.000896,0.000182};
    public static double rho ;
    public static double beta ;
    //= 0.007;
    public static double[] li ;
    //= {1/78.64,1/31.51,1/8.66,1/3.22,1/0.716,1/0.258};
    public static double l ;
    //= 2*Math.pow(10, -5);
    public static double h ;
    public static double bt ;
    public static double et ;
    public static double n0 ;
    public static double[] C0 = new double[6];
    public static double[] B = new double[7];
    public static double[] Nt = new double[7];
    public static double[] n;
    public static double[][] F = new double[7][7];
    public static double[][] temp_FB = new double[7][7];//尽管矩阵相乘得到一个一维矩阵，但是依然需要二维矩阵存放
    public static double[] ne;//new indeed
    public static double sin_a;
    public static double sin_b;
    public static double A;//线性反应性系数
    public List<Double> res = new ArrayList<>();

    public Taylor(String y1,double[] betai1,double[] li1,double l1,double bt1,double et1,double h1 ) {
        this.y = y1;
        this.betai = betai1;
        this.li = li1;
        this.l = l1;
        this.bt = bt1;
        this.et = et1;
        this.h= h1;
        this.beta = this.sumbeta(betai1);
    }

    public double[] calN() {
        n0 = 1;
        //以下借助于字符串包含判断输入的反应性
        if(y.indexOf("sin")!=-1) {
            k = 1;//正弦
            sin_a = Double.parseDouble(y.substring(0,y.indexOf("*")));
            sin_b = Double.parseDouble(y.substring(y.indexOf("(")+1,y.indexOf("*e") ));

        }
        else if(y.indexOf("expr")!=-1) {
            k=-1;//线性
            A = Double.parseDouble(y.substring(0,y.indexOf("*")));
        }


        else {
            k=0;
            rho = Double.parseDouble(y);
        }

        System.out.println(sin_a);
        System.out.println(sin_b);
        System.out.println(A);

        //Taylor tt = new Taylor();
        double d_length =  (et-bt)/h;
        int cir = 1;
        int int_length = (int) d_length;
        n = new double[int_length];
        for(int sk = 0;sk<int_length;sk++){
            n[sk] = 0;
        }
        for(int c=0;c<6;c++) {
            C0[c] = betai[c]*n0/l/li[c];
        }
        //main
        for(int t = 0;t<int_length;t++) {
            double rt = t*et/int_length;//real time
            n[t] = n0;
            cir++;
            F1 f1 = new F1(h,rt);
            F2 f2 = new F2(h,rt);

            F[0][0] = 1-f1.calIn()-f2.calIn()*(this.Rho(rt)-beta)/l;

            //(1)计算F矩阵以及B矩阵部分值
            for(int kk=0;kk<6;kk++) {
                F[0][1+kk] = 1-f2.calIn()*li[kk];
                B[kk+1] = Math.exp(-li[kk]*h)*C0[kk];
            }
            //(1)END

            //（2）计算系数矩阵剩余部分值

            for(int xx = 1;xx<7;xx++) {
                G1 g1 = new G1(h,xx-1);
                G2 g2 = new G2(h,xx-1);
                F[xx][0] = -betai[xx-1]*(g1.calIn()+g2.calIn()*(this.Rho(rt)-beta)/l)/l;
                for(int yy = 1;yy<7;yy++) {
                    F[xx][yy] = Math.pow(0,Math.abs(yy-xx))-betai[xx-1]*g2.calIn()*li[yy-1]/l;
                }
            }
            //(2)END
            //(3)计算每次得到的C0数组元素和
            double sum = 0;
            for(int ss = 0;ss<6;ss++) {
                sum = sum + C0[ss];
            }
            //(3)END

            B[0] = n0 + sum;
            RealMatrix M_F = new Array2DRowRealMatrix(F);
            RealMatrix M_B = new Array2DRowRealMatrix(B);
            RealMatrix inM_F = new LUDecomposition(M_F).getSolver().getInverse();
            RealMatrix mulF_B = inM_F.multiply(M_B);
            temp_FB = mulF_B.getData();

            //
            n0 = temp_FB[0][0];
            for(int bb = 0;bb<6;bb++) {
                C0[bb] = temp_FB[bb+1][0];
            }
            //


            if(t==-1) {
                System.out.println("F fllows");
                for(int ls=0;ls<7;ls++) {
                    for(int ld=0;ld<7;ld++) {
                        System.out.println(F[ls][ld]);
                    }
                }
                System.out.println("F END");
                System.out.println("B follows");
                for(int ls = 0;ls<7;ls++) {
                    System.out.println(B[ls]);
                }
                System.out.println("B END");

            }


        }


        UnivariateFunction func = null;
        UnivariateIntegrator integrator = null;
        func = this.new MyFunction();
        integrator =  new SimpsonIntegrator();
        //F2 ff = new F2(1,0);
        F2 f2 = new F2(h,0);
        F1 f1 = new F1(h,0);
        System.out.println(f1.calIn());
        System.out.println(f2.calIn());
        //System.out.println(integrator.integrate(1000, func, 2, 3));
        //for(int nn = 0;nn<int_length;nn++) {
        //  System.out.println(n[nn]);
        //}

        return n;
    }
    public  double Rho(double t) {
        //return 0.003;
        if(k==1) {
            return sin_a*Math.sin(sin_b*t);
        }
        else if(k==-1) {
            return A*t;
        }
        else {
            return rho;
        }

    }
    public double sumbeta(double[] b) {
        double ini = 0;
        for(int i = 0;i<5;i++) {
            ini = ini + b[i];
        }
        return ini;
    }
    public class MyFunction implements UnivariateFunction {
        @Override
        public double value(double x) {
            double y = 3;
            return y;
        }
    }

}