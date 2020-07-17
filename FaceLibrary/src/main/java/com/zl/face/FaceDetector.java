package com.zl.face;

import android.content.Context;
import android.graphics.Rect;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FaceDetector {

    static {
        System.loadLibrary("faceLibrary");
    }

    /**
     * 初始化SDK的模型
     *
     * @param mContext 上下文环境
     * @return 初始话结果
     */
    public boolean init(Context mContext) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mnn";
        String modelPath = copyModel2SD(mContext, "ultra", path);
        String mnnPath = modelPath + File.separator + "RFB-320.mnn";
        boolean ret = initDetector(mnnPath, 320, 240, 2, true);
        if (!ret) {
            return false;
        }
        return true;
    }

    /**
     * 初始化算法
     *
     * @param mnnPath      模型文件地址
     * @param resizeWidth  图片压缩宽
     * @param resizeHeight 图片压缩高
     * @param numThread    线程数
     * @param openCL       是否打开opencl，默认打开
     * @return 人脸信息
     */
    public native boolean initDetector(String mnnPath, int resizeWidth, int resizeHeight, int numThread, boolean openCL);

    /**
     * 解析人脸信息
     *
     * @param imgPath 图片地址
     * @return 人脸信息
     */
    public native FaceInfo[] detectFile(String imgPath);

    /**
     * 解析人脸信息
     *
     * @param imgPath 图片地址
     * @return 人脸信息
     */
    public native float[] detectPic(String imgPath);


    /**
     * 解析人脸信息
     *
     * @param yuv    摄像图输出的预览帧
     * @param width  帧的宽度
     * @param height 帧的高度
     * @return 人像信息
     * 使用这个方法，需要将摄像图帧旋转至0度
     */
    public native FaceInfo[] detectYuv(byte[] yuv, int width, int height);

    /**
     * 解析人脸信息
     *
     * @param yuv    摄像图输出的预览帧
     * @param width  帧的宽度
     * @param height 帧的高度
     * @return 人像信息
     * 使用这个方法，需要将摄像图帧旋转至0度
     */
    public native float[] detectYUV(byte[] yuv, int width, int height);


    public List<FaceInfo> detectYuvImg(byte[] yuv, int width, int height) {
        float[] infos = detectYUV(yuv, width, height);
        return transformFaceInfo(infos);
    }
    /**
     * 初始化SDK的模型
     *
     * @param mContext 上下文环境
     * @return 初始话结果
     */
    public boolean initIR(Context mContext) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "mnn";
        String modelPath = copyModel2SD(mContext, "ultra", path);
        String mnnPath = modelPath + File.separator + "slim-320.mnn";
        boolean ret = initIRDetector(mnnPath, 320, 240, 2, true);
        if (!ret) {
            return false;
        }
        return true;
    }

    /**
     * 初始化算法
     *
     * @param mnnPath      模型文件地址
     * @param resizeWidth  图片压缩宽
     * @param resizeHeight 图片压缩高
     * @param numThread    线程数
     * @param openCL       是否打开opencl，默认打开
     * @return 人脸信息
     */
    public native boolean initIRDetector(String mnnPath, int resizeWidth, int resizeHeight, int numThread, boolean openCL);

    /**
     * 解析人脸信息
     *
     * @param imgPath 图片地址
     * @return 人脸信息
     */
    public native FaceInfo[] detectIRFile(String imgPath);


    /**
     * 解析人脸信息
     *
     * @param yuv    摄像图输出的预览帧
     * @param width  帧的宽度
     * @param height 帧的高度
     * @return 人像信息
     * 使用这个方法，需要将摄像图帧旋转至0度
     */
    public native FaceInfo[] detectIRYuv(byte[] yuv, int width, int height);

    /**
     * 解析人脸信息
     *
     * @param yuv    摄像图输出的预览帧
     * @param width  帧的宽度
     * @param height 帧的高度
     * @return 人像信息
     * 使用这个方法，需要将摄像图帧旋转至0度
     */
    public native float[] detectIRYUV(byte[] yuv, int width, int height);

    public List<FaceInfo> detectIRYuvImg(byte[] yuv, int width, int height) {
        float[] infos = detectIRYUV(yuv, width, height);
        return transformFaceInfo(infos);
    }

    public static List<FaceInfo> transformFaceInfo(float[] infos) {
        ArrayList<FaceInfo> faceInfos = new ArrayList<>();
        if (null == infos || infos.length < 5) {
            return faceInfos;
        }
        int size = infos.length / 5;
        for (int i = 0; i < size; i++) {
            int index = i * 5;
            float left = infos[index];
            float top = infos[index + 1];
            float right = infos[index + 2];
            float bottom = infos[index + 3];
            float score = infos[index + 4];
            Rect rect = new Rect((int) left, (int) top, (int) right, (int) bottom);
            FaceInfo faceInfo = new FaceInfo();
            faceInfo.setFaceRect(rect);
            faceInfo.setScore(score);
            faceInfos.add(faceInfo);
        }
        return faceInfos;
    }

    private String copyModel2SD(Context mContext, String model, String path) {
        String modelPath = path + File.separator + model;
        File file = new File(modelPath);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            copyAssets(mContext, model, modelPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return modelPath;
    }

    /**
     * 复制asset文件到指定目录
     *
     * @param mContext 上下文环境
     * @param oldPath  asset下的路径
     * @param newPath  SD卡下保存路径
     * @throws IOException 文件拷贝异常
     */
    public static void copyAssets(Context mContext, String oldPath, String newPath) throws IOException {
        String fileNames[] = mContext.getAssets().list(oldPath);
        if (fileNames.length > 0) {
            File file = new File(newPath);
            file.mkdirs();
            for (String fileName : fileNames) {
                copyAssets(mContext, oldPath + File.separator + fileName, newPath + File.separator + fileName);
            }
        } else {
            InputStream is = mContext.getAssets().open(oldPath);
            FileOutputStream fos = new FileOutputStream(new File(newPath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {
                fos.write(buffer, 0, byteCount);
            }
            fos.flush();
            is.close();
            fos.close();
        }
    }
}
