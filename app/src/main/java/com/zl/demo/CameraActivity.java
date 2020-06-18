package com.zl.demo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zl.demo.util.ImageUtil;
import com.zl.demo.util.YUVUtil;
import com.zl.demo.widget.CameraPreView;
import com.zl.demo.widget.FaceRectView;
import com.zl.demo.widget.IRCameraHelper;
import com.zl.face.FaceDetector;
import com.zl.face.FaceInfo;

import java.io.File;

public class CameraActivity extends Activity {
    private CameraPreView mTextureView;
    private FaceRectView faceRectView;
    private FaceDetector detector;
    private IRCameraHelper irCameraHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mTextureView = findViewById(R.id.texture);
        faceRectView = findViewById(R.id.rectview);
        mTextureView.setCameraAngle(90);
        mTextureView.setPrevieSize(640, 480);
        mTextureView.setPreviewFrameListener(previewFrameListener);
        detector = new FaceDetector();
        boolean init = detector.init(this);
        Log.i("canshu", "init result:" + init);
        init = detector.initIR(this);
        Log.i("canshu", "init result:" + init);
        initIrCamera();
    }

    protected void initIrCamera() {
        irCameraHelper = new IRCameraHelper();
        irCameraHelper.setCameraAngle(90);
        irCameraHelper.setPrevieSize(640, 480);
        irCameraHelper.init();
        irCameraHelper.setPreviewFrameListener(irPreviewFrameListener);
    }

    private CameraPreView.PreviewFrameListener previewFrameListener = data -> {
        data = YUVUtil.YUV420spRotate90Clockwise(data, 640, 480);
        FaceInfo[] faceInfos = detector.detectYuv(data, 480, 640);
        FaceInfo faceInfo = findMaxFace(faceInfos);
        if (null != faceInfo) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    faceRectView.setRect(faceInfo.getFaceRect());
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    faceRectView.setRect(0, 0, 0, 0);
                }
            });
        }
//        SavePreviewFrame(data);
    };

    private IRCameraHelper.PreviewFrameListener irPreviewFrameListener = data -> {
        data = YUVUtil.YUV420spRotate90Clockwise(data,640, 480);
        FaceInfo[] faceInfos = detector.detectIRYuv(data, 480, 640);
        FaceInfo faceInfo = findMaxFace(faceInfos);
        if (null != faceInfo) {
            runOnUiThread(() -> Toast.makeText(getApplicationContext(),faceInfo.toString(),Toast.LENGTH_SHORT).show());
        } else {
//            runOnUiThread(() -> Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show());
        }
    };

    private void SavePreviewFrame(byte[] frame) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String path = Constant.CACHE_PATH + File.separator + "frame" + File.separator + System.currentTimeMillis() + ".png";
                Bitmap bitmap = ImageUtil.getBitmap(frame, 480, 640);
                ImageUtil.saveBitmapFile(path, bitmap);
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (irCameraHelper != null) {
            irCameraHelper.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (irCameraHelper != null) {
            irCameraHelper.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (irCameraHelper != null) {
            irCameraHelper.onDestroy();
        }
    }


    public static FaceInfo findMaxFace(FaceInfo[] faceInfos) {
        if (null == faceInfos || faceInfos.length < 1) {
            return null;
        }
        FaceInfo maxFaceInfo = null;
        for (FaceInfo faceInfo : faceInfos) {
            if (maxFaceInfo != null && maxFaceInfo.getFaceRect().width() * maxFaceInfo.getFaceRect().height() > maxFaceInfo.getFaceRect().width() * maxFaceInfo.getFaceRect().height()) {
                break;
            } else {
                maxFaceInfo = faceInfo;
            }
        }
        return maxFaceInfo;
    }
}
