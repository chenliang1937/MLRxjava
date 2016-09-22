package com.ya.mei.mlrxjava.ui.fragment.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.rxbus.RxBus;
import com.ya.mei.mlrxjava.ui.activity.ShellActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chenliang3 on 2016/3/14.
 */
public class RxBusTopFragment extends Fragment {

    private RxBus rxBus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxbus_top, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rxBus = ((ShellActivity)getActivity()).getRxBusSingleton();
    }

    @OnClick(R.id.btn_rxbus_tap)
    public void onTapButtonClicked(){
        if (rxBus.hasObservers()){
            rxBus.send(new RxBusFragment.TapEvent());
        }
    }
}
