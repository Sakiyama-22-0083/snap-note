package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity implements ListView.OnItemLongClickListener, ListView.OnItemClickListener {
    private static final String TAG = "ListActivity";
    private FirebaseAuth auth;
    public FirebaseDatabase database;
    public DatabaseReference reference;
    public CustomAdapter customAdapter;
    public ListView listView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        // Anonymous login
        signInAnonymously();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("memo");

        listView = findViewById(R.id.list_view);

        //CustomAdapterをセット
        customAdapter = new CustomAdapter(getApplicationContext(), R.layout.card_view, new ArrayList<>());
        listView.setAdapter(customAdapter);
        //LongListenerを設定
        listView.setOnItemLongClickListener(this);
        listView.setOnItemClickListener(this);

        // ボタンクリック時の処理
        findViewById(R.id.return_capture_btn).setOnClickListener(v -> finish());

        reference.addChildEventListener(new ChildEventListener() {
            // データを読み込むときはイベントリスナーを登録して行う。
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // アイテムのリストを取得するか、アイテムのリストへの追加がないかリッスンします。
                MemoData memoData = dataSnapshot.getValue(MemoData.class);
                customAdapter.add(memoData);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // リスト内のアイテムに対する変更がないかリッスンします。
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                // リストから削除されるアイテムがないかリッスンします。
                Log.d("MemoActivity", "onChildRemoved:" + dataSnapshot.getKey());
                MemoData result = dataSnapshot.getValue(MemoData.class);
                if (result == null) return;

                MemoData item = customAdapter.getMemoKey(result.getFirebaseKey());

                customAdapter.remove(item);
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                // 並べ替えリストの項目順に変更がないかリッスンします。
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // ログを記録するなどError時の処理を記載する
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final MemoData memoData = customAdapter.getItem(position);
        Intent intent = new Intent(getApplication(), MemoActivity.class);
        intent.putExtra("MEMO", memoData);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final MemoData memoData = customAdapter.getItem(position);

        new AlertDialog.Builder(this).setTitle("削除").setMessage("このメモを削除しますか？").setPositiveButton("Yes", (dialog, which) -> {
            assert memoData != null;
            reference.child(memoData.getFirebaseKey()).removeValue();
        }).setNegativeButton("No", null).show();
        return false;
    }

    private void signInAnonymously() {
        auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInAnonymously:success");
            }
        });
    }

}
