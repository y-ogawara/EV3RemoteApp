package org.t_robop.y_ogawara.ev3remoteapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

public class ButtonActivity extends AppCompatActivity {
    Button btn1;
    Button btn2;
    Button btn3;
    Button btn4;
    TextView txt1;
    ListView lst1;
    ArrayAdapter<String> adapter;
    String str2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        btn1 = (Button)findViewById(R.id.button);                                       //うしろ
        btn2 = (Button)findViewById(R.id.button5);                                      //ひだり
        btn3 = (Button)findViewById(R.id.button6);                                      //みぎ
        btn4 = (Button)findViewById(R.id.button7);                                      //まえ
        txt1 = (TextView)findViewById(R.id.textView);
        lst1 = (ListView)findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        lst1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onSend(view);
            }
        });
    }

    public void onSend(View v){
        //**********************************方向判定*****************************//
        switch(v.getId()){
            case R.id.button:
                str2 = "後";
                break;
            case R.id.button5:
                str2 = "左";
                break;
            case R.id.button6:
                str2 = "右";
                break;
            case R.id.button7:
                str2 = "前";
                break;
            default:
                break;
        }
        //********************************************************************//
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);                       //ダイアログの生成
        alertDlg.setTitle("ダイアログタイトル");
        final NumberPicker np1 = new NumberPicker(ButtonActivity.this);                     //ダイアログ中の数字ロール生成
        np1.setMaxValue(10);                                                                //上限設定
        np1.setMinValue(1);                                                                 //下限設定
        alertDlg.setView(np1);
        alertDlg.setPositiveButton(                                                         //ボタン押された処理
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                       /* String str1 = String.valueOf(np1.getValue());
                        adapter.add(str2+" "+str1);
                        lst1.setAdapter(adapter);//*/
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
}
