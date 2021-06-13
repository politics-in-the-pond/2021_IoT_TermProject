package com.example.termproject_applight;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {

    DBHelper dbHelper;
    SQLiteDatabase db = null;
    Cursor cursor;
    ListView listview;
    SettingItem listItem;
    SettingAdapter adapter;

    String name = "";
    String power = "";
    String color = "";
    String time = "";

    Button delete;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        listview = findViewById(R.id.settingList);
        dbHelper = new DBHelper(this,4);
        db = dbHelper.getWritableDatabase();

        listItem = new SettingItem();
        adapter = new SettingAdapter(SettingActivity.this, new SettingAdapter.OnApplyClickListener(){
            @Override
            public void onApply(View v, int pos){
                name = adapter.getName(pos);
                color = adapter.getColor(pos);
                time = adapter.getTime(pos);
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("setting", name);
                intent.putExtra("color", color);
                intent.putExtra("time", time);
                setResult(1, intent);
                finish();
            }
        });

        listview.setAdapter(adapter);

        //리스트뷰를 출력하기 위한 함수. 액티비티가 create할때마다 불려서 리스트를 만듦.
        listViewDB();

        delete = findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete();
            }
        });
    }

    public void listViewDB(){
        //커서로 원하는 데이터베이스 어트리뷰트를 고르기~우리는 한 row데이터를 가져올 것이므로 아래와 같은 쿼리를 사용함
        cursor = db.rawQuery("SELECT*FROM setting",null);
        startManagingCursor(cursor);

        //데이터베이스에있는 모든 row에 대한 어트리뷰트 값을 가져와 string 변수에 저장. while문 한번은 한 row에 대한 불러오기와 가공, 리스트뷰 추가.
        while (cursor.moveToNext()) {
            name = cursor.getString(0);
            color = cursor.getString(1);
            time = cursor.getString(2);
            //데이터베이스에서 가져온 시간을 출력을 위해 일정 00:00 형태로 만듬
            //모든 가공이 끝나면 리스트에 추가해 줌.
            adapter.addItem(name, color, time);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //기존 리스트뷰를 모두 삭제함. 이렇게 하지 않으면 새로 업뎃된 리스트가 기존 리스트 뒤에 또 생기므로 원하는 리스트가 안나옴..
        int count = adapter.getCount();
        if(count > 0)
        {
            for(int i = count-1 ; i >= 0; i--)
            {
                adapter.removeItem(i);
            }
        }

        //삭제하고 업뎃된 디비로 리스트 다시 만듬
        listViewDB();
        //수정된 리스트뷰 적용
        adapter.notifyDataSetChanged();
    }

    public void delete() {
        int count = adapter.getCount();
        if(count > 0)
        {
            for(int i = count-1 ; i >= 0; i--)
            {
                adapter.removeItem(i);
            }
        }
        db.execSQL("DELETE FROM setting");
    }
}
