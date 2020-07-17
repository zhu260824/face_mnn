# face_mnn
Android 人脸检测和识别

[![](https://jitpack.io/v/com.gitee.zhu260824/face_mnn.svg)](https://jitpack.io/#com.gitee.zhu260824/face_mnn)

#### 开源算法说明
- 推理算法：[MNN](https://github.com/alibaba/MNN)
- 检测算法：[Ultra](https://github.com/Linzaer/Ultra-Light-Fast-Generic-Face-Detector-1MB)
- 识别算法：mobilefacenet

#### 使用
-  添加依赖
    1. Add it in your root build.gradle at the end of repositories:
        ```
        allprojects {
        	repositories {
	            	...
	        	maven { url 'https://jitpack.io' }
        	}
        }
        ```
    2. Add the dependency
        ```
        dependencies {
            implementation 'com.gitee.zhu260824:face_mnn:xxxxx'
        }
        ```
- 代码中使用
    1. 初始化SDK（默认初始化）
        ```
        /**
        * 初始化SDK的模型
        *
        * @param mContext 上下文环境
        * @return 初始话结果
        */
        public boolean init(Context mContext){}
        ```
        单独初始化
        ```
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
        ```
    2. 使用
        ```
        /**
        * 解析人脸信息
        *
        * @param imgPath 图片地址
        * @return 人脸信息
        */
        public native FaceInfo[] detect(String imgPath);
        /**
        * 解析人脸信息
        *
        * @param yuv    摄像图输出的预览帧
        * @param width  帧的宽度
        * @param height 帧的高度
        * @deprecated 使用这个方法，需要将摄像图帧旋转至0度
        */
        public native List<FaceInfo> detectYuvImg(byte[] yuv, int width, int height);
        ```
- 根据使用设备减少so包，缩小apk大小
    ```
    android {
        ...
        defaultConfig {
           ...
            ndk {
                abiFilters "armeabi-v7a", "arm64-v8a" 
            }
        }
    }
    ```
#### 总结