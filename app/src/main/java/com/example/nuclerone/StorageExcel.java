package com.example.nuclerone;



import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nuclerone.R;
import com.example.nuclerone.To_Excel.DemoBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import com.example.nuclerone.To_Excel.ExcelUtil;
import com.example.nuclerone.To_Excel.Neutron_Time;


public class StorageExcel extends Activity implements View.OnClickListener {

    private Button exportButton;
    private Button openButton;
    private TextView textView;

    private AlertDialog alertDialog;
    private AlertDialog mDialog;
    public double[] get_excel_N;
    public double bg_time;
    public double step;
    public double end_time;

    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    private int REQUEST_PERMISSION_CODE = 1000;


    private String filePath = Environment.getExternalStorageDirectory() + "/AndroidExcelDemo";

    private void requestPermission() {
        if (Build.VERSION.SDK_INT > 23) {
            if (ContextCompat.checkSelfPermission(StorageExcel.this,
                    permissions[0])
                    == PackageManager.PERMISSION_GRANTED) {
                //授予权限
                Log.i("requestPermission:", "用户之前已经授予了权限！");
            } else {
                //未获得权限
                Log.i("requestPermission:", "未获得权限，现在申请！");
                requestPermissions(permissions
                        , REQUEST_PERMISSION_CODE);
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_excel);
        requestPermission();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        //
        Intent intent = getIntent();
        get_excel_N = intent.getDoubleArrayExtra("To_excel");
        bg_time = intent.getDoubleExtra("bg_time",0);
        step = intent.getDoubleExtra("step",0.001);
        end_time = intent.getDoubleExtra("end_time",1);
        //Log.i("N",String.valueOf(get_excel_N[1]));
        //Log.i("bg",String.valueOf(bg_time));
        //Log.i("step",String.valueOf(step));
        //Log.i("end_time",String.valueOf(end_time));
        exportButton = findViewById(R.id.export_button);
        exportButton.setOnClickListener(StorageExcel.this);

        openButton = findViewById(R.id.open_button);
        openButton.setOnClickListener(StorageExcel.this);
        textView = findViewById(R.id.textView);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i("onPermissionsResult:", "权限" + permissions[0] + "申请成功");
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {
                Log.i("onPermissionsResult:", "用户拒绝了权限申请");
                AlertDialog.Builder builder = new AlertDialog.Builder(StorageExcel.this);
                builder.setTitle("permission")
                        .setMessage("点击允许才可以使用我们的app哦")
                        .setPositiveButton("去允许", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (alertDialog != null && alertDialog.isShowing()) {
                                    alertDialog.dismiss();
                                }
                                ActivityCompat.requestPermissions(StorageExcel.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        });
                alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
        }
    }

    private void showDialogTipUserRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.export_button:
                exportExcel(this);

                break;
            case R.id.open_button:
                openDir();
            default:
                break;
        }
    }

    private void openDir() {

        File file = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setDataAndType(Uri.fromFile(file), "file/*");
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "没有正确打开文件管理器", Toast.LENGTH_SHORT).show();
        }
    }


    private void exportExcel(Context context) {


        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }


        String excelFileName = "/neutron.xls";


        String[] title = {"Time", "Neutron"};
        String sheetName = "Time_Neutron";

        List<Neutron_Time> excelNeutron_TimeList = new ArrayList<>();
        double double_length =  (end_time-bg_time)/step;
        int length = (int) double_length;
        for(int i = 0;i<length;i++){
            excelNeutron_TimeList.add(new Neutron_Time(i*end_time/length,get_excel_N[i]));
        }
        //List<DemoBean> demoBeanList = new ArrayList<>();
        //DemoBean demoBean1 = new DemoBean("张三", 10, true);
        //DemoBean demoBean2 = new DemoBean("小红", 12, false);
        //DemoBean demoBean3 = new DemoBean("李四", 18, true);
        //DemoBean demoBean4 = new DemoBean("王香", 13, false);
        //demoBeanList.add(demoBean1);
        //demoBeanList.add(demoBean2);
        //demoBeanList.add(demoBean3);
        //demoBeanList.add(demoBean4);
        filePath = filePath + excelFileName;


        ExcelUtil.initExcel(filePath, sheetName, title);


        ExcelUtil.writeObjListToExcel(excelNeutron_TimeList, filePath, context);

        textView.setText("excel已导出至：" + filePath);

    }
}
