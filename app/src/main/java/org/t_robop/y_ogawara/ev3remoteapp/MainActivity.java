package org.t_robop.y_ogawara.ev3remoteapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
    static int allTime;

    String macAddress;

    String action;

    /**
     * リスト関連
     **/
    //実行する処理のリスト
    static ListView listRun;
    //実行する処理用のアダプター
    static ArrayAdapter<String> adapterRun;
    //リスト編集やるためのArrayList
    static ArrayList arrayListRun;
    //リストのどの要素をクリックしたかを知るためのグローバル変数
    int touchPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinnerSetting();

        //リストの関連付け
        listRun = (ListView) findViewById(R.id.list_run);

        /**list関連の初期設定**/
        //ArrayListの初期化
        arrayListRun = new ArrayList<String>();
        //アダプターの初期化
        adapterRun = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListRun);

        setListClick();

    }

    //命令ボタン処理
    public void command(View v) {
        Button com = (Button) findViewById(R.id.com);
        if (String.valueOf(com.getText()).equals("実行")){
            if (arrayListRun.size() != 0) {
                //ボタンのテキストを「停止」に変更
                com.setText("停止");
                //ボタンのbackgroundを赤に変更
                com.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_stop));
                //上から処理開始
                TheRunningMachine();
            }
        }else{
            //停止処理
            sendBluetooth(1, STOP);
            //実行されなかったリストの要素を消す
            arrayListRun.clear();
            //華麗に復活
            Iwillbeback();
        }
    }

    //リセットボタンの処理
    public void reset(View v) {
        //リスト全消し
        listResetMethod();
    }

    //
//    public void comman(View v){
//        Button runBtn =(Button)findViewById(R.id.run);
//        Button stopBtn =(Button)findViewById(R.id.stop);
//        switch (String.valueOf(v.getTag())){
//            case "run":
//                if(arrayListRun.size()!=0) {
//                    runBtn.setVisibility(View.INVISIBLE);
//                    stopBtn.setVisibility(View.VISIBLE);
//                    TheRunningMachine();
//                }
//                break;
//            case "stop":
//                runBtn.setVisibility(View.VISIBLE);
//                stopBtn.setVisibility(View.INVISIBLE);
//                //停止処理
//                sendBluetooth(1,STOP);
//                break;
//            case "reset":
//                //リスト全消し
//                listResetMethod();
//                break;
//        }
//    }
    //移動ボタン処理
    public void move(View v) {
        switch (String.valueOf(v.getTag())) {
            case "front":
                action = "前進";
                break;
            case "left":
                action = "左折";
                break;
            case "right":
                action = "右折";
                break;
            case "back":
                action = "後退";
                break;
        }
        //********************************************************************//
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);                       //ダイアログの生成
        //ダイアログのタイトル
        alertDlg.setTitle(action);
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
                        String second = String.valueOf(np1.getValue());
                        arrayListRun.add(action + "【" + String.valueOf(second) + "秒】");
                        setList();
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

        // キーボードを強制的に隠せてない
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }


    }

    //spinner設定用メソッド
    void spinnerSetting() {
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
                String addressArray[] = item.split(",", 0);
                //macAddressにaddressを入れる
                macAddress = addressArray[0];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    //指定時間だけ画面の処理を止める
    public void sendBluetooth(int num, final int event) {
        num = num * 1000;
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
                //どんどん消されていくのを防ぐために停止処理の時は要素を消さない
                if(event!=STOP) {
                    //最上位の要素消すマン
                    listCelDelete();
                }
                //もし要素が無くなったら
                if(arrayListRun.size()==0){
                    //停止処理送るやで
                    sendBluetooth(1,STOP);
                    //華麗なる復活
                    Iwillbeback();
                }
            }
        }, allTime);
    }

    //送信データの生成
    static byte[] sendMessage(int num) {
        byte[] tele = new byte[21];
        tele[0] = (byte) 19;
        tele[1] = (byte) 0;
        tele[2] = (byte) 0;
        tele[3] = (byte) 0;
        tele[4] = (byte) 0;
        tele[5] = (byte) 0;
        tele[6] = (byte) 0;

        //止まるとき
        if (num == 0) {   //Stop Motors at PortC & D
            tele[7] = (byte) 0xA4;     //OUTPUT_POWER
            tele[8] = (byte) 0;
            tele[9] = (byte) 4;     //Motor ID = PortC
            tele[10] = (byte) 0;     //Motor Power
            tele[11] = (byte) 0xA6;    //OUTPUT_START
            tele[12] = (byte) 0;
            tele[13] = (byte) 4;     //Motor ID = PortC

            tele[14] = (byte) 0xA4;     //OUTPUT_POWER
            tele[15] = (byte) 0;
            tele[16] = (byte) 8;     //Motor ID = PortD
            tele[17] = (byte) 0;     //Motor Power
            tele[18] = (byte) 0xA6;    //OUTPUT_START
            tele[19] = (byte) 0;
            tele[20] = (byte) 8;     //Motor ID = PortD
        }

        //進むとき
        if (num == 1) {    //Forward Motors at PortC & D
            tele[7] = (byte) 0xA4;
            tele[8] = (byte) 0x00;
            tele[9] = (byte) 4;
            tele[10] = (byte) 68;
            tele[11] = (byte) 0xA6;
            tele[12] = (byte) 0;
            tele[13] = (byte) 4;

            tele[14] = (byte) 0xA4;
            tele[15] = (byte) 0x00;
            tele[16] = (byte) 8;
            tele[17] = (byte) 68;
            tele[18] = (byte) 0xA6;
            tele[19] = (byte) 0;
            tele[20] = (byte) 8;
        }
        //バック
        if (num == 2) {    //Backward Motors at PortC & D
            tele[7] = (byte) 0xA4;
            tele[8] = (byte) 0x00;
            tele[9] = (byte) 4;
            tele[10] = (byte) 40;
            tele[11] = (byte) 0xA6;
            tele[12] = (byte) 0;
            tele[13] = (byte) 4;

            tele[14] = (byte) 0xA4;
            tele[15] = (byte) 0x00;
            tele[16] = (byte) 8;
            tele[17] = (byte) 40;
            tele[18] = (byte) 0xA6;
            tele[19] = (byte) 0;
            tele[20] = (byte) 9;
        }

        //右回転
        if (num == 3) {    //Turn Right = Forward Motor at PortC(Left) and Stop PortD(Right)
            tele[7] = (byte) 0xA4;
            tele[8] = (byte) 0x00;
            tele[9] = (byte) 4;
            tele[10] = (byte) 0;
            tele[11] = (byte) 0xA6;
            tele[12] = (byte) 0;
            tele[13] = (byte) 4;

            tele[14] = (byte) 0xA4;
            tele[15] = (byte) 0x00;
            tele[16] = (byte) 8;
            tele[17] = (byte) 68;
            tele[18] = (byte) 0xA6;
            tele[19] = (byte) 0;
            tele[20] = (byte) 8;
        }
        //左回転
        if (num == 4) {    //Turn Left = Forward Motor at PortD(Right) and Stop PortC(Left)
            tele[7] = (byte) 0xA4;
            tele[8] = (byte) 0x00;
            tele[9] = (byte) 4;
            tele[10] = (byte) 68;
            tele[11] = (byte) 0xA6;
            tele[12] = (byte) 0;
            tele[13] = (byte) 4;

            tele[14] = (byte) 0xA4;
            tele[15] = (byte) 0x00;
            tele[16] = (byte) 8;
            tele[17] = (byte) 0;
            tele[18] = (byte) 0xA6;
            tele[19] = (byte) 0;
            tele[20] = (byte) 8;
        }
        //byte配列を返す
        return tele;
    }

    //List更新処理(ArrayListの情報をadapter&listに反映すっぞ)
    public void setList() {
        //アダプターの更新
        adapterRun = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayListRun);
        //アダプターセット
        listRun.setAdapter(adapterRun);
    }

    //リストの最上位のみ消す
    public void listCelDelete() {
        if (arrayListRun.size() != 0) {
            arrayListRun.remove(0);
            adapterRun.notifyDataSetChanged();
        } else {
            sendBluetooth(1, 0);
        }
    }

    //リストをリセットするメソッド
    public void listResetMethod() {
        //リスト全消し
        arrayListRun.clear();
        setList();
    }

    //リストの要素クリックした時
    public void setListClick() {

        //アイテムクリック時ののイベントを追加
        listRun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {

                //今クリックした要素のポジションを取得
                touchPos = pos;

                //選択アイテムを取得
                ListView listView = (ListView) parent;
                String item = (String) listView.getItemAtPosition(pos);

                //文字数へらしてタイトル取得
                final String title = getWords(item, 2);

                //秒数取得
                int ret = Integer.parseInt(item.substring(2 - 1).replaceAll("[^0-9]", ""));

                AlertDialog.Builder alertDlg = new AlertDialog.Builder(MainActivity.this);                       //ダイアログの生成
                //ダイアログのタイトル
                alertDlg.setTitle(title);
                final NumberPicker np1 = new NumberPicker(MainActivity.this);                     //ダイアログ中の数字ロール生成
                np1.setMaxValue(10);                                                                //上限設定
                np1.setMinValue(1);                                                                 //下限設定
                //ナンバーピッカーの初期位置を指定
                np1.setValue(ret);
                alertDlg.setView(np1);
                alertDlg.setPositiveButton(                                                         //ボタン押された処理
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // OK ボタンクリック処理

                                //選択された要素を編集
                                arrayListRun.set(touchPos, title + "【" + String.valueOf(np1.getValue()) + "秒】");

                                setList();
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
        });
    }

    //先頭から数えた文字数を取得するメソッド
    public String getWords(String origin, int num) {
        return origin.substring(0, num);
    }

    //リストの処理内容を送るメソッド
    public void TheRunningMachine() {

        String listItem;

        //リストの内容を保存
        saveArray(arrayListRun, "array", this);

        //要素数を取得
        int size = arrayListRun.size();

        //要素数だけ回す
        for (int cnt = 0; cnt < size; cnt++) {

            //listの要素を取得
            listItem = String.valueOf(arrayListRun.get(cnt));

            //秒数取得
            int ret = Integer.parseInt(listItem.substring(2 - 1).replaceAll("[^0-9]", ""));

            //要素の前からに文字を取得して送る処理を変える
            switch (getWords(listItem, 2)) {
                case "前進":
                    sendBluetooth(ret, FRONT);
                    break;
                case "後退":
                    sendBluetooth(ret, BACK);
                    break;
                case "右折":
                    sendBluetooth(ret, RIGHT);
                    break;
                case "左折":
                    sendBluetooth(ret, LEFT);
                    break;
            }
        }

    }

    //リストの要素が華麗に復活を果たすメソッド
    public void Iwillbeback(){
        //保存した要素を取得
        String[] arrayList = getArray("array", this);
        //追加処理
        for (int n = 0; n < arrayList.length; n++) {
            arrayListRun.add(arrayList[n]);
        }
        //リスト復活(リストの要素データはArrayListに入ってる)
        setList();
        //テキストを「実行」に変更
        Button com=(Button)findViewById(R.id.com);
        com.setText("実行");
        com.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_run));
    }

    // プリファレンス保存
    // aaa,bbb,ccc... の文字列で保存
    static public void saveArray(ArrayList<String> array, String PrefKey, Context context) {
        String str = new String("");
        for (int i = 0; i < array.size(); i++) {
            str = str + array.get(i);
            if (i != array.size() - 1) {
                str = str + ",";
            }
        }
        SharedPreferences prefs1 = context.getSharedPreferences("Array", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs1.edit();
        editor.putString(PrefKey, str).commit();
    }

    // プリファレンス取得
    // aaa,bbb,ccc...としたものをsplitして返す
    static public String[] getArray(String PrefKey, Context context) {
        SharedPreferences prefs2 = context.getSharedPreferences("Array", Context.MODE_PRIVATE);
        String stringItem = prefs2.getString(PrefKey, "");
        if (stringItem != null && stringItem.length() != 0) {
            return stringItem.split(",");
        } else {
            return null;
        }
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
