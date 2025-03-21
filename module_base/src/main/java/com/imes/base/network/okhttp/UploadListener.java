package com.imes.base.network.okhttp;

import java.io.File;

public abstract class UploadListener<T> implements RequestListener<T> {

    public abstract void uploadFailed(File file,Throwable e);
}