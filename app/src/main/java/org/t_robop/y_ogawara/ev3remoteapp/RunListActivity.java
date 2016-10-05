package org.t_robop.y_ogawara.ev3remoteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class RunListActivity extends AppCompatActivity {

    //実行する処理のリスト
    ListView listRun;
    //実行する処理用のアダプター
    ArrayAdapter<String> adapterRun;

    //テスト用前進ボタン
    Button btnMae;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_list);

        //関連付け
        listRun=(ListView)findViewById(R.id.list_run);
        //アダプターの初期化
        adapterRun=new ArrayAdapter<>(this, R.layout.run_list_item);

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

        //アダプター追加からのセット
        addAdapter();

    }

    //アダプター追加処理
    public void addAdapter(){

        //アダプターに追加
        adapterRun.add("やったぜ");
        //アダプターセット
        listRun.setAdapter(adapterRun);

    }
}
