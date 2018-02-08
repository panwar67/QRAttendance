package com.lions.app.qrattendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Success extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        Intent intent = getIntent();
        TextView timestamp = (TextView)findViewById(R.id.timestamp);
        timestamp.setText(intent.getStringExtra("timestamp"));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Success.this,Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
}
