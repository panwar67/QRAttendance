package com.lions.app.qrattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScanAndMark extends AppCompatActivity  implements QRCodeReaderView.OnQRCodeReadListener{

    QRCodeReaderView qrCodeReaderView;
    private ProgressDialog progressDialog;
    String DOWN_URL = "http://www.4liongroup.com/attendance/submitpresent.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_and_mark);

        qrCodeReaderView = (QRCodeReaderView)findViewById(R.id.scanandmarkreader);

        qrCodeReaderView.setOnQRCodeReadListener(this);

        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
       // qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();



    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {

        qrCodeReaderView.setQRDecodingEnabled(false);
        qrCodeReaderView.stopCamera();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        String dayformatted = df2.format(date.getTime());
        String formattedDate = df.format(date.getTime());
        String dom = date.getDate()+"";
        String day = date.getDay() + "";
        String month = date.getMonth() + "";
        String year = date.getYear() + "";
        String time = date.getHours() + " : " + date.getMinutes();

        Log_In_User(text, day, month, formattedDate, year, time, dom, dayformatted);


    }

    public boolean Log_In_User(final String name,
                               final String day, final String month,
                               final String timestamp, final String year,
                               final String time, final String dom, final String dayformatted)
    {




        StringRequest stringRequest4 = new StringRequest(Request.Method.POST, DOWN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {

                        progressDialog.dismiss();

                        if(s.contains("uploaded"))
                        {
                            Toast.makeText(ScanAndMark.this, "Attendance Submitted Successfully", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(ScanAndMark.this, Success.class).putExtra("timestamp",timestamp));

                            finish();


                        }else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(ScanAndMark.this, "Error Occured ! Try again Later", Toast.LENGTH_LONG).show();

                        }

                        //Showing toast message of the response
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        //  loading.dismiss();
                        progressDialog.dismiss();
                        //Showing toast
                        Toast.makeText(ScanAndMark.this, "Error In Connectivity ", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                HashMap<String,String> Keyvalue = new HashMap<String,String>();
                Keyvalue.put("name",name);

                Keyvalue.put("day",day);
                Keyvalue.put("month",month);
                Keyvalue.put("time",time);
                Keyvalue.put("timestamp",timestamp);
                Keyvalue.put("year",year);
                Keyvalue.put("dom",dom);
                Keyvalue.put("dayformatted",dayformatted);
                Log.d("parameters",Keyvalue.toString());
                //returning parameters
                return Keyvalue;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue4 = Volley.newRequestQueue(this);
        stringRequest4.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to the queue
        requestQueue4.add(stringRequest4);

        progressDialog = new ProgressDialog(ScanAndMark.this);
        progressDialog.setMessage("Submitting Time....");
        progressDialog.show();

        return true;
    }




}
