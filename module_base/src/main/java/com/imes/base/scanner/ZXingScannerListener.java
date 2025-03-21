package com.imes.base.scanner;

/**
 * Created by yjp on 2017/10/19.
 */

public interface ZXingScannerListener {
    /**
     * 扫描结果
     *
     * @param result 扫描结果
     */
    public void onResult(String result);

    /**
     * 手电筒操作
     *
     * @param isOpen true:打开；false:关闭
     */
    public void onFlashClick(boolean isOpen);
}
