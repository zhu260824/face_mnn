//  Created by Linzaer on 2019/11/15.
//  Copyright © 2019 Linzaer. All rights reserved.

#ifndef UltraFace_hpp
#define UltraFace_hpp

#pragma once

#include "base_util.h"
#include "MNN/Interpreter.hpp"
#include "MNN/MNNDefine.h"
#include "MNN/Tensor.hpp"
#include "MNN/ImageProcess.hpp"
#include "opencv2/opencv.hpp"
#include <algorithm>
#include <iostream>
#include <string>
#include <vector>
#include <memory>
#include <chrono>


#define num_featuremap 4
#define hard_nms 1
#define blending_nms 2 /* mix nms was been proposaled in paper blaze face, aims to minimize the temporal jitter*/

class UltraFace {
public:
    UltraFace();

    ~UltraFace();

    bool init(const std::string &mnn_path, int resizeWidth = 320, int resizeHeight = 240,
              int num_thread_ = 4, float score_threshold_ = 0.7, float iou_threshold_ = 0.3,
              bool openCL = true);

    std::vector<FaceInfo> detect(cv::Mat &img);

private:
    std::shared_ptr<MNN::Interpreter> ultraface_interpreter;

    MNN::Session *ultraface_session = nullptr;

    MNN::Tensor *input_tensor = nullptr;

    int num_thread = 4;
    int resize_w = 320;
    int resize_h = 240;
    float score_threshold = 0.7;
    float iou_threshold = 0.3;
    const float mean_vals[3] = {127, 127, 127};
    const float norm_vals[3] = {1.0 / 128, 1.0 / 128, 1.0 / 128};
    const float center_variance = 0.1;
    const float size_variance = 0.2;
    const std::vector<std::vector<float>> min_boxes = {
            {10.0f,  16.0f,  24.0f},
            {32.0f,  48.0f},
            {64.0f,  96.0f},
            {128.0f, 192.0f, 256.0f}};
    const std::vector<float> strides = {8.0, 16.0, 32.0, 64.0};
    int num_anchors;
    std::vector<std::vector<float>> priors = {};

    void generateBBox(std::vector<FaceInfo> &bbox_collection, int img_width, int img_height,
                      MNN::Tensor *scores, MNN::Tensor *boxes);

    void nms(std::vector<FaceInfo> &input, std::vector<FaceInfo> &output, int type = blending_nms);

};

#endif /* UltraFace_hpp */
