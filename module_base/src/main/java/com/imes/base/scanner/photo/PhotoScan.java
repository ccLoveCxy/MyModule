package com.imes.base.scanner.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.imes.base.scanner.core.ZXingScannerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class PhotoScan {

    private List<BarcodeFormat> mFormats;
    private List<Integer> degressList = new ArrayList<Integer>();
    private MultiFormatReader mReader;

    public PhotoScan(List<BarcodeFormat> formats) {
        mFormats = formats;
        initReader();
        degressList.add(0);
        degressList.add(270);
        degressList.add(180);
        degressList.add(90);
    }

    private void initReader(){
        Map<DecodeHintType,Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, getFormats());
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
        mReader = new MultiFormatReader();
        mReader.setHints(hints);
    }

    public Collection<BarcodeFormat> getFormats() {
        if(mFormats == null) {
            return ZXingScannerView.ALL_FORMATS;
        }
        return mFormats;
    }


    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 600);
        if (sampleSize <= 0) {
            sampleSize = 1;
        }
        options.inSampleSize = sampleSize;
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        Result result = null;
        for (Integer degress : degressList) {
            result = parseBitmap(scanBitmap, degress);
            if (null != result) {
                break;
            }
        }
        return result;
    }

    private Result parseBitmap(Bitmap scanBitmap, int degress) {
        try {
            if (degress > 0) {
                scanBitmap = rotateImage(scanBitmap, degress);
            }
            RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            return mReader.decodeWithState(binaryBitmap);
        } catch (Exception e) {
        } catch (OutOfMemoryError e1) {
        }
        return null;
    }

    //旋转Bitmap再次识别
    private Bitmap rotateImage(Bitmap bitmap, int degress) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degress);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
