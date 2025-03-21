package com.imes.base.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.imes.module_base.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by admin on 2015/12/23.
 */
public class CaptureActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = "CaptureActivity";

    public static final String BARCODE = "CaptureActivity.BARCODE";
    public static final String KEY_EXTERNAL_DATA = "CaptureActivity。DATA";
    private ImageView backTv;
    private TextView imageTv;

    private ZXingScannerFragment mFragment;
    private String mScanType = ZXingScannerFragment.SCANNER_TYPE_ALL;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.lib_barcode_capture_main);
        mScanType = getIntent().getStringExtra("scanType");
        backTv = (ImageView) findViewById(R.id.capture_main_back);
        imageTv = (TextView) findViewById(R.id.capture_btn);
        backTv.setOnClickListener(this);
        imageTv.setOnClickListener(this);

        mFragment = new ZXingScannerFragment();
        mFragment.setScannerType(mScanType);
        mFragment.setListener(new ZXingScannerListener() {
            @Override
            public void onResult(String result) {
                Intent intent = new Intent();
                intent.putExtra(BARCODE, result);
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onFlashClick(boolean isOpen) {

            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.capture_main_scanner, mFragment)
                .commitAllowingStateLoss();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.capture_main_back) {
            finish();
        } else if (v.getId() == R.id.capture_btn) {
            mFragment.chooseFromAlbum();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        mFragment.onActivityResult(requestCode, resultCode, intent);
    }
}
