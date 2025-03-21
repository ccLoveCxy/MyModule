package com.imes.base.scanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.zxing.Result;
import com.imes.base.scanner.core.ZXingScannerView;
import com.imes.base.scanner.photo.PhotoManager;
import com.imes.module_base.R;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 扫一扫
 *
 */

public class ZXingScannerFragment extends Fragment implements SensorEventListener, ZXingScannerView.ResultHandler, View.OnClickListener {

    public static final int REQUEST_CODE_CHOOSE_PHOTO = 1011;
    /**
     * 扫描类型：二维码+条形码
     */
    public static final String SCANNER_TYPE_ALL = "all";
    /**
     * 扫描类型：仅条形码
     */
    public static final String SCANNER_TYPE_BARCODE = "barCode";
    /**
     * 扫描类型：仅二维码
     */
    public static final String SCANNER_TYPE_QRCODE = "qrCode";
    private ZXingScannerView mZXingScannerView;
    private TextView mHintTv;
    private TextView mFlashTv;
    private String mScannerType = SCANNER_TYPE_ALL;

    private PhotoManager mPhotoManager;

    //手电筒开关：true：打开；false：关闭
    private boolean mFlash = false;
    private SensorManager mSensorManager;

    private ZXingScannerListener mListener;
    private Spanned mHint = null;

    /**
     * 设置结果回调
     *
     * @param listener
     */
    public void setListener(ZXingScannerListener listener) {
        mListener = listener;
    }

    /**
     * 设置提示语内容
     *
     * @param hint
     */
    public void setHint(Spanned hint) {
        mHint = hint;
        if (mHintTv != null) {
            mHintTv.setText(mHint);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mZXingScannerView.startCamera();
        mZXingScannerView.setResultHandler(this);
    }

    /**
     * 设置扫描类型
     *
     * @param scannerType SCANNER_TYPE_ALL，SCANNER_TYPE_BARCODE，SCANNER_TYPE_QRCODE
     */
    public void setScannerType(String scannerType) {
        mScannerType = scannerType;
        if (mZXingScannerView != null) {
            setScannerType();
        }
    }

    /**
     * 从相册选择照片</br>
     * 注意：需要在Activity的onActivityResult回调里调用Fragment的onActivityResult方法
     */
    public void chooseFromAlbum() {
        if (getActivity() != null) {
            mPhotoManager.openPhoto(REQUEST_CODE_CHOOSE_PHOTO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.lib_fragment_zxing_scanner, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mZXingScannerView = (ZXingScannerView) view.findViewById(R.id.m_lib_zxing_scanner_view);
        mHintTv = (TextView) view.findViewById(R.id.m_lib_zxing_scanner_hint);
        mFlashTv = (TextView) view.findViewById(R.id.m_lib_zxing_scanner_flash);
        mPhotoManager = new PhotoManager(getActivity());
        initView();
    }

    private void initView() {
        // this paramter will make your HUAWEI phone works great!
        mZXingScannerView.setAspectTolerance(0.5f);
        setScannerType();
        checkLight();
        if (!TextUtils.isEmpty(mHint)) {
            mHintTv.setText(mHint);
        }
        WindowManager wm = (WindowManager) getActivity()
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        //底层代码，扫描框宽度为屏幕*0.625
        float offset = (outMetrics.widthPixels * 0.625f) / 2f;
        mHintTv.setTranslationY(-offset);
        mFlashTv.setTranslationY(offset);
        mFlashTv.setOnClickListener(this);
        mZXingScannerView.setFlash(false);
    }

    private void setScannerType(){
        if(SCANNER_TYPE_BARCODE.equals(mScannerType)){
            mZXingScannerView.setFormats(ZXingScannerView.ONED_FORMATS);
            setDefaultHint("将条码放入框内，即可自动扫描");
            mPhotoManager.setScannerTypeAndFormats(mScannerType, ZXingScannerView.ONED_FORMATS);
        }else if(SCANNER_TYPE_QRCODE.equals(mScannerType)){
            mZXingScannerView.setFormats(ZXingScannerView.TWOD_FORMATS);
            setDefaultHint("将二维码放入框内，即可自动扫描");
            mPhotoManager.setScannerTypeAndFormats(mScannerType, ZXingScannerView.TWOD_FORMATS);
        }else{
            mZXingScannerView.setFormats(ZXingScannerView.ALL_FORMATS);
            setDefaultHint("将二维码/条码放入框内，即可自动扫描");
            mPhotoManager.setScannerTypeAndFormats(mScannerType, ZXingScannerView.ALL_FORMATS);
        }
    }

    private void setDefaultHint(String hint) {
        if (mHintTv != null && TextUtils.isEmpty(mHint)) {
            mHintTv.setText(hint);
        }
    }

    private void checkLight() {
        try {
            //检测是否是弱光环境
            mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            //第二步：获取 Sensor 传感器类型
            Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            //第四步：注册 SensorEventListener
            mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.m_lib_zxing_scanner_flash) {
            switchFlash();
        }
    }

    private void switchFlash() {
        try {
            if (mFlash) {
                mZXingScannerView.setFlash(false);
                mFlashTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.lib_flashlight_clode, 0, 0);
                mFlash = false;
            } else {
                mZXingScannerView.setFlash(true);
                mFlashTv.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.lib_flashlight_open, 0, 0);
                mFlash = true;
            }
            if (mListener != null) {
                mListener.onFlashClick(mFlash);
            }
        } catch (Exception e) {
        }
    }

    private void unregisterSensorListener() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //提示当前光照强度
        try {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                float v = event.values[0];
                if (v <= 10.0f && !mFlash) {
                    mZXingScannerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            switchFlash();
                        }
                    },1000);
                }
                unregisterSensorListener();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(mZXingScannerView != null){
            mZXingScannerView.resumeCameraPreview(this);
            mZXingScannerView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mZXingScannerView != null){
            mZXingScannerView.stopCamera();
        }
    }

    @Override
    public void onDestroy() {
        unregisterSensorListener();
        super.onDestroy();
        if(mZXingScannerView != null){
            mZXingScannerView.destroy();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mZXingScannerView != null){
            mZXingScannerView.stopCamera();
        }

    }

    @Override
    public void handleResult(Result result) {
        mZXingScannerView.resumeCameraPreview(this);
        if (mListener != null) {
            mListener.onResult(result.getText());
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHOOSE_PHOTO) {
            Activity activity = getActivity();
            if (resultCode == Activity.RESULT_OK && data != null && activity != null) {
                mPhotoManager.dealPhoto(data, mListener);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
