package com.example.mymodule;

import androidx.activity.ComponentActivity;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;

import com.example.mymodule.databinding.ActivityMainBinding;
import com.imes.base.mvp.BaseMVPActivity;
import com.imes.base.mvp.BasePresenter;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MyModule myModule;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        myModule = new ViewModelProvider(this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MyModule.class);
        binding.setMm(myModule);
        binding.setLifecycleOwner(this);
    }
    
}