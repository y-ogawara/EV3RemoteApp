package org.t_robop.y_ogawara.ev3remoteapp;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RunListActivity extends AppCompatActivity {

    //ArrayListに追加➝ArrayListを元にしたArrayAdapterを作成➝ListにArrayAdapterを反映

    /**リスト関連**/
    //実行する処理のリスト
    ListView listRun;
    //実行する処理用のアダプター
    ArrayAdapter<String> adapterRun;
    //リスト編集やるためのArrayList
    ArrayList arrayListRun;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_list);

        //リストの関連付け
        listRun=(ListView)findViewById(R.id.list_run);

        /**list関連の初期設定**/
        //ArrayListの初期化
        arrayListRun=new ArrayList<String>();
        //アダプターの初期化
        adapterRun=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayListRun);

        /**ダイアログレイアウトの呼び出し**/
        //ダイアログレイアウトの読み込み
        LayoutInflater factory = LayoutInflater.from(this);
        inputView = factory.inflate(R.layout.dialog_edit, null);
        //ダイアログ内の関連付け
        dialogEdit =(EditText)inputView.findViewById(R.id.dialog_edit);
        dialogText=(TextView)inputView.findViewById(R.id.dialog_text);

        //リストをクリックした時のイベント追加
        setListClick();

        //ダイアログのセット
        setDialog();
    }

    //前進ボタン押した時ッ(ボタン系はこの中身の通りにしてやれば動くはず)
    public void zensin(View v){
        //ダイアログ内のedittextクリーン
        resetEdit(dialogEdit,dialogText);
        //ダイアログ表示
        showDialog("前進",0);
    }

    //List更新処理(ArrayListの情報をadapter&listに反映すっぞ)
    public void setList(){
        //アダプターの更新
        adapterRun=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayListRun);
        //アダプターセット
        listRun.setAdapter(adapterRun);
    }

    //ダイアログ生成メソッド(起動時に呼んであげて)
    public void setDialog(){

        //ダイアログが精製されてない時(つまり初回のみ)
        if(alertDlg==null) {

            //新しくダイアログ作ったるで！
            alertDlg= new AlertDialog.Builder(RunListActivity.this)
            //ダイアログレイアウトに使うViewを指定
            .setView(inputView)
            //ok押した時のイベントセット
            .setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // OK ボタンクリック処理

                            //入力された文字列の文字数が0でない時(何か入力されてる時)
                            if(dialogEdit.getText().toString().length()!=0) {
                                //入力されたText取得
                                int second = Integer.parseInt(dialogEdit.getText().toString());
                                //新規作成時
                                if (NUM == 0) {
                                    //ArrayListに追加
                                    arrayListRun.add(dialogText.getText().toString() + "【" + String.valueOf(second) + "秒】");
                                }
                                //編集時
                                else if (NUM == 1) {
                                    //選択された要素を編集
                                    arrayListRun.set(touchPos, dialogText.getText().toString() + "【" + String.valueOf(second) + "秒】");
                                }
                                //List更新
                                setList();
                            }
                        }
                    })
            //Cancel押した時
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

    //リストの要素クリックした時
    public void setListClick(){

        //アイテムクリック時ののイベントを追加
        listRun.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent,
                                    View view, int pos, long id) {

                //今クリックした要素のポジションを取得
                touchPos=pos;

                //選択アイテムを取得
                ListView listView = (ListView)parent;
                String item = (String)listView.getItemAtPosition(pos);

                //listから数値のみ取得
                int ret = Integer.parseInt(item.substring(4-1).replaceAll("[^0-9]",""));
                //数値がある時(安全設計)
                if(String.valueOf(ret)!="") {
                    //ダイアログ内のedittextに貼る
                    dialogEdit.setText(String.valueOf(ret));
                }

                //ここで編集用ダイアログ出す
                showDialog(item.substring(0,2),1);//先頭二文字をタイトルに
            }
        });
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

    //新規追加用に一度edittextをクリーンできるメソッド
    public void resetEdit(EditText edit,TextView view){
        //edittextの内容を削除
        edit.getEditableText().clear();
        //textView(edittext以外のView)にフォーカスを移す
        view.requestFocus();
    }
}
