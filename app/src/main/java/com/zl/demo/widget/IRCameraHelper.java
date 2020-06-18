package com.zl.demo.widget;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.List;

public class IRCameraHelper {
    private static final String TAG = "IRCameraHelper";
    private static final int PREVIEW_MSG = 1;
    private Camera mCamera;
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private int previewWidth;
    private int previewHeight;
    private int cameraAngle = 0;
    private Handler mCameraHandler;
    private PreviewFrameListener previewFrameListener;
    private boolean stopPreview = true;

    public void init() {
        openCamera();
        setCameraParameters();
        setDisplayOrientation(cameraAngle);
        setPreviewSize();
        startPreview();
    }

    public void onResume() {
        if (mCamera != null && stopPreview) {
            startPreview();
        } else {
            init();
        }
    }

    public void onPause() {
        stopPreview();
    }

    public void onDestroy() {
        stopPreview();
        closeCamera();
    }


    public void setCameraAngle(int cameraAngle) {
        this.cameraAngle = cameraAngle;
    }

    public void setPrevieSize(int previewWidth, int previewHeight) {
        this.previewWidth = previewWidth;
        this.previewHeight = previewHeight;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewFrameListener(PreviewFrameListener previewFrameListener) {
        this.previewFrameListener = previewFrameListener;
    }

    private void setPreviewSize() {
        if (null == mCamera) return;
        Camera.Parameters mParameters = mCamera.getParameters();
        /*List<Camera.Size> sizes = mParameters.getSupportedPreviewSizes();
        if (null != sizes && sizes.size() > 0) {
            for (Camera.Size size : sizes) {
                Log.i(TAG, "Camera.Size:width" + size.width + ",height" + size.height);
            }
        }*/
        mParameters.setPreviewSize(previewWidth, previewHeight);
        mCamera.setParameters(mParameters);
    }

    private void setCameraParameters() {
        if (null == mCamera) return;
        Camera.Parameters mParameters = mCamera.getParameters();
        List<String> focusModes = mParameters.getSupportedFocusModes();
        if (null != focusModes && focusModes.size() > 0) {
//        for (String focusMode : focusModes) {
//            Log.i(TAG,"FocusMode:"+focusMode);
//        }
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            } else if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }
        List<String> sceneModes = mParameters.getSupportedSceneModes();
        if (null != sceneModes && sceneModes.size() > 0) {
//            for (String focusMode : sceneModes) {
//                Log.i(TAG, "SceneMode:" + focusMode);
//            }
            if (sceneModes.contains(Camera.Parameters.SCENE_MODE_SPORTS)) {
                mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_SPORTS);
            } else if (sceneModes.contains(Camera.Parameters.SCENE_MODE_ACTION)) {
                mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_ACTION);
            }
        }
        List<Integer> previewFormats = mParameters.getSupportedPreviewFormats();
        if (null != previewFormats && previewFormats.size() > 0) {
            if (previewFormats.contains(ImageFormat.NV21)) {
                mParameters.setPreviewFormat(ImageFormat.NV21);
            }
        }
        mCamera.setParameters(mParameters);
    }

    private void openCamera() {
        if (null == mCamera) {
            mCamera = Camera.open(getCameraId());
        }
    }

    private int getCameraId() {
        return mCameraId;
    }

    public void setCameraId(int mCameraId) {
        this.mCameraId = mCameraId;
    }


    private void setDisplayOrientation(int degree) {
        if (mCamera != null)
            mCamera.setDisplayOrientation(degree);
    }

    private void startPreview() {
        if (mCamera != null) {
            mCamera.setErrorCallback(errorCallback);
            if (previewFrameListener != null) {
                setPreviewCallbackWithBuffer();
            }
            mCamera.startPreview();
            stopPreview = false;
        }
    }


    private Handler getCameraHandler() {
        if (null == mCameraHandler) {
            synchronized (IRCameraHelper.class) {
                if (null == mCameraHandler) {
                    HandlerThread mCameraThread = new HandlerThread("IRCameraThread");
                    mCameraThread.start();
                    mCameraHandler = new Handler(mCameraThread.getLooper()) {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if (msg.what == PREVIEW_MSG && previewFrameListener != null) {
                                previewFrameListener.onPreviewFrame((byte[]) msg.obj);
                            }
                        }
                    };
                }
            }
        }
        return mCameraHandler;
    }

    private void setPreviewCallbackWithBuffer() {
        if (mCamera != null) {
            Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
            int size = ((previewSize.width * previewSize.height) * ImageFormat.getBitsPerPixel(ImageFormat.NV21)) / 8;
            byte[] callbackBuffer = new byte[size];
            mCamera.setPreviewCallbackWithBuffer((data, camera) -> {
                camera.addCallbackBuffer(data);
                if (data != null) {
                    getCameraHandler().sendMessage(getCameraHandler().obtainMessage(1, data));
                }
            });
            mCamera.addCallbackBuffer(callbackBuffer);
        }
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallbackWithBuffer(null);
            mCamera.setErrorCallback(null);
            mCamera.stopPreview();
            stopPreview = true;
        }
    }

    private void closeCamera() {
        if (mCamera != null) {
            mCamera.setErrorCallback(null);
            mCamera.release();
            mCamera = null;
            Log.i(TAG, "Camera has closed!");
        }
    }

    private Camera.ErrorCallback errorCallback = (error, camera) -> Log.e(TAG, "Camera,onError:" + error);

    public interface PreviewFrameListener {
        void onPreviewFrame(byte[] data);
    }
}
