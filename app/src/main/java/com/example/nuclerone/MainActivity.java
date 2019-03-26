package com.example.nuclerone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
    public double dbeta;
    public double dlambda;
    public double dblambda;
    public double dbgtime;
    public double dstep;
    public double dendtime;
    public double[] arrdata = new double[7];
    public double dtemp;
    public Bundle b  = new Bundle();


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
        btnplot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etStr = etbeta.getText().toString();
                arrdata[0] = Double.parseDouble(etrho.getText().toString());
                arrdata[1] = Double.parseDouble(etbeta.getText().toString());
                arrdata[2] = Double.parseDouble(etlambda.getText().toString());
                arrdata[3] = Double.parseDouble(etblambda.getText().toString());
                arrdata[4] = Double.parseDouble(etbgtime.getText().toString());
                arrdata[5] = Double.parseDouble(etstep.getText().toString());
                arrdata[6] = Double.parseDouble(etendtime.getText().toString());

                //Toast.makeText(MainActivity.this,etStr,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,Plot.class);
                b.putDoubleArray("arrdata",arrdata);
                intent.putExtras(b);
                //intent.putExtra("key",arrdata);
                startActivity(intent);

            }
        });

    }
}
