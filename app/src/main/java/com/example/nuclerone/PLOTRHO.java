package com.example.nuclerone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PLOTRHO extends AppCompatActivity {
    //private String dd;
    private LineChart lineChart;
    private int length;
    private double A11;
    private double A12;
    private double A21;
    private double A22;
    private double n0;
    private double c0;
    private double st;//记录传参步长
    private double temp;
    private List<Double> nt = new ArrayList<>();
    private String rho;
    private String mt;
    private int arrsize;
    //private double[] nt = new double[10000];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plotrho);
        lineChart = (LineChart) findViewById(R.id.lineChart);

        Intent intent = this.getIntent();
        int tl = intent.getIntExtra("tl",1);
         tl = tl-1;
         st = 0.001;
         st = intent.getDoubleExtra("h",0.001);
        double[] truerho = new double[tl];
        truerho = intent.getDoubleArrayExtra("rho");
        //rho = intent.getStringExtra("str_rho");
        //st = intent.getDoubleExtra("step",1);
        //Log.i("Plot",rho);
        //double[] arrdata = new double[7];
        //Bundle b = intent.getExtras();
        //arrsize = intent.getIntExtra("len",0);
        // arrdata = b.getDoubleArray("arrdata");
        //double[] ntt = new double[arrsize];
        //ntt = b.getDoubleArray("nn");
        //A11 = ntt[2];
        //mt = String.valueOf(A11);
        //Log.i("Plot",mt);
        //mt = String.valueOf(ntt[3]);

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
        for(int ii = 0;ii<tl;ii++) {
            nt.add(truerho[ii]);
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
        float xvalues = 0;
        for (int jj = 0;jj<nt.size();jj++) {
            //yDataList.add(new Entry(jj, new Random().nextInt(300)));
            //yDataList.add(new Entry(jj,(float) 2.5));
            temp = nt.get(jj);
            xvalues = xvalues+(float) st;
            //   xDataList.add(jj/1000+"");
            yDataList.add(new Entry(xvalues,(float) temp));
        }

        //设置图例
        Legend l = lineChart.getLegend();//图例
        l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART_INSIDE);//设置图例的位置
        l.setTextSize(10f);//设置文字大小
        l.setForm(Legend.LegendForm.CIRCLE);//正方形，圆形或线
        l.setFormSize(10f); // 设置Form的大小
        l.setWordWrapEnabled(true);//是否支持自动换行 目前只支持BelowChartLeft, BelowChartRight, BelowChartCenter
        l.setFormLineWidth(10f);//设置Form的宽度

        //END

        //设置MarkView

        MyMarkerView myMarkerView = new MyMarkerView(this);

        myMarkerView.setChartView(lineChart);

        lineChart.setMarker(myMarkerView);

        //END



        LineDataSet dataSet = new LineDataSet(yDataList,"Netron Flux");
        dataSet.setColor(Color.parseColor("#ff5500"));
        dataSet.setCircleColor(Color.parseColor("#ff5500"));
        dataSet.setLineWidth(1f);
        //设置运行曲线平滑#禁用曲线平滑防止过拟合
        //dataSet.setCubicIntensity(0.6f);
        //dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        //
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
    /**
     *自定义MyMarkerView
     */



    public class MyMarkerView extends MarkerView {



        private TextView tvContent;



        public MyMarkerView(Context context) {

            super(context, R.layout.mark_view);



            tvContent = (TextView) findViewById(R.id.tvContent);

        }



        // callbacks everytime the MarkerView is redrawn, can be used to update the

        // content (user-interface) 每次 MarkerView 重绘此方法都会被调用，并为您提供更新它显示的内容的机会

        @Override

        public void refreshContent(Entry e, Highlight highlight) {

            //这里就设置你想显示到makerview上的数据，Entry可以得到X、Y轴坐标，也可以e.getData()获取其他你设置的数据
            //Utils.formatNumber方法中的digitCount:用于显示在MarkView中的数字小数点后有效位数，但是小数点是逗号形式

            tvContent.setText("t:" + Utils.formatNumber(e.getX(),4,true)+",n："+Utils.formatNumber(e.getY(), 4, true));

            super.refreshContent(e, highlight);

        }





        /*

         * offset 是以點到的那個點作為 (0,0) 中心然後往右下角畫出來 该方法是让markerview现实到坐标的上方

         * 所以如果要顯示在點的上方

         * X=寬度的一半，負數

         * Y=高度的負數

         */

        @Override

        public MPPointF getOffset() {

            // Log.e("ddd", "width:" + (-(getWidth() / 2)) + "height:" + (-getHeight()));

            return new MPPointF(-(getWidth() / 2), -getHeight());

        }

    }




}
