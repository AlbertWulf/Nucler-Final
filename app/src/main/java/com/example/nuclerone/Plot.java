package com.example.nuclerone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Plot extends AppCompatActivity {
   //private String dd;
    private LineChart lineChart;
    private int length;
    private double A11;
    private double A12;
    private double A21;
    private double A22;
    private double n0;
    private double c0;
    private double temp;
    private List<Double> nt = new ArrayList<>();
    private String rho;
    private String mt;
    private int arrsize;
    //private double[] nt = new double[10000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        lineChart = (LineChart) findViewById(R.id.lineChart);
        Intent intent = this.getIntent();
        rho = intent.getStringExtra("str_rho");
        Log.i("Plot",rho);
       double[] arrdata = new double[7];
       Bundle b = intent.getExtras();
       arrsize = intent.getIntExtra("len",0);
      // arrdata = b.getDoubleArray("arrdata");
        double[] ntt = new double[arrsize];
               ntt = b.getDoubleArray("nn");
               A11 = ntt[2];
               mt = String.valueOf(A11);
               //Log.i("Plot",mt);
               mt = String.valueOf(ntt[3]);
               //Log.i("Plot",mt);
       // for(int x=0;x<7;x++){
         //   Log.i("Plot",""+arrdata[x]);//必须一个个的读取
        //}
        //length = Math.floor(3/2);
        //A11 = (arrdata[0]-arrdata[1])/arrdata[3];
        //A12 = arrdata[2];
        //n0 = 1;
        //c0 = arrdata[1]/(arrdata[2]*arrdata[3]);
        //length = (int) Math.ceil((arrdata[6]-arrdata[4])/arrdata[5]);
        //for (int ii=0;ii < length;ii++) {
         //   nt.add(Math.exp((A11*n0+A12*c0)*ii/length*arrdata[6]));
            //nt.add(0.1*ii);
          //  Log.i("Plot","" + ii/length*Math.exp(A11*n0+A12*c0));
        //}
        for(int ii = 0;ii<arrsize;ii++) {
            nt.add(ntt[ii]);
        }

        //for (int kk = 0;kk < nt.size();kk++) {
          //  Log.i("Plot","" + nt.get(kk));

        //}


        //Log.i("Plot" ," "+nt.size());
        //dd = in
        // tent.getStringExtra("extra_data");
        //Toast.makeText(Plot.this,dd,Toast.LENGTH_SHORT).show();
       // List<String> xDataList = new ArrayList<>();// x轴数据源
        List<Entry> yDataList = new ArrayList<>();// y轴数据数据源
//给上面的X、Y轴数据源做假数据测试
        for (int jj = 0;jj<nt.size();jj++) {
            //yDataList.add(new Entry(jj, new Random().nextInt(300)));
            //yDataList.add(new Entry(jj,(float) 2.5));
            temp = nt.get(jj);
            yDataList.add(new Entry(jj,(float) temp));
        }

        LineDataSet dataSet = new LineDataSet(yDataList,"Netron Flux");
        dataSet.setColor(Color.parseColor("#ff5500"));
        dataSet.setCircleColor(Color.parseColor("#ff5500"));
        dataSet.setLineWidth(1f);

        XAxis xAxis = lineChart.getXAxis();
        YAxis leftyAxis = lineChart.getAxisLeft();
        YAxis rightyAxis = lineChart.getAxisRight();
        rightyAxis.setEnabled(false);
        leftyAxis.setEnabled(false);

        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //LineDataSet dataSet1 = new LineDataSet(yDataList,"flux");


        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();



        lineChart.animateY(2000);

        //for (int i = 0; i < 24; i++) {
            // x轴显示的数据
           // xDataList.add(i + ":00");
            //y轴生成float类型的随机数
            //float value = (float) (Math.random()) + 3;
            //yDataList.add(new Entry(value, i));
        //}

//显示图表,参数（ 上下文，图表对象， X轴数据，Y轴数据，图表标题，曲线图例名称，坐标点击弹出提示框中数字单位）
        //Plot.showChart(this, lineChart, xDataList, yDataList, "供热趋势图", "供热量/时间","kw/h");


    }

}
