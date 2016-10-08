package org.t_robop.y_ogawara.ev3remoteapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RunListActivity extends AppCompatActivity {

    //実行する処理のリスト
    ListView listRun;
    //実行する処理用のアダプター
    ArrayAdapter<String> adapterRun;

    //テスト用前進ボタン
    Button btnMae;

    AlertDialog alertDlg;

    View inputView;

    EditText dialogEdit;

    TextView dialogText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_list);

        //関連付け
        listRun=(ListView)findViewById(R.id.list_run);
        //アダプターの初期化
        adapterRun=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        //レイアウトの呼び出し
        LayoutInflater factory = LayoutInflater.from(this);
        inputView = factory.inflate(R.layout.dialog_edit, null);

        //ダイアログ内の関連付け
        dialogEdit =(EditText)inputView.findViewById(R.id.dialog_edit);
        dialogText=(TextView)inputView.findViewById(R.id.dialog_text);

        // アイテムクリック時ののイベントを追加
        listRun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {

                // 選択アイテムを取得
                ListView listView = (ListView)parent;
                String item = (String)listView.getItemAtPosition(pos);

                Toast.makeText(RunListActivity.this, item, Toast.LENGTH_SHORT).show();

                //listから数値のみ取得してダイアログ内のedittextに貼る
                int ret = Integer.parseInt(item.substring(4-1).replaceAll("[^0-9]",""));
                dialogEdit.setText(String.valueOf(ret));

                //ここで編集用ダイアログ出す
                showDialog(item.substring(0,2));//現在のリストの場所+1,先頭二文字

            }
        });

        //初回ダイアログセット
        setDialog();

    }

    //前進ボタン押したー
    public void zensin(View v){

        //ダイアログ内のedittextクリーン
        resetEdit(dialogEdit,dialogText);

        //ダイアログ表示
        showDialog("前進");
    }

    //アダプター追加処理(入力した文字列と入力された秒数を一緒に追加すっぞ)
    public void addAdapter(String action,int second){

        //アダプターに追加
        adapterRun.add(action+"【"+String.valueOf(second)+"秒】");
        //アダプターセット
        listRun.setAdapter(adapterRun);

    }

    //ダイアログ生成メソッド(起動時に呼んであげて)
    public void setDialog(){

        if(alertDlg==null) {

            alertDlg = new AlertDialog.Builder(RunListActivity.this)
            .setView(inputView)
            .setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // OK ボタンクリック処理

                            //入力されたText取得
                            int second=Integer.parseInt(dialogEdit.getText().toString());

                            //アダプター追加からのセット
                            addAdapter(dialogText.getText().toString(),second);
                        }
                    })
            .setNegativeButton(
                    "Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel ボタンクリック処理
                        }
                    })
            .create();
        }
    }

    public void showDialog(String title){

        dialogText.setText(title);

        alertDlg.show();

    }

    /**新規追加用に一度edittextをクリーンできるメソッド**/
    public void resetEdit(EditText edit,TextView view){

        //edittextの内容を削除
        edit.getEditableText().clear();
        //textView(edittext以外のView)にフォーカスを移す
        view.requestFocus();

    }
}
