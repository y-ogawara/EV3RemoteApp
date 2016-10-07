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

    AlertDialog.Builder alertDlg;

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


                //ここで編集用ダイアログ出す

            }
        });
    }

    //前進ボタン押したー
    public void zensin(View v){

        setDialog("前進");

    }

    //アダプター追加処理
    public void addAdapter(String action,int second){

        //アダプターに追加
        adapterRun.add(action+"【"+String.valueOf(second)+"秒】");
        //アダプターセット
        listRun.setAdapter(adapterRun);

    }

    //ダイアログ生成メソッド
    public void setDialog(String title){

        dialogText.setText(title);

        if(alertDlg==null) {

            alertDlg = new AlertDialog.Builder(this)
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
                    });
            alertDlg.create();
        }
        alertDlg.show();

    }
}
