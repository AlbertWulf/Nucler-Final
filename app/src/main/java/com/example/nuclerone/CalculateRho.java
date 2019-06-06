package com.example.nuclerone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nuclerone.R;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class CalculateRho extends AppCompatActivity {
    public double[] betai = {0.000266,0.001491,0.001316,0.002849,0.000896,0.000182};
    public double[] lambdai = {1/78.64,1/31.51,1/8.66,1/3.22,1/0.716,1/0.258};
    public double[] li = lambdai;
    public double beta = 0.007;
    public double l = 0.00002;
   // public double[] n = new double[1001];
    public double h = 0.001;
    public double t = 1;
    public double[] co = {1045.9,2349.1,569.8,458.7,32.1,2.3};
    public TextView tv;
    public String abpath;
    public EditText ed_rhoedtime;
    public EditText ed_rhostep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_rho);

        Button btn = (Button) findViewById(R.id.btn_open);
        //Button btnnew = findViewById(R.id.btn_new);
        ed_rhoedtime = findViewById(R.id.et_endtime);
        ed_rhostep = findViewById(R.id.et_step);
        //tv = findViewById(R.id.tv);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = ed_rhoedtime.getText().toString();
                String s2 = ed_rhostep.getText().toString();
                if(s1.equals("")==false && s2.equals("")==false) {
                    t = Double.parseDouble(ed_rhoedtime.getText().toString());
                    h = Double.parseDouble(ed_rhostep.getText().toString());
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, 1);
                }
                else{
                    new SweetAlertDialog(CalculateRho.this)
                            .setTitleText("参数缺失")
                            .setContentText("请输入全部参数！")
                            .show();
                }

            }        });


    }
    //
    //
    public double[] CalRho(double[] n,double h,double t)
    {
        int lenrho = (int) Math.floor(t/h);
        double[] calrho = new double[lenrho];
        for(int i =1;i<lenrho;i++){
            double now_time = h*i;
            calrho[i] = beta + l*(n[i]-n[i-1])/h/n[i]-l/n[i]*getsum2(now_time,co,li)-calint2(now_time,h,n,betai,li)/n[i];

        }
        return calrho;

    }
    public double getsum2(double t,double[] c0i,double[] li)
    {
        double p = 0;
        for(int i=0;i<6;i++)
        {
            p = p+li[i]*c0i[i]*Math.exp(-li[i]*t);
        }
        return p;
    }
    public double calint2(double t,double h,double[] n,double[] bi,double[] li)
    {
        double cal2 = 0;
        int kk = 0;
        int lenti = (int) Math.floor(t/h)+1;
        double[] ti = new double[lenti];
        for(int i=0;i<lenti;i++)
        {
            ti[i] = h*i;
        }
        for(int i =0;i<lenti;i++)
        {
            double temp = 0;
            for(int j=0;j<6;j++)
            {
                temp = temp + n[i]*bi[j]*li[j]*Math.exp(-li[j]*(t-ti[i]))*h;
            }
            cal2 = cal2 + temp;
        }
        return cal2;
    }
    public  double[] readFileOnLine(String filePath){//输入文件路径
        double[] tt = {1};
        try {
            List<Double> person=new ArrayList<>();
            Scanner input = new Scanner(new File(filePath));
            int num = 0;
            double temp = 0;
            while (input.hasNext()) {
                if(input.hasNextDouble()){
                    // temp += input.nextDouble();
                    person.add(input.nextDouble());
                    num ++;
                }
                else input.next();
            }
            input.close();
            int len = person.size();
            double[] n = new double[len];
            Log.i("len",String.valueOf(len));
            for(int i =0;i<len;i++) {
                n[i] = person.get(i);
            }
            return n;
        }
        catch (Exception e){
            Log.i("gg","dadjeif");
            return tt;
        }

    }
//AB





    @Override

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();


            if ("file".equalsIgnoreCase(uri.getScheme())){//使用第三方应用打开

               // Toast.makeText(this,uri.getPath()+"11111",Toast.LENGTH_SHORT).show();

                return;

            }

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后

                String path = getPath(this, uri);
                try {
                    List<Double> person=new ArrayList<>();
                    InputStream is = getContentResolver().openInputStream(uri);
                    Scanner input = new Scanner(is);
                    // BufferedReader reader=new BufferedReader(is);
                    //byte[] ch=new byte[1024];
                    //double[] td = new double[1024];
                    //int len=is.read(ch);
                    int num = 0;
                    double temp = 0;
                    while (input.hasNext()) {
                        if(input.hasNextDouble()){
                            // temp += input.nextDouble();
                            person.add(input.nextDouble());
                            num ++;
                        }
                        else input.next();
                    }
                    input.close();
                    int len = person.size();
                    double[] n = new double[len];
                    for(int i =0;i<len;i++) {
                        n[i] = person.get(i);
                    }
                    for(int i = 0;i<10;i++) {
                        System.out.println(n[i]);
                    }
                    double[] geteho = new double[len];
                    geteho = CalRho(n,h,t);
                    for(int i = 0;i<20;i++){
                        System.out.println(geteho[i]);
                    }
                    System.out.println(getsum2(0.002,co,li));
                    System.out.println(calint2(0.002,h,n,betai,li));

                    Intent intentrho = new Intent(CalculateRho.this,PLOTRHO.class);
                    intentrho.putExtra("rho",geteho);
                    intentrho.putExtra("tl",len);
                    intentrho.putExtra("h",h);
                    startActivity(intentrho);
                    // int length = 0;
                    //int temp=0;          //所有读取的内容都使用temp接收
                    //while((temp=is.read(ch))!=-1){    //当没有读取完时，继续读取
                    //  ch[length]=(byte)temp;

                    //String s = new String(ch,0,temp);


                    //double k = Double.parseDouble(s);
                    //Log.i("ss",s);
                    //Log.i("dd",String.valueOf(k));
                    // Log.i("ss",String.valueOf(k*3));
                    //}
                    //Log.i("inpsas",new String(ch,0,length));


                    //is.close();
                }
                catch (Exception e){
                    new SweetAlertDialog(this)
                            .setTitleText("参数缺失或错误")
                            .setContentText("请检查输入参数！")
                            .show();
                    Log.i("md","sk");
                }

                abpath ="/" +path.toString();
               // tv.setText(abpath);
                double[] sn;
                //ReadTxtFile(abpath);
                Toast.makeText(this,path.toString(),Toast.LENGTH_SHORT).show();
                //for(int i =0;i<5;i++){
                //  double nt = sn[i];
                //String str = String.valueOf(nt);
                //Log.i("i",str);
                //}

            } else {//4.4一下系统调用方法

                Toast.makeText(CalculateRho.this, getRealPathFromURI(uri)+"222222", Toast.LENGTH_SHORT).show();

            }

        }

    }



    public String getRealPathFromURI(Uri contentUri) {

        String res = null;

        String[] proj = { MediaStore.Images.Media.DATA };

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);

        if(null!=cursor&&cursor.moveToFirst()){;

            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            res = cursor.getString(column_index);

            cursor.close();

        }

        return res;

    }



    /**

     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使

     */

    @SuppressLint("NewApi")

    public String getPath(final Context context, final Uri uri) {



        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;



        // DocumentProvider

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider

            if (isExternalStorageDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);

                final String[] split = docId.split(":");

                final String type = split[0];



                if ("primary".equalsIgnoreCase(type)) {

                    return Environment.getExternalStorageDirectory() + "/" + split[1];

                }

            }

            // DownloadsProvider

            else if (isDownloadsDocument(uri)) {



                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(

                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));



                return getDataColumn(context, contentUri, null, null);

            }

            // MediaProvider

            else if (isMediaDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);

                final String[] split = docId.split(":");

                final String type = split[0];



                Uri contentUri = null;

                if ("image".equals(type)) {

                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

                } else if ("video".equals(type)) {

                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

                } else if ("audio".equals(type)) {

                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

                }



                final String selection = "_id=?";

                final String[] selectionArgs = new String[]{split[1]};



                return getDataColumn(context, contentUri, selection, selectionArgs);

            }

        }

        // MediaStore (and general)

        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            return getDataColumn(context, uri, null, null);

        }

        // File

        else if ("file".equalsIgnoreCase(uri.getScheme())) {

            return uri.getPath();

        }

        return null;

    }



    /**

     * Get the value of the data column for this Uri. This is useful for

     * MediaStore Uris, and other file-based ContentProviders.

     *

     * @param context       The context.

     * @param uri           The Uri to query.

     * @param selection     (Optional) Filter used in the query.

     * @param selectionArgs (Optional) Selection arguments used in the query.

     * @return The value of the _data column, which is typically a file path.

     */

    public String getDataColumn(Context context, Uri uri, String selection,

                                String[] selectionArgs) {



        Cursor cursor = null;

        final String column = "_data";

        final String[] projection = {column};



        try {

            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,

                    null);

            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);

                return cursor.getString(column_index);

            }

        } finally {

            if (cursor != null)

                cursor.close();

        }

        return null;

    }



    /**

     * @param uri The Uri to check.

     * @return Whether the Uri authority is ExternalStorageProvider.

     */

    public boolean isExternalStorageDocument(Uri uri) {

        return "com.android.externalstorage.documents".equals(uri.getAuthority());

    }



    /**

     * @param uri The Uri to check.

     * @return Whether the Uri authority is DownloadsProvider.

     */

    public boolean isDownloadsDocument(Uri uri) {

        return "com.android.providers.downloads.documents".equals(uri.getAuthority());

    }



    /**

     * @param uri The Uri to check.

     * @return Whether the Uri authority is MediaProvider.

     */

    public boolean isMediaDocument(Uri uri) {

        return "com.android.providers.media.documents".equals(uri.getAuthority());

    }
    private void readFile(String filePath){
        if(filePath == null) return;

        File file = new File(filePath);
        if(file.isDirectory()){
            Log.i("id", filePath + " is directory");
            return;
        }else{
            try {
                InputStream is = new FileInputStream(file);
                if(is != null){
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);

                    String line;
                    while((line = br.readLine()) != null){
                        Log.i("line", line);
                    }
                }
            } catch (FileNotFoundException e) {
                Log.i("gg", filePath + " doesn't found!");
            }catch (IOException e) {
                Log.i("dd", filePath + " read exception, " + e.getMessage());
            }
        }
    }
    //CC

    public static String ReadTxtFile(String strFilePath)

    {

        String path = strFilePath;

        List<String> newList=new ArrayList<String>();

        //打开文件

        File file = new File(path);

        //如果path是传递过来的参数，可以做一个非目录的判断

        if (file.isDirectory())

        {

            Log.d("TestFile", "The File doesn't not exist.");

        }

        else

        {

            try {

                InputStream instream = new FileInputStream(file);

                if (instream != null)

                {

                    InputStreamReader inputreader = new InputStreamReader(instream);

                    BufferedReader buffreader = new BufferedReader(inputreader);

                    String line;

                    //分行读取

                    while (( line = buffreader.readLine()) != null) {

                        newList.add(line+"\n");

                    }

                    instream.close();

                }

            }

            catch (java.io.FileNotFoundException e)

            {

                Log.d("TestFile", "The File doesn't not exist.");

            }

            catch (IOException e)

            {

                Log.d("TestFile", e.getMessage());

            }

        }

        return strFilePath;

    }

    //CC


}

