package com.ya.mei.mlrxjava.ui.fragment.rxbus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.rxbus.RxBus;
import com.ya.mei.mlrxjava.ui.activity.ShellActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chenliang3 on 2016/3/14.
 */
public class RxBusBottomFragment extends Fragment {

    @Bind(R.id.rxbus_tap_txt)
    TextView tapEventTxtShow;
    @Bind(R.id.rxbus_tap_count)
    TextView tapEventCountShow;

    private RxBus rxBus;
    private CompositeSubscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rxbus_bottom, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rxBus = ((ShellActivity)getActivity()).getRxBusSingleton();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscription = new CompositeSubscription();

        ConnectableObservable<Object> tapEventEmitter = rxBus.toObserverable().publish();

        subscription.add(tapEventEmitter.subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof RxBusFragment.TapEvent) {
                    showTapText();
                }
            }
        }));

        subscription.add(tapEventEmitter.publish(new Func1<Observable<Object>, Observable<List<Object>>>() {
            @Override
            public Observable<List<Object>> call(Observable<Object> stream) {
                return stream.buffer(stream.debounce(1, TimeUnit.SECONDS));
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<List<Object>>() {
                    @Override
                    public void call(List<Object> taps) {
                        showTapCount(taps.size());
                    }
                })
        );

        subscription.add(tapEventEmitter.connect());
    }

    @Override
    public void onStop() {
        super.onStop();
        subscription.clear();
    }

    private void showTapCount(int size) {
        tapEventCountShow.setText(String.valueOf(size));
        tapEventCountShow.setVisibility(View.VISIBLE);
        tapEventCountShow.setScaleX(1f);
        tapEventCountShow.setScaleY(1f);
        ViewCompat.animate(tapEventCountShow)
                .scaleXBy(-1f)
                .scaleYBy(-1f)
                .setDuration(800)
                .setStartDelay(100);
    }

    private void showTapText() {
        tapEventTxtShow.setVisibility(View.VISIBLE);
        tapEventTxtShow.setAlpha(1f);
        ViewCompat.animate(tapEventTxtShow).alphaBy(-1f).setDuration(400);
    }
}
