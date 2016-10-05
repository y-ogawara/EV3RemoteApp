package org.t_robop.y_ogawara.ev3remoteapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class debugIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_intent);
    }
    public void onClick(View v){
        Intent intent = new Intent();
        switch (String.valueOf(v.getTag())) {
            case "debug1":
                intent.setClass(this,MainActivity.class);
                startActivity(intent);
            break;

            case "debug2":
                intent.setClass(this,MainActivity.class);
                startActivity(intent);
                break;

            case "debug3":
                intent.setClass(this,RunListActivity.class);
                startActivity(intent);
                break;
        }
    }
}
