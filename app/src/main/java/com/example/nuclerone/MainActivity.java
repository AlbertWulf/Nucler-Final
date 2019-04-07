package com.example.nuclerone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import Jama.Matrix;

public class MainActivity extends AppCompatActivity {
    private String etStr;
    private EditText etrho = null;
    private EditText etbeta = null;
    private EditText etlambda = null;
    private EditText etblambda = null;
    private EditText etbgtime = null;
    private EditText etstep = null;
    private EditText etendtime = null;
    public double drho;
    public double beta=0;//sum of betai
    public double[] betai = new double[6];
    public double[] lambdai = new double[6];
    public double dblambda;// usually 2*10^(-5)
    public double dbgtime;
    public double h;//time step
    public double dendtime;
    public double[] arrdata = new double[7];
    public double[] y = new double[7];//y matirx
    public double[] co = new double[7];//Ci(t)
    public double[][] G = new double[7][7];//coefficient matrix of Hansen Method Equation
    public double[][] F = new double[7][7];
    public double[][] yg = new double[7][7];//G*y should be a 1x7 vector,but jama think it's 7x7
    public double dtemp;
    public double n0 = 1;
    public double al;//alpha
    public double p;//store the rho(t)
    private int len;
    private int size;
    private double l;
    public double maxcha;//max value of eigen value of F
    public Object result;
    public String expr;
    public Bundle b  = new Bundle();
    public String temp;
    private String res;
    private String [] tempi = new String[6];//temp store the string array of betai
    private String [] tempj = new String[6];//temp store the string array of lambdai
    private List<Double> n = new ArrayList<>();
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("rhino");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btnplot = findViewById(R.id.btn_plot);
        etbeta = findViewById(R.id.et_beta);
        etrho = findViewById(R.id.et_rho);
        etlambda = findViewById(R.id.et_lambda);
        etblambda = findViewById(R.id.et_blambda);
        etbgtime = findViewById(R.id.et_bgtime);
        etstep = findViewById(R.id.et_step);
        etendtime = findViewById(R.id.et_endtime);


       // final double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};

        //Matrix A = new Matrix(array);

        btnplot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n.clear();//clear elements of n ,otherwise it will store the data of each print
                etStr = etrho.getText().toString();
                arrdata[0] = 0;
                //Double.parseDouble(etrho.getText().toString());
               // arrdata[1] = Double.parseDouble(etbeta.getText().toString());
                //arrdata[2] = Double.parseDouble(etlambda.getText().toString());
                arrdata[3] = Double.parseDouble(etblambda.getText().toString());
                arrdata[4] = Double.parseDouble(etbgtime.getText().toString());
                arrdata[5] = Double.parseDouble(etstep.getText().toString());
                arrdata[6] = Double.parseDouble(etendtime.getText().toString());
                tempi = etbeta.getText().toString().split(",");
                tempj = etlambda.getText().toString().split(",");
                h = arrdata[5];//time step
                l=  (arrdata[6]-arrdata[4])/arrdata[5];
                len = (int) l;
                beta = 0;
                for(int i =0;i<6;i++) {
                    betai[i] = Double.parseDouble(tempi[i]);
                    lambdai[i] = Double.parseDouble(tempj[i]);
                    beta = beta + betai[i];
                }
                y[0] = n0;//n0
                for(int jj = 0;jj<6;jj++) {
                    co[jj] = betai[jj]*n0/lambdai[jj]/arrdata[3];
                    y[jj+1] = co[jj];
                }

                expr = etrho.getText().toString();
                expr = expr.replaceAll("sin","Math.sin");
                expr = expr.replaceAll("pi","Math.PI");
                double mk = 0;//replace tt

                //主循环
                for(int tt = 0;tt<len;tt++) {
                    mk = (double) tt;
                    if(expr.indexOf("expr")==-1) {
                        p = Double.parseDouble(expr);
                    }
                    else {
                        engine.put("expr", mk/len*arrdata[6]);
                        //engine.put("y", 10);
                        try {
                            result = engine.eval(expr);
                            res = result.toString();
                            p = Double.parseDouble(res);
                            //if(tt == 100){
                            //  Log.i("res",res);
                            //Log.i("tt",String.valueOf(tt));
                            //Log.i("p",String.valueOf(p));
                            //Log.i("len",String.valueOf(len));
                            //Log.i("mk",String.valueOf(mk));
                            //Log.i("arr",String.valueOf(arrdata[6]));
                            //Log.i("all",String.valueOf(mk/len*arrdata[6]));
                            //}

                        }
                        catch (ScriptException e1)
                        {}
                    }
//                    engine.put("expr", mk/len*arrdata[6]);
//                    //engine.put("y", 10);
//                    try {
//                         result = engine.eval(expr);
//                         res = result.toString();
//                         p = Double.parseDouble(res);
//                        //if(tt == 100){
//                          //  Log.i("res",res);
//                            //Log.i("tt",String.valueOf(tt));
//                            //Log.i("p",String.valueOf(p));
//                            //Log.i("len",String.valueOf(len));
//                            //Log.i("mk",String.valueOf(mk));
//                            //Log.i("arr",String.valueOf(arrdata[6]));
//                            //Log.i("all",String.valueOf(mk/len*arrdata[6]));
//                        //}
//
//                    }
//                    catch (ScriptException e1)
//                    {}
                    //System.out.println(result);
                    al = (beta-p)/arrdata[3];
                    F[0][0] = (p-beta)/arrdata[3];

                    //assign value of matrix F
                    for (int ff = 1;ff<7;ff++) {
                        F[0][ff] = lambdai[ff-1];
                        F[ff][0] = betai[ff-1]/arrdata[3];
                        F[ff][ff] = -lambdai[ff-1];
                    }
                    Matrix A = new Matrix(F);
                    double[][] chara = A.eig().getD().getArray();
                    double[] a = new double[7];//store the diagonal elements of chara(3X3)
                    for(int aa = 0;aa<7;aa++) {
                        a[aa] = chara[aa][aa];
                    }
                    Arrays.sort(a);
                    maxcha = a[6];//max value of eigen value of Matrix F
                    G[0][0] = Math.exp(-al*h);
                    for(int ii=0;ii<6;ii++) {
                        G[0][ii+1] = lambdai[ii]*(Math.exp(maxcha*h)-Math.exp(-al*h))/(maxcha+al);
                        G[ii+1][0] = (Math.exp(maxcha*h)-Math.exp(-lambdai[ii]*h))/(maxcha+lambdai[ii])*betai[ii]/arrdata[3];
                        G[ii+1][ii+1] = Math.exp(-lambdai[ii]*h);
                    }
                    Matrix gg = new Matrix(G);
//                    if(tt == 100) {
//                        for(int ff = 0;ff<7;ff++) {
//                            Log.i("ch",String.valueOf(a[ff]));
//                         //   Log.i("FF",String.valueOf(F[ff][fff]));
//                        }
//
//                        Log.i("p",String.valueOf(p));
//                        Log.i("p",String.valueOf(al));
//                        Log.i("p",String.valueOf(h));
//                        Log.i("beta",String.valueOf(beta));
//                        Log.i("arr",String.valueOf(arrdata[3]));
//
//                        //Log.i("maxcha",String.valueOf(maxcha));
//                       // Log.i("yg",String.valueOf(G[0][2]));
//                        //for(int oo = 0;oo<7;oo++) {
//                          //  for (int ll =0;ll<7;ll++) {
//                                //Log.i("F",String.valueOf(F[oo][ll]));
//                            //    Log.i("G"+oo+ll,String.valueOf(G[oo][ll]));
//                            //}
//                        //}
//                    }

                    Matrix yy = new Matrix(y,1);
                    Matrix ynew  = gg.times(yy.transpose());
                    yg = ynew.getArray();
                    for(int sp = 0;sp<7;sp++) {
                        y[sp] = yg[sp][0];
                        //Log.i("sp",String.valueOf(sp));
                        //Log.i("yg",String.valueOf(yg[sp][0]));

                    }
                    if(tt==100) {
                        for(int oo = 0;oo<7;oo++) {
                            Log.i("y"+oo,String.valueOf(y[oo]));
                            Log.i("yg"+oo,String.valueOf(yg[oo][0]));
                        }
                    }
                    n.add(y[0]);
                }

                size = n.size();    //动态数组a的长度
                Double[] doubnt = (Double[])n.toArray(new Double[size]);//将动态数组a转换成Double数组
                double[] db = new double[size];
                for(int mm = 0;mm<size;mm++) {
                    db[mm] = doubnt[mm].doubleValue();
                }
                Log.i("HHD",String.valueOf(doubnt[2]));
                Log.i("HHD",String.valueOf(doubnt[3]));
                Log.i("Size",String.valueOf(size));
                Intent intent = new Intent(MainActivity.this,Plot.class);
                //b.putDoubleArray("nt",arrdata);
                b.putDoubleArray("nn",db);
                intent.putExtras(b);
                intent.putExtra("len",size);
                intent.putExtra("str_rho",etStr);
                //intent.putExtra("key",arrdata);
                startActivity(intent);


            }
        });

    }

}
