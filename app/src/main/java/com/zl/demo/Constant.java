package com.zl.demo;

import android.os.Environment;

import java.io.File;

public class Constant {
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final String CACHE_PATH=BASE_PATH+File.separator+"Alisports"+File.separator+"gamepass";

}
