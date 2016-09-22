package com.ya.mei.mlrxjava.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.view.ViewClickEvent;
import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.ui.adapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by chenliang3 on 2016/3/11.
 *
 * 使用buffer统计累计事件
 */
public class BufferFragment extends Fragment {

    @Bind(R.id.list_threading_log)
    ListView logsList;
    @Bind(R.id.btn_start_operation)
    Button tapButton;

    private LogAdapter adapter;
    private List<String> logs;
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buffer, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();
    }

    @Override
    public void onStart() {
        super.onStart();
        subscription = getBufferedSubscription();
    }

    @Override
    public void onPause() {
        super.onPause();
        subscription.unsubscribe();
    }

    private Subscription getBufferedSubscription(){
        return RxView.clickEvents(tapButton)
                .map(new Func1<ViewClickEvent, Integer>() {
                    @Override
                    public Integer call(ViewClickEvent viewClickEvent) {
                        Timber.d("--------GOT A TAP");
                        printLog("GOT A TAP");
                        return 1;
                    }
                })
                .buffer(2, TimeUnit.SECONDS)//2s
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onCompleted() {
                        //you'll never reach here
                        Timber.d("-------onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "----------oops on error");
                        printLog("Dang error! check your logs");
                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        Timber.d("----------onNext");
                        if (integers.size() > 0){
                            printLog(String.format("%d taps", integers.size()));
                        }else {
                            Timber.d("----------No taps received");
                        }
                    }
                });
    }

    private void setupLogger(){
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(), new ArrayList<String>());
        logsList.setAdapter(adapter);
    }

    private boolean isCurrentlyOnMainThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private void printLog(String logsMsg){
        if (isCurrentlyOnMainThread()){
            logs.add(0, logsMsg + " (main thread) ");
            adapter.clear();
            adapter.addAll(logs);
        }else {
            logs.add(0, logsMsg + " (NOT main thread) ");

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.addAll(logs);
                }
            });
        }
    }

}
