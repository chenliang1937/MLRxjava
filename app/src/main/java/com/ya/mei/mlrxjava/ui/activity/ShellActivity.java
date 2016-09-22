package com.ya.mei.mlrxjava.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.rxbus.RxBus;
import com.ya.mei.mlrxjava.ui.fragment.BufferFragment;
import com.ya.mei.mlrxjava.ui.fragment.ConcurrencyWithSchedulersFragment;
import com.ya.mei.mlrxjava.ui.fragment.DebounceSearchEmitterFragment;
import com.ya.mei.mlrxjava.ui.fragment.DoubleBindingTextViewFragment;
import com.ya.mei.mlrxjava.ui.fragment.FormValidationCombineLatestFragment;
import com.ya.mei.mlrxjava.ui.fragment.RetrofitFragment;
import com.ya.mei.mlrxjava.ui.fragment.rxbus.RxBusFragment;

/**
 * Created by chenliang3 on 2016/3/10.
 */
public class ShellActivity extends AppCompatActivity{

    private int config;

    private RxBus rxBus = null;
    //this is better done with a DI Library like Dagger
    public RxBus getRxBusSingleton(){
        if (rxBus == null){
            rxBus = new RxBus();
        }
        return rxBus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shell);

        config = getIntent().getIntExtra("Config", 0);
        switch (config){
            case 0:
                setFragment(new ConcurrencyWithSchedulersFragment());
                break;
            case 1:
                setFragment(new BufferFragment());
                break;
            case 2:
                setFragment(new DebounceSearchEmitterFragment());
                break;
            case 3:
                setFragment(new RetrofitFragment());
                break;
            case 4:
                setFragment(new DoubleBindingTextViewFragment());
                break;
            case 5:
                setFragment(new RxBusFragment());
                break;
            case 6:
                setFragment(new FormValidationCombineLatestFragment());
                break;
        }
    }

    private void setFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.shell_container, fragment).commit();
    }
}
