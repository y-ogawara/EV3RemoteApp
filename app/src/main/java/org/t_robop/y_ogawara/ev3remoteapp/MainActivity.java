package org.t_robop.y_ogawara.ev3remoteapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static org.t_robop.y_ogawara.ev3remoteapp.R.id.stop;

public class MainActivity extends AppCompatActivity {

    String str2;

    /**リスト関連**/
    //実行する処理のリスト
    ListView listRun;
    //実行する処理用のアダプター
    ArrayAdapter<String> adapterRun;
    //リスト編集やるためのArrayList
    ArrayList arrayListRun;
    //リストのどの要素をクリックしたかを知るためのグローバル変数
    int touchPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button runBtn =(Button)findViewById(R.id.run);
        Button stopBtn =(Button)findViewById(R.id.stop);
        runBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);

        //リストの関連付け
        listRun=(ListView)findViewById(R.id.list_run);

        /**list関連の初期設定**/
        //ArrayListの初期化
        arrayListRun=new ArrayList<String>();
        //アダプターの初期化
        adapterRun=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayListRun);

    }

    //命令ボタン処理
    public void command(View v){
        Button runBtn =(Button)findViewById(R.id.run);
        Button stopBtn =(Button)findViewById(R.id.stop);
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
                str2 = "前進";
                break;
            case "left":
                str2 = "左回転";
                break;
            case "right":
                str2 = "右回転";
                break;
            case "back":
                str2 = "後退";
                break;
        }
        //********************************************************************//
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);                       //ダイアログの生成
        //ダイアログのタイトル
        alertDlg.setTitle(str2);
        final NumberPicker np1 = new NumberPicker(MainActivity.this);                     //ダイアログ中の数字ロール生成
        np1.setMaxValue(10);                                                                //上限設定
        np1.setMinValue(1);                                                                 //下限設定
        //ナンバーピッカーの初期位置を指定できる
        //np1.setValue(0);
        alertDlg.setView(np1);
        alertDlg.setPositiveButton(                                                         //ボタン押された処理
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                        String str1 = String.valueOf(np1.getValue());
                        adapterRun.add(str2+" "+str1);
                        listRun.setAdapter(adapterRun);
                    }
                });
        alertDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                    }
                });

        // 表示
        alertDlg.create().show();


    }
    //接続ボタン処理
    public void connect(View v){

    }
}