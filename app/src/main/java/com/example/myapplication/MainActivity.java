package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TextRecognitionTestApp";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private PreviewView viewFinder;
    private ImageCapture imageCapture;
    private TextRecognizer textRecognizer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewFinder = findViewById(R.id.viewFinder);// カメラのView
        textRecognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());// テキスト認識オブジェクト
        // パーミッションの確認
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
        // ボタンクリック時の処理
        findViewById(R.id.capture_btn).setOnClickListener(v -> capture());
        findViewById(R.id.list_btn).setOnClickListener(v -> goList());
    }

    /**
     * ListActivityへの移動メソッド
     */
    private void goList(){
        Intent intent = new Intent(getApplication(), ListActivity.class);// インテント作成
        intent.putExtra("SEND_DATA", "");
        startActivity(intent);
    }
    /**
     * 撮影メソッド
     */
    private void capture() {
        if (imageCapture == null) return;

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            /**
             * キャプチャ成功時メソッド
             * @param imageProxy The captured image
             */
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                super.onCaptureSuccess(imageProxy);
                if (imageProxy.getImage() != null) {
                    InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
                    textRecognizer.process(image).addOnSuccessListener(visionText -> showResultText(visionText.getText())).addOnFailureListener(e -> showResultText("認識に失敗しました: " + e)).addOnCompleteListener(task -> imageProxy.close());
                }
            }

            /**
             * エラー発生時メソッド
             * @param exception An {@link ImageCaptureException} that contains the type of error, the
             *                  error message and the throwable that caused it.
             */
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "エラー:" + exception.getMessage(), exception);
                Toast.makeText(MainActivity.this, "エラー:" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 認識結果を画面に表示するメソッド
     */
    private void showResultText(String resultText) {
        Intent intent = new Intent(getApplication(), ResultActivity.class);// インテント作成
        intent.putExtra("SEND_DATA", resultText);// 入力データをセット
        startActivity(intent);// 画面遷移
    }

    /**
     * 撮影開始メソッド
     */
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);// カメラプロバイダーを非同期に取得するプロセス
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();// カメラプロバイダー
                Preview preview = new Preview.Builder().build();// プレビューのユースケースを作成
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());// プレビューのサーフェスプロバイダーを設定

                imageCapture = new ImageCapture.Builder().build();// 画面キャプチャのユースケース作成
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;// カメラの指定
                cameraProvider.unbindAll();// 既存のユースケースバインドを解除
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);// 新しいユースケースをバインド
            } catch (InterruptedException | ExecutionException e) {
                Log.e(TAG, "ユースケースのバインディングに失敗", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    /**
     * 全てのパーミッション確認メソッド
     */
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            int resultCheck = ContextCompat.checkSelfPermission(this, permission);
            if (resultCheck != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    /**
     * パーミッションリクエストメソッド
     *
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) startCamera();
            else {
                Toast.makeText(this, "カメラ撮影が許可されなかったため終了します", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

}
