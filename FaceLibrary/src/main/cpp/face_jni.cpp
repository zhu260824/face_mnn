//
// Created by 朱林 on 2020/5/8.
//
#include <android/log.h>
#include <android/bitmap.h>
#include <jni.h>
#include <string>
#include <vector>
#include <iostream>
#include "base_util.h"
#include "UltraFace.hpp"
#include "opencv2/imgproc/types_c.h"

#define TAG "Face"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)
UltraFace *detector;
UltraFace *irDetector;

extern "C"
JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    return JNI_VERSION_1_6;
}

extern "C"
JNIEXPORT void JNI_OnUnload(JavaVM *vm, void *reserved) {
    if (NULL != detector) {
        detector->~UltraFace();
    }
    if (NULL != irDetector) {
        irDetector->~UltraFace();
    }
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_zl_face_FaceDetector_initDetector(JNIEnv *env, jobject thiz, jstring mnn_path,
                                           jint resize_width, jint resize_height, jint num_thread,
                                           jboolean open_cl) {
    detector = new UltraFace();
    std::string mnnPath = getFilePath(env, mnn_path);
    float score_threshold = 0.7;
    float iou_threshold = 0.3;
    return detector->init(mnnPath, resize_width, resize_height, num_thread, score_threshold,
                          iou_threshold, open_cl);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_zl_face_FaceDetector_detectFile(JNIEnv *env, jobject thiz, jstring img_path) {
    jobjectArray faceArgs = nullptr;
    const char *imgPath = env->GetStringUTFChars(img_path, 0);
    cv::Mat cv_img = cv::imread(imgPath);
    std::vector<FaceInfo> face_info = detector->detect(cv_img);
    int32_t num_face = static_cast<int32_t>(face_info.size());
    LOGD("检测到的人脸数目：%d\n", num_face);
    faceArgs = native2JavaFacInfo(env, face_info, faceArgs, num_face);
    return faceArgs;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_zl_face_FaceDetector_detectYuv(JNIEnv *env, jobject thiz,
                                        jbyteArray yuv, jint width, jint height) {
    jobjectArray faceArgs = nullptr;
    jbyte *pBuf = (jbyte *) env->GetByteArrayElements(yuv, 0);
    cv::Mat image = cv::Mat(height + height / 2, width, CV_8UC1, (unsigned char *) pBuf);
    cv::Mat mBgr;
    cvtColor(image, mBgr, CV_YUV2BGR_NV21);
    std::vector<FaceInfo> face_info = detector->detect(mBgr);
    int32_t num_face = static_cast<int32_t>(face_info.size());
    LOGD("检测到的人脸数目：%d\n", num_face);
    jclass faceClass = env->FindClass("com/zl/face/FaceInfo");
    jmethodID faceClassInitID = (env)->GetMethodID(faceClass, "<init>", "()V");
    jfieldID faceScore = env->GetFieldID(faceClass, "score", "F");
    jfieldID faceRect = env->GetFieldID(faceClass, "faceRect", "Landroid/graphics/Rect;");
    jclass rectClass = env->FindClass("android/graphics/Rect");
    jmethodID rectClassInitID = (env)->GetMethodID(rectClass, "<init>", "()V");
    jfieldID rect_left = env->GetFieldID(rectClass, "left", "I");
    jfieldID rect_top = env->GetFieldID(rectClass, "top", "I");
    jfieldID rect_right = env->GetFieldID(rectClass, "right", "I");
    jfieldID rect_bottom = env->GetFieldID(rectClass, "bottom", "I");
    faceArgs = (env)->NewObjectArray(num_face, faceClass, 0);
    for (int i = 0; i < num_face; i++) {
        float score = face_info[i].score;
        int row1 = face_info[i].x1;
        int col1 = face_info[i].y1;
        int row2 = face_info[i].x2;
        int col2 = face_info[i].y2;
        jobject newFace = (env)->NewObject(faceClass, faceClassInitID);
        jobject newRect = (env)->NewObject(rectClass, rectClassInitID);
        (env)->SetIntField(newRect, rect_left, row1);
        (env)->SetIntField(newRect, rect_top, col1);
        (env)->SetIntField(newRect, rect_right, row2);
        (env)->SetIntField(newRect, rect_bottom, col2);
        (env)->SetObjectField(newFace, faceRect, newRect);
        (env)->SetFloatField(newFace, faceScore, score);
        (env)->SetObjectArrayElement(faceArgs, i, newFace);
    }
    return faceArgs;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_com_zl_face_FaceDetector_initIRDetector(JNIEnv *env, jobject thiz, jstring mnn_path,
                                             jint resize_width, jint resize_height, jint num_thread,
                                             jboolean open_cl) {
    irDetector = new UltraFace();
    std::string mnnPath = getFilePath(env, mnn_path);
    float score_threshold = 0.7;
    float iou_threshold = 0.3;
    return irDetector->init(mnnPath, resize_width, resize_height, num_thread, score_threshold,
                            iou_threshold, open_cl);
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_zl_face_FaceDetector_detectIRFile(JNIEnv *env, jobject thiz, jstring img_path) {
    jobjectArray faceArgs = nullptr;
    const char *imgPath = env->GetStringUTFChars(img_path, 0);
    cv::Mat cv_img = cv::imread(imgPath);
    std::vector<FaceInfo> face_info = irDetector->detect(cv_img);
    int32_t num_face = static_cast<int32_t>(face_info.size());
    LOGD("IR-->检测到的人脸数目：%d\n", num_face);
    faceArgs = native2JavaFacInfo(env, face_info, faceArgs, num_face);
    return faceArgs;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_com_zl_face_FaceDetector_detectIRYuv(JNIEnv *env, jobject thiz, jbyteArray yuv, jint width,
                                          jint height) {
    jobjectArray faceArgs = nullptr;
    jbyte *pBuf = (jbyte *) env->GetByteArrayElements(yuv, 0);
    cv::Mat image = cv::Mat(height + height / 2, width, CV_8UC1, (unsigned char *) pBuf);
    cv::Mat mBgr;
    cvtColor(image, mBgr, CV_YUV2BGR_NV21);
    std::vector<FaceInfo> face_info = irDetector->detect(mBgr);
    int32_t num_face = static_cast<int32_t>(face_info.size());
    LOGD("IR-->检测到的人脸数目：%d\n", num_face);
    jclass faceClass = env->FindClass("com/zl/face/FaceInfo");
    jmethodID faceClassInitID = (env)->GetMethodID(faceClass, "<init>", "()V");
    jfieldID faceScore = env->GetFieldID(faceClass, "score", "F");
    jfieldID faceRect = env->GetFieldID(faceClass, "faceRect", "Landroid/graphics/Rect;");
    jclass rectClass = env->FindClass("android/graphics/Rect");
    jmethodID rectClassInitID = (env)->GetMethodID(rectClass, "<init>", "()V");
    jfieldID rect_left = env->GetFieldID(rectClass, "left", "I");
    jfieldID rect_top = env->GetFieldID(rectClass, "top", "I");
    jfieldID rect_right = env->GetFieldID(rectClass, "right", "I");
    jfieldID rect_bottom = env->GetFieldID(rectClass, "bottom", "I");
    faceArgs = (env)->NewObjectArray(num_face, faceClass, 0);
    for (int i = 0; i < num_face; i++) {
        float score = face_info[i].score;
        int row1 = face_info[i].x1;
        int col1 = face_info[i].y1;
        int row2 = face_info[i].x2;
        int col2 = face_info[i].y2;
        jobject newFace = (env)->NewObject(faceClass, faceClassInitID);
        jobject newRect = (env)->NewObject(rectClass, rectClassInitID);
        (env)->SetIntField(newRect, rect_left, row1);
        (env)->SetIntField(newRect, rect_top, col1);
        (env)->SetIntField(newRect, rect_right, row2);
        (env)->SetIntField(newRect, rect_bottom, col2);
        (env)->SetObjectField(newFace, faceRect, newRect);
        (env)->SetFloatField(newFace, faceScore, score);
        (env)->SetObjectArrayElement(faceArgs, i, newFace);
    }
    return faceArgs;
}