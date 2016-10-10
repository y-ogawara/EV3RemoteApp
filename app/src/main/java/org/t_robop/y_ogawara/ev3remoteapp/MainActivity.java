package org.t_robop.y_ogawara.ev3remoteapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import static org.t_robop.y_ogawara.ev3remoteapp.R.id.stop;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button runBtn =(Button)findViewById(R.id.run);
        Button stopBtn =(Button)findViewById(stop);
        runBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);
    }

    //命令ボタン処理
    public void command(View v){
        Button runBtn =(Button)findViewById(R.id.run);
        Button stopBtn =(Button)findViewById(stop);
        switch (String.valueOf(v.getTag())){
            case "run":
                runBtn.setVisibility(View.INVISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
                break;
            case "stop":
                runBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.INVISIBLE);
                break;
            case "reset":
                break;
        }
    }
    //移動ボタン処理
    public void move(View v){
        switch (String.valueOf(v.getTag())){
            case "front":
                break;
            case "left":
                break;
            case "right":
                break;
            case "back":
                break;
        }
    }
    //接続ボタン処理
    public void connect(View v){

    }
}