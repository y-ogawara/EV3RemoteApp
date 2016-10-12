package org.t_robop.y_ogawara.ev3remoteapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import org.t_robop.y_ogawara.ev3remoteapp.ev3.AndroidComm;
import org.t_robop.y_ogawara.ev3remoteapp.ev3.EV3Command;

import java.io.IOException;
import java.util.ArrayList;



public class MainActivity extends AppCompatActivity {

    //定数宣言
    final int STOP = 0;
    final int FRONT = 1;
    final int BACK = 2;
    final int RIGHT = 3;
    final int LEFT = 4;
    final int TEST = 5;

    //sendBluetooth用の変数
    int allTime;



    String macAddress;

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
        spinnerSetting();

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
    public void connect(View v) {
        BluetoothAdapter mBtAdapter = null;

        mBtAdapter = BluetoothAdapter.getDefaultAdapter();


        //00:16:53:44:69:AB   org.t_robop.y_ogawara.ev3remoteapp.ev3 青
        //00:16:53:44:59:C0   org.t_robop.y_ogawara.ev3remoteapp.ev3 緑
        //00:16:53:43:DE:A0   org.t_robop.y_ogawara.ev3remoteapp.ev3 灰色


        BluetoothDevice device = mBtAdapter.getRemoteDevice(macAddress);

        AndroidComm.getInstance().setDevice(device); // Set device

// Connect to EV3
        try {
            EV3Command.open();
            Toast.makeText(this, "接続成功！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "エラーです", Toast.LENGTH_LONG).show();
        }


    }
    //spinner設定用メソッド
    void spinnerSetting (){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        adapter.add("00:16:53:44:69:AB,ev3青");
        adapter.add("00:16:53:44:59:C0,eb3緑");
        adapter.add("00:16:53:43:DE:A0,ev3灰色");

        //スピナーの関連付け
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // アダプターを設定します
        spinner.setAdapter(adapter);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //スピナーを宣言
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得
                String item = (String) spinner.getSelectedItem();
                // , で文字を分割して保存
                String addressArray[] = item.split(",",0);
                //macAddressにaddressを入れる
                macAddress =addressArray[0];
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    //指定時間だけ画面の処理を止める
    void sendBluetooth (int num, final int event){
        num = num*1000;
        allTime = allTime + num;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            //遅延処理したい内容
            public void run() {
                try {
                    AndroidComm.mOutputStream.write(sendMessage(event));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, allTime);
    }

    //送信データの生成
    byte[] sendMessage(int num) {
        byte[] tele = new byte[21];
        tele[0] = (byte)19;
        tele[1] = (byte)0;
        tele[2] = (byte)0;
        tele[3] = (byte)0;
        tele[4] = (byte)0;
        tele[5] = (byte)0;
        tele[6] = (byte)0;

        //止まるとき
        if (num == 0) {   //Stop Motors at PortC & D
            tele[7] = (byte)0xA4;     //OUTPUT_POWER
            tele[8] = (byte)0;
            tele[9] = (byte)4;     //Motor ID = PortC
            tele[10] = (byte)0;     //Motor Power
            tele[11] = (byte)0xA6;    //OUTPUT_START
            tele[12] = (byte)0;
            tele[13] = (byte)4;     //Motor ID = PortC

            tele[14] = (byte)0xA4;     //OUTPUT_POWER
            tele[15] = (byte)0;
            tele[16] = (byte)8;     //Motor ID = PortD
            tele[17] = (byte)0;     //Motor Power
            tele[18] = (byte)0xA6;    //OUTPUT_START
            tele[19] = (byte)0;
            tele[20] = (byte)8;     //Motor ID = PortD
        }

        //進むとき
        if (num == 1) {    //Forward Motors at PortC & D
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)68;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)68;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)8;
        }
        //バック
        if (num == 2) {    //Backward Motors at PortC & D
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)40;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)40;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)9;
        }

        //右回転
        if (num == 3) {    //Turn Right = Forward Motor at PortC(Left) and Stop PortD(Right)
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)0;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)68;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)8;
        }
        //左回転
        if (num == 4) {    //Turn Left = Forward Motor at PortD(Right) and Stop PortC(Left)
            tele[7] = (byte)0xA4;
            tele[8] = (byte)0x00;
            tele[9] = (byte)4;
            tele[10] = (byte)68;
            tele[11] = (byte)0xA6;
            tele[12] = (byte)0;
            tele[13] = (byte)4;

            tele[14] = (byte)0xA4;
            tele[15] = (byte)0x00;
            tele[16] = (byte)8;
            tele[17] = (byte)0;
            tele[18] = (byte)0xA6;
            tele[19] = (byte)0;
            tele[20] = (byte)8;
        }
        //byte配列を返す
        return tele;
    }

}
                /*
                EV3を動かしたいときはこのコードを使ってください

                TODO:一度実行したらallTimeを初期化してください

                左側の変数 遅延時間
                右側の変数 なにをしたいか(中身はint)

                例
                sendBluetooth(0,FRONT);
                sendBluetooth(3,LEFT);
                sendBluetooth(2,FRONT);
                sendBluetooth(4,STOP);

                すぐに前進
                3病後に左回転開始
                2秒後に右回転開始
                4秒後にストップ

                */
