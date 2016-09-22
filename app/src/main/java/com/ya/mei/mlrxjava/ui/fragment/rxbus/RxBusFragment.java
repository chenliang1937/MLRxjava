package com.ya.mei.mlrxjava.ui.fragment.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ya.mei.mlrxjava.R;

import butterknife.ButterKnife;

/**
 * Created by chenliang3 on 2016/3/14.
 */
public class RxBusFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxbus, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rxbus_frag_1, new RxBusTopFragment())
                .replace(R.id.rxbus_frag_2, new RxBusBottomFragment())
                .commit();
    }

    public static class TapEvent {}

}
