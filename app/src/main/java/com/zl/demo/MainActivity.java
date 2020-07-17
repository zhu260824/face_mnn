package com.zl.demo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.zl.demo.util.ImageUtil;
import com.zl.face.FaceDetector;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private ImageView ivT1, ivT2, ivT31, ivT32, ivT41, ivT42;
    private Button btnT1, btnT2;
    private TextView tvT1, tvT2, tvT3, tvT4;
    private FaceDetector detector;
    private String imgPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mnn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ivT1 = findViewById(R.id.iv_t1);
        btnT1 = findViewById(R.id.btn_t1);
        tvT1 = findViewById(R.id.tv_t1);
        ivT2 = findViewById(R.id.iv_t2);
        btnT2 = findViewById(R.id.btn_t2);
        tvT2 = findViewById(R.id.tv_t2);
        ivT31 = findViewById(R.id.iv_t3_1);
        ivT32 = findViewById(R.id.iv_t3_2);
        tvT3 = findViewById(R.id.tv_t3);
        ivT41 = findViewById(R.id.iv_t4_1);
        ivT42 = findViewById(R.id.iv_t4_2);
        tvT4 = findViewById(R.id.tv_t4);
        ivT1.post(() -> ivT1.setImageBitmap(ImageUtil.getBitmapByAssets(ivT1.getContext(), "img/test.jpg")));
        ivT2.post(() -> ivT2.setImageBitmap(ImageUtil.getBitmapByAssets(ivT2.getContext(), "img/test2.jpg")));
        btnT1.setOnClickListener(v -> {
            String msg = detect(v.getContext(), "img/test.jpg", ivT1);
            tvT1.setText(msg);
        });
        btnT2.setOnClickListener(v -> {
            String msg = detect(v.getContext(), "img/test2.jpg", ivT2);
            tvT2.setText(msg);
        });
        findViewById(R.id.btn_t1_1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String msg = getFeature(v.getContext(), "img/test.jpg", ivT1);
//                tvT1.setText(msg);
            }
        });
        findViewById(R.id.btn_t3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  ivT31.post(() -> ivT31.setImageBitmap(ImageUtil.getBitmapByAssets(ivT31.getContext(), "img/test.jpg")));
                ivT32.post(() -> ivT32.setImageBitmap(ImageUtil.getBitmapByAssets(ivT32.getContext(), "img/test3.png")));
                long startTime = System.currentTimeMillis();
                float[] feature1 = detector.getFeature(imgPath + File.separator + "img/test.jpg");
                Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
                Log.i("canshu", Arrays.toString(feature1));
                startTime = System.currentTimeMillis();
                float[] feature2 = detector.getFeature(imgPath + File.separator + "img/test3.png");
                Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
                Log.i("canshu", Arrays.toString(feature2));
                double smaile = detector.featureCompare(feature1, feature2);
                Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
                Log.i("canshu", "比对结果：" + smaile);
                tvT3.setText("比对结果：" + smaile);*/
            }
        });
        findViewById(R.id.btn_t4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ivT41.post(() -> ivT41.setImageBitmap(ImageUtil.getBitmapByAssets(ivT41.getContext(), "img/test3.png")));
//                ivT42.post(() -> ivT42.setImageBitmap(ImageUtil.getBitmapByAssets(ivT42.getContext(), "img/test4.png")));
//                long startTime = System.currentTimeMillis();
//                float[] feature1 = detector.getFeature(imgPath + File.separator + "img/test3.png");
//                Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
//                Log.i("canshu", Arrays.toString(feature1));
//                startTime = System.currentTimeMillis();
//                float[] feature2 = detector.getFeature(imgPath + File.separator + "img/test4.png");
//                Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
//                Log.i("canshu", Arrays.toString(feature2));
//                double smaile = detector.featureCompare(feature1, feature2);
//                Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
//                Log.i("canshu", "比对结果：" + smaile);
//                tvT4.setText("比对结果：" + smaile);
            }
        });
        activate();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (checkPermission()) {
            activate();
//        } else {
//            requestPermission();
//        }
    }

    private String detect(Context mContext, String assetsPath, ImageView imageView) {
        String msg = "";
        Bitmap bitmap = ImageUtil.getBitmapByAssets(mContext, assetsPath);
        msg = msg + "image size = " + bitmap.getWidth() + "x" + bitmap.getHeight() + "\n";
        long startTime = System.currentTimeMillis();
//        FaceInfo[] faces = detector.detectBitmap(bitmap);
//        FaceInfo[] faces = detector.detectFile(imgPath + File.separator + assetsPath);
//        Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
//        Log.i("canshu", Arrays.toString(faces));

        float[] faces = detector.detectPic(imgPath + File.separator + assetsPath);
        Log.i("canshu", "耗时：" + (System.currentTimeMillis() - startTime));
        Log.i("canshu", Arrays.toString(faces));
//        List<Face> faces = hyper.getTrackingInfo();
//        msg = msg + "face num = " + faces.size() + "\n";
//        msg = msg + "detectTime = " + (System.currentTimeMillis() - startTime) + "ms";
//        for (Face face : faces) {
//            Log.i("canshu", face.toString());
//            canvas.drawRect(face.getLeft(), face.getTop(), face.getRight(), face.getBottom(), paint);
//        }
//        imageView.post(() -> imageView.setImageBitmap(cb));
        return msg;
    }

    public void activate() {
        detector = new FaceDetector();
        boolean init = detector.init(this);
        Log.i("canshu", "init result:" + init);
        ImageUtil.copyImage2SD(this, imgPath);
    }


    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 2);
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 3);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0) {
            if (checkPermission()) {
                activate();
            } else {
                requestPermission();
            }
        }
    }


}