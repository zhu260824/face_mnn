//
// Created by 朱林 on 2020/5/9.
//

#ifndef FACE_MNN_BASE_UTIL_H
#define FACE_MNN_BASE_UTIL_H

#include <string>
#include <vector>
#include <jni.h>

typedef struct FaceInfo {
    float x1;
    float y1;
    float x2;
    float y2;
    float score;
} FaceInfo;

std::string getDirPath(JNIEnv *env, jstring dirPath);

std::string getFilePath(JNIEnv *env, jstring filePath);

jobjectArray native2JavaFacInfo(JNIEnv *env, std::vector<FaceInfo> face_info,
                                jobjectArray faceArgs, int32_t num_face);

#endif //FACE_MNN_BASE_UTIL_H
