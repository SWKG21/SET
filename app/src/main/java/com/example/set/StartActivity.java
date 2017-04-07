package com.example.set;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


/**
 * Created by wang on 3/12/17.
 */

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Button btnSingle = (Button) findViewById(R.id.btnSingle);
        Button btnMulti = (Button) findViewById(R.id.btnMulti);
        Button btnRule = (Button) findViewById(R.id.btnRule);

        btnSingle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(StartActivity.this, ModifiedSingleActivity.class);
                startActivity(it);
            }
        });

        btnRule.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(StartActivity.this, RuleActivity.class);
                startActivity(it);
            }
        });

        btnMulti.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent it = new Intent(StartActivity.this, MultiActivity.class);
                startActivity(it);
            }
        });


    }
}
