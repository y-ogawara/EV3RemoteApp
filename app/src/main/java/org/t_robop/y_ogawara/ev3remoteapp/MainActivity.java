package org.t_robop.y_ogawara.ev3remoteapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.t_robop.y_ogawara.ev3remoteapp.ev3.AndroidComm;
import org.t_robop.y_ogawara.ev3remoteapp.ev3.EV3Command;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements Handler.Callback {

    //ハンドラ宣言
    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
    @Override
    public void run() {
        handler.sendEmptyMessage(1);
    }
};
    BluetoothAdapter mBtAdapter;
    //定数宣言
    final int STOP = 0;
    final int FRONT = 1;
    final int BACK = 2;
    final int RIGHT = 3;
    final int LEFT = 4;
    final int TEST = 5;


    String macAddress;

    String action;

    /**リスト関連**/
    //実行する処理のリスト
    ListView listRun;
    //実行する処理用のアダプター
    static ArrayAdapter<String> adapterRun;
    //リスト編集やるためのArrayList
    static ArrayList arrayListRun;
    //リストのどの要素をクリックしたかを知るためのグローバル変数
    int touchPos;

    /**ダイアログ設定関連**/
    //ダイアログ
    AlertDialog alertDlg;
    //ダイアログ内の処理分け用グローバル変数(0:追加処理,1:編集処理)
    int NUM;

    /**ダイアログレイアウト関連**/
    //ダイアログのレイアウトを取得するView
    View inputView;
    //ダイアログ内のEditText(こいつを弄ることで何処からでもダイアログ内のEditTextを弄れるゾ！)
    EditText dialogEdit;
    //ダイアログ内のTextView(こいつを弄ることで何処からでもダイアログ内のTextViewを弄れるゾ！)
    TextView dialogText;

    /**ボタン・スピナー関連**/
    Button com;
    Button connect;
    Button front;
    Button back;
    Button left;
    Button right;
    //レイアウト
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler(MainActivity.this);
        spinnerSetting();

        //リストの関連付け
        listRun=(ListView)findViewById(R.id.list_run);

        /**list関連の初期設定**/
        //ArrayListの初期化
        arrayListRun=new ArrayList<String>();
        saveArray(arrayListRun,"array",this);

        //アダプターの初期化
        adapterRun=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayListRun);

        /**ダイアログレイアウトの呼び出し**/
        //ダイアログレイアウトの読み込み
        LayoutInflater factory = LayoutInflater.from(this);
        inputView = factory.inflate(R.layout.dialog_edit, null);
        //ダイアログ内の関連付け
        dialogEdit =(EditText)inputView.findViewById(R.id.dialog_edit);
        dialogText=(TextView)inputView.findViewById(R.id.dialog_text);
        //ボタン・スピナーの関連付け
        com = (Button) findViewById(R.id.com);
        linearLayout=(LinearLayout) findViewById(R.id.rightScreen);
        connect =(Button)findViewById(R.id.connect);
        front =(Button)findViewById(R.id.front);
        back =(Button)findViewById(R.id.back);
        left =(Button)findViewById(R.id.left);
        right =(Button)findViewById(R.id.right);

        setListClick();

        setDialog();

    }
    //命令ボタン処理
    public void command(View v) {
        if (String.valueOf(com.getText()).equals("実行")) {
            //ev3未接続時に落ちないように
            if (mBtAdapter == null){
                Toast.makeText(this, "接続ボタンを押してね！", Toast.LENGTH_LONG).show();
                return;
            }
            if (arrayListRun.size() != 0) {
                //リストの要素の保存
                saveArray(arrayListRun,"array",this);
                //ボタンのテキストを「停止」に変更
                com.setText("停止");
                //ボタンのbackgroundを赤に変更
                com.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_stop));
                //リセットボタンの無効
                Button reset =(Button) findViewById(R.id.reset);
                reset.setBackgroundColor(Color.parseColor("#4E342E"));
                reset.setEnabled(false);
                //右画面を暗転
                onFilter();
                //上から処理開始
                TheRunningMachine();
            }
        } else {
            //停止処理
            cancel();
            //実行されなかったリストの要素を消す
            arrayListRun.clear();
            //華麗に復活
            Iwillbeback();
        }
    }

    public void onFilter(){
        //暗転
        linearLayout.setBackgroundColor(Color.parseColor("#424242"));
        //ボタンの無効
        connect.setVisibility(View.INVISIBLE);
        front.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);
        left.setVisibility(View.INVISIBLE);
        right.setVisibility(View.INVISIBLE);
    }
    public void offFilter(){
        //暗転解除
        linearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        //ボタンの有効
        connect.setVisibility(View.VISIBLE);
        connect.setVisibility(View.VISIBLE);
        front.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        left.setVisibility(View.VISIBLE);
        right.setVisibility(View.VISIBLE);
    }
    //リセットボタンの処理
    public void reset(View v) {
        //リスト全消し
        listResetMethod();
    }


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
        resetEdit(dialogEdit,dialogText);
        showDialog(action,0);
    }
        //********************************************************************//
    public void setDialog(){
        if(alertDlg==null) {
            alertDlg = new AlertDialog.Builder(MainActivity.this)                       //ダイアログの生成
            .setTitle(action)
            .setView(inputView)
            .setPositiveButton(                                                         //ボタン押された処理
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // OK ボタンクリック処理
                            if (dialogEdit.getText().toString().length() != 0) {
                                float second = Float.parseFloat(dialogEdit.getText().toString());
                                if (NUM == 0) {
                                    arrayListRun.add(action + "【" + String.valueOf(second) + "秒】");
                                } else if (NUM == 1) {
                                    //選択された要素を編集
                                    arrayListRun.set(touchPos, dialogText.getText().toString() + "【" + String.valueOf(second) + "秒】");
                                }
                                setList();
                            }
                        }
                    })
            .setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel ボタンクリック処理
                        }
                    })

            // 表示
            .create();
        }


    }
    //接続ボタン処理
    public void connect(View v) {
        //BluetoothAdapter mBtAdapter;

//        if (mBtAdapter == null){
//
//        }else if (){
//
//        }
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice device = mBtAdapter.getRemoteDevice(macAddress);

        AndroidComm.getInstance().setDevice(device); // Set device

// Connect to EV3
        try {
            EV3Command.open();
            Toast.makeText(this, "接続成功！", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "エラーです", Toast.LENGTH_LONG).show();
            //接続が失敗したらnullに
            mBtAdapter = null;
        }

        // キーボードを強制的に隠せてない
        if (v != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) this.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }


    }
    //spinner設定用メソッド
    void spinnerSetting (){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // アイテムを追加します
        adapter.add("00:16:53:44:69:AB,ev3青");
        adapter.add("00:16:53:44:59:C0,ev3緑");
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
    public void sendBluetooth(float num,int event){
        num = num*100;

        //ここで信号をEV3に送信
        try {
            AndroidComm.mOutputStream.write(sendMessage(event));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ここで指定時間後に
        handler.postDelayed(runnable, (long)num);
    }

    //送信データの生成
    static byte[] sendMessage(int num) {
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

    //List更新処理(ArrayListの情報をadapter&listに反映すっぞ)
    public void setList(){
        //アダプターの更新
        adapterRun=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayListRun);
        //アダプターセット
        listRun.setAdapter(adapterRun);
    }

    //新規追加用に一度edittextをクリーンできるメソッド
    public void resetEdit(EditText edit,TextView view){
        //edittextの内容を削除
        edit.getEditableText().clear();
        //textView(edittext以外のView)にフォーカスを移す
        view.requestFocus();
    }

    //リストの最上位のみ消す
     public void listCelDelete(){
         //要素がまだある時
        if(arrayListRun.size()!=0) {
            //最上位の要素消す
            arrayListRun.remove(0);
            //リスト適用
            adapterRun.notifyDataSetChanged();
            //次のデータ送信
            TheRunningMachine();
        }
    }

    //リストをリセットするメソッド
    public void listResetMethod(){
        //リスト全消し
        arrayListRun.clear();
        setList();
    }

    //タイトルと設定した数字でダイアログが表示されます
    public void showDialog(String title,int another){
        //追加処理or編集処理
        NUM=another;
        //指定されたタイトルをダイアログ内のTextViewにセット
        dialogText.setText(title);
        //ダイアログ展開
        alertDlg.show();
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

                //秒数取得
                float ret = Float.parseFloat(item.substring(2 - 1).replaceAll("[^0-9+\\.]", ""));
                //数値がある時(安全設計)
                if(String.valueOf(ret)!="") {
                    //ダイアログ内のedittextに貼る
                    dialogEdit.setText(String.valueOf(ret));
                }

                //ここで編集用ダイアログ出す
                showDialog(getWords(item,2),1);//先頭二文字をタイトルに

            }
        });
    }

    //先頭から数えた文字数を取得するメソッド
    public String getWords(String origin,int num){
        return origin.substring(0,num);
    }

    // プリファレンス保存
    // aaa,bbb,ccc... の文字列で保存
    public void saveArray(ArrayList<String> array, String PrefKey,Context context){
        String str = new String("");
        for (int i =0;i<array.size();i++){
            str = str + array.get(i);
            if (i !=array.size()-1){
                str = str + ",";
            }
        }
        SharedPreferences prefs1 = context.getSharedPreferences("Array", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs1.edit();
        editor.putString(PrefKey, str).commit();
    }

    // プリファレンス取得
    // aaa,bbb,ccc...としたものをsplitして返す
    public String[] getArray(String PrefKey,Context context){
        SharedPreferences prefs2 = context.getSharedPreferences("Array", Context.MODE_PRIVATE);
        String stringItem = prefs2.getString(PrefKey,"");
        if(stringItem != null && stringItem.length() != 0){
            return stringItem.split(",");
        }else{
            return null;
        }
    }

    //リストのposition0の内容に応じた処理を送信するメソッド
    public void TheRunningMachine() {
        //この辺に接続処理とかtime処理とか書いてくらさい

        if(arrayListRun.size()!=0) {
            //listの要素を取得
            String listItem = String.valueOf(arrayListRun.get(0));

            //秒数取得
            float ret = Float.parseFloat(listItem.substring(2 - 1).replaceAll("[^0-9]", ""));

            listRun.getChildAt(0).setBackgroundColor(Color.parseColor("#00ff00"));

            //要素の前からに文字を取得
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
        else {
            //強制停止
            cancel();
            //リスト復活
            Iwillbeback();
        }
    }

    //リストの華麗なる復活
    public void Iwillbeback(){
        //保存した要素データの読み込み
        String[] arrayList=getArray("array",this);
        //保存数だけ追加処理
        for(int n=0;n<arrayList.length;n++){
            arrayListRun.add(arrayList[n]);
        }
        //リスト復活(リストの要素データはArrayListに入ってる)
        setList();
        //テキストを「実行」に変更
        com.setText("実行");
        com.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_run));
        //右画面の暗転を解除
        offFilter();
        //リセットボタンの有効
        Button reset =(Button) findViewById(R.id.reset);
        reset.setEnabled(true);
        reset.setBackgroundDrawable(getResources().getDrawable(R.drawable.color_reset));
    }
    void cancel(){
        //念のためハンドラを終了
        handler.removeCallbacks(runnable);
        try {
            //ev3に終了命令を飛ばす
            AndroidComm.mOutputStream.write(sendMessage(0));

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        //コールバックメッセージを取得
        switch(msg.what){
            case 0:
                //エラー用
                return true;
            case 1:
                // 遅延処理が完了したときに呼ばれる
                // 次の遅延処理を呼び出して欲しい

                //リストの最上位の要素消しまーす
                listCelDelete();

                return true;
            default:
                return false;
        }
    }
}
