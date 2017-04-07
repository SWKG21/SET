package com.example.set;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MultiActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);

        Button btnCreate = (Button) findViewById(R.id.btnCreate);
        Button btnJoin = (Button) findViewById(R.id.btnJoin);

        btnCreate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MultiActivity.this, CreateActivity.class);
                startActivity(it);
            }
        });

        btnJoin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent it = new Intent(MultiActivity.this, JoinActivity.class);
                startActivity(it);
            }
        });
    }
}
