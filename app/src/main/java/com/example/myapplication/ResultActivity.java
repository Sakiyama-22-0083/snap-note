package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference();
    private String data;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        data = intent.getStringExtra("SEND_DATA");
        TextView textView = findViewById(R.id.result_text);
        textView.setMovementMethod(new ScrollingMovementMethod());
        if (data != null && !data.equals("")) textView.setText(data);

        findViewById(R.id.return_btn).setOnClickListener(v -> finish());
    }

    public void save(View v) {
        String key = reference.push().getKey();
        EditText editText=findViewById(R.id.edit_text);
        String title = editText.getText().toString();
        String content = data;

        MemoData memoData = new MemoData(key, title, content);

        assert key != null;
        reference.child("memo").child(key).setValue(memoData).addOnSuccessListener(v1 -> {
            Intent intent = new Intent(getApplication(), ListActivity.class);// インテント作成
            startActivity(intent);// SubActivityに画面遷移
            finish();
        });
    }

}
