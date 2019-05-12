package com.example.nuclerone;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
//import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.getbase.floatingactionbutton.FloatingActionButton;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.bumptech.glide.Glide;
import com.example.nuclerone.TaylorMethod.Taylor;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import Jama.Matrix;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    public int Method_choose = 0;
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
    private DrawerLayout mDrawerLayout;
    //以下设置U235的缓发中子参数
    public String U_3rho = "0.003";
    public String U_3beta = "0.000266,0.001491,0.001316,0.002849,0.000896,0.000182";
    public String U_3Lambda = "0.0127,0.0317,0.115,0.311,1.40,3.87";
    public String U_3Blambda = "0.00002";
    //U235 END
    //以下设置U238的缓发中子参数
    public String U_8rho = "0.003";
    public String U_8beta = "0.000266,0.001491,0.001316,0.002849,0.000896,0.000182";
    public String U_8Lambda = "0.0127,0.0317,0.115,0.311,1.40,3.87";
    public String U_8Blambda = "0.00002";
    //U238END
    //以下设置Pu239的缓发中子参数
    public String P_9rho = "0.003";
    public String P_9beta = "0.000266,0.001491,0.001316,0.002849,0.000896,0.000182";
    public String P_9Lambda = "0.0127,0.0317,0.115,0.311,1.40,3.87";
    public String P_9Blambda = "0.00002";
    //Pu239END
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //(修)修改顶部状态栏的融合
        if (Build.VERSION.SDK_INT >= 21) {

            View decorView = getWindow().getDecorView();

            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        //(修)END
        setContentView(R.layout.activity_main);
        //Button btnplot = findViewById(R.id.btn_plot);
        final FloatingActionsMenu f1_btn_menu =  findViewById(R.id.fab_menu);
        final FloatingActionButton fl_btn_hansen = findViewById(R.id.fab_1);
        final FloatingActionButton f1_btn_taylor =  findViewById(R.id.fab_2);

        etbeta = findViewById(R.id.et_beta);
        etrho = findViewById(R.id.et_rho);
        etlambda = findViewById(R.id.et_lambda);
        etblambda = findViewById(R.id.et_blambda);
        etbgtime = findViewById(R.id.et_bgtime);
        etstep = findViewById(R.id.et_step);
        etendtime = findViewById(R.id.et_endtime);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        //(BING)以下记载必应每日一图作为背景
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        bingPicImg = findViewById(R.id.bin_pic_img);
        String bingPic = prefs.getString("bing_pic",null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }
        //(BING)END
        //（侧）以下处理侧边栏的点击事件
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setCheckedItem(R.id.nav_call);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   case R.id.nav_call:
                     etrho.setText(U_3rho);
                     etbeta.setText(U_3beta);
                     etlambda.setText(U_3Lambda);
                     etblambda.setText(U_3Blambda);
                     mDrawerLayout.closeDrawers();
                     break;
               }
               switch (item.getItemId()){
                   case R.id.nav_friends:
                       etrho.setText(U_8rho);
                       etbeta.setText(U_8beta);
                       etlambda.setText(U_8Lambda);
                       etblambda.setText(U_8Blambda);
                       mDrawerLayout.closeDrawers();
                       break;
               }
               switch (item.getItemId()){
                   case R.id.nav_location:
                       etrho.setText(P_9rho);
                       etbeta.setText(P_9beta);
                       etlambda.setText(P_9Lambda);
                       etblambda.setText(P_9Blambda);
                       mDrawerLayout.closeDrawers();
                       break;
               }
                switch (item.getItemId()){
                    case R.id.nav_mail:
                        Method_choose = 0;
                        mDrawerLayout.closeDrawers();
                        break;
                }
                switch (item.getItemId()){
                    case R.id.nav_task:{
                        Method_choose = 1;
                        mDrawerLayout.closeDrawers();
                    }
                }

                return true;
            }
        });
        //(侧）END
        //(Swipe)以下处理下拉刷新更新必应图片背景的事件
        //swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorAccent),getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorAccent));
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBingPic();
            }
        });
        //(Swipe)END
       // final double[][] array = {{1.,2.,3},{4.,5.,6.},{7.,8.,10.}};

        //Matrix A = new Matrix(array);
        f1_btn_taylor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Method_choose = 1;
                try {
                    domain(Method_choose);
                }
                catch (Exception e1){
                    Toast.makeText(MainActivity.this,"Invalid Input,Please Check!",Toast.LENGTH_SHORT).show();

                }

            }
        });

        fl_btn_hansen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Method_choose = 0;
                try{
                    domain(Method_choose);
                }
                catch (Exception e2){
                    Toast.makeText(MainActivity.this,"Invalid Input,Please Check!",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void domain(int Me_Choose) throws Exception{
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
        //for two lines below ,it's disturbing when users want to input sin or pi because java needs Math.sin and Math.PI,so
        //replace here and you just need to input sin and pi
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

                }
                catch (ScriptException e1)
                {}
            }
//
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
            Matrix yy = new Matrix(y,1);
            Matrix ynew  = gg.times(yy.transpose());
            yg = ynew.getArray();
            for(int sp = 0;sp<7;sp++) {
                y[sp] = yg[sp][0];
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
        if(Method_choose == 1){
            String y = expr.replaceAll("Math.sin","sin");
            Taylor taylor = new Taylor(y,betai,lambdai,arrdata[3],arrdata[4],arrdata[6],arrdata[5]);
            db = taylor.calN();

        }


        // Log.i("HHD",String.valueOf(doubnt[2]));
        //Log.i("HHD",String.valueOf(doubnt[3]));
        //Log.i("Size",String.valueOf(size));
        Intent intent = new Intent(MainActivity.this,Plot.class);
        //b.putDoubleArray("nt",arrdata);
        b.putDoubleArray("nn",db);
        intent.putExtras(b);
        intent.putExtra("len",size);
        intent.putExtra("str_rho",etStr);
        intent.putExtra("step",arrdata[5]);
        //intent.putExtra("key",arrdata);
        startActivity(intent);
    }
    /**
     *自定义MyMarkerView
     */



    /**
     * 加载必应每日一图
     */
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(MainActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
        swipeRefresh.setRefreshing(false);
    }

}
