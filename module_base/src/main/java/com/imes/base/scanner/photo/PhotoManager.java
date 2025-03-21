package com.imes.base.scanner.photo;

import android.app.Activity;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.imes.base.scanner.ZXingScannerFragment;
import com.imes.base.scanner.ZXingScannerListener;

import java.util.List;

public class PhotoManager {

    private Activity mActivity;
    private ZXingScannerListener mListener;
    private String mScannerType = ZXingScannerFragment.SCANNER_TYPE_ALL;
    private List<BarcodeFormat> mFormats;
    private String mHintText = "未找到二维码/条码，请重新选择或确认二维码/条码是否清晰";

    public PhotoManager(Activity activity) {
        mActivity = activity;
    }

    public void setScannerTypeAndFormats(String scannerType, List<BarcodeFormat> formats) {
        mScannerType = scannerType;
        mFormats = formats;
        if(ZXingScannerFragment.SCANNER_TYPE_BARCODE.equals(mScannerType)){
            mHintText = "未找到条码，请重新选择或确认条码是否清晰";
        }else if(ZXingScannerFragment.SCANNER_TYPE_QRCODE.equals(mScannerType)){
            mHintText = "未找到二维码，请重新选择或确认二维码是否清晰";
        }
    }

    public void openPhoto(int REQUEST_CODE) {
        try {
//            Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
//            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
//            innerIntent.setType("image/*");
//            Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
//            mActivity.startActivityForResult(wrapperIntent, REQUEST_CODE);
            Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
            innerIntent.setAction(Intent.ACTION_PICK);
            innerIntent.setType("image/*");
            mActivity.startActivityForResult(innerIntent, REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(mActivity, "相册打开失败", Toast.LENGTH_SHORT).show();
        }

    }

    public void dealPhoto(Intent data, ZXingScannerListener listener) {
        mListener = listener;
        String photoPath = null;
        try {
            photoPath = PhotoUtils.getImagePath(mActivity, data.getData());
        } catch (Exception e) {
            Toast.makeText(mActivity, "获取图片失败，请尝试通过相机扫描。", Toast.LENGTH_SHORT).show();
        }

        new Thread(new MyRunnable(photoPath)).start();
    }

    public class MyRunnable implements Runnable {
        private String mPath;

        public MyRunnable(String path) {
            mPath = path;
        }

        @Override
        public void run() {
            Result result = null;
            try {
                result = new PhotoScan(mFormats).scanningImage(mPath);
            } catch (Exception e) {
            }
            onResult(result);
        }
    }

    private void onResult(Result result) {
        if (result == null || TextUtils.isEmpty(result.toString())) {

            Looper.prepare();
            Toast.makeText(mActivity, mHintText, Toast.LENGTH_SHORT).show();
            Looper.loop();
        } else {
            String recode = PhotoRecode.recode(result.toString());
            if (mListener != null) {
                mListener.onResult(recode);
            }
        }
    }

}
