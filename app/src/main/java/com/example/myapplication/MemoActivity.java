package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MemoActivity extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);
        TextView title = findViewById(R.id.title_text);
        TextView content = findViewById(R.id.content_text);
        Intent intent = getIntent();
        MemoData data = (MemoData) intent.getSerializableExtra("MEMO");
        assert data != null;
        title.setText(data.getTitle());
        content.setText(data.getContent());
        // ボタンクリック時の処理
        findViewById(R.id.return_list_btn).setOnClickListener(v -> finish());
    }
}

