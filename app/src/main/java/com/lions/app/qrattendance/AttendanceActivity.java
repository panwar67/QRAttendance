package com.lions.app.qrattendance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity implements QRCodeReaderView.OnQRCodeReadListener, GoogleApiClient.OnConnectionFailedListener{

    private QRCodeReaderView qrCodeReaderView;
    String DOWN_URL = "http://www.4liongroup.com/attendance/submitpresent.php";
    String DOWN_URL_2 = "http://www.4liongroup.com/attendance/checkout.php";
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    int i=0;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        qrCodeReaderView = (QRCodeReaderView) findViewById(R.id.qrreader);
        qrCodeReaderView.setOnQRCodeReadListener(this);

        // Use this function to enable/disable decoding
        qrCodeReaderView.setQRDecodingEnabled(true);

        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView.setAutofocusInterval(2000L);

        // Use this function to enable/disable Torch
        qrCodeReaderView.setTorchEnabled(true);

        // Use this function to set front camera preview
       // qrCodeReaderView.setFrontCamera();

        // Use this function to set back camera preview
        qrCodeReaderView.setBackCamera();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                mAuth.signOut();

                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                startActivity(new Intent(AttendanceActivity.this, Login.class));
                                finish();
                            }
                        });

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }







    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Toast.makeText(getApplicationContext(),text+"",Toast.LENGTH_LONG).show();
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
        Log_In_User(text+""+mAuth.getCurrentUser().getDisplayName()+"", day, month, formattedDate, year, time, dom, dayformatted);
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
                            Toast.makeText(AttendanceActivity.this, "Attendance Submitted Successfully", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(AttendanceActivity.this, Success.class).putExtra("timestamp",timestamp));

                            finish();


                        }else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(AttendanceActivity.this, "Error Occured ! Try again Later", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(AttendanceActivity.this, "Error In Connectivity ", Toast.LENGTH_LONG).show();
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

        progressDialog = new ProgressDialog(AttendanceActivity.this);
        progressDialog.setMessage("Submitting Time....");
        progressDialog.show();

        return true;
    }


    public boolean Log_Out_User(final String name,
                               final String day, final String month,
                               final String timestamp, final String year,
                               final String time)
    {

        StringRequest stringRequest4 = new StringRequest(Request.Method.POST, DOWN_URL_2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.d("checkout_response",""+s);
                        progressDialog.dismiss();

                        if(s.contains("uploaded"))
                        {
                            Toast.makeText(AttendanceActivity.this, "Attendance Submitted Successfully", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(AttendanceActivity.this, Success.class).putExtra("timestamp",timestamp));

                            finish();


                        }else
                        {
                            progressDialog.dismiss();
                            Toast.makeText(AttendanceActivity.this, "Error Occured ! Try again Later", Toast.LENGTH_LONG).show();

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
                        Toast.makeText(AttendanceActivity.this, "Error In Connectivity ", Toast.LENGTH_LONG).show();
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


                //returning parameters
                return Keyvalue;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue4 = Volley.newRequestQueue(this);
        stringRequest4.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //Adding request to the queue
        requestQueue4.add(stringRequest4);

        progressDialog = new ProgressDialog(AttendanceActivity.this);
        progressDialog.setMessage("Submitting Time....");
        progressDialog.show();

        return true;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
