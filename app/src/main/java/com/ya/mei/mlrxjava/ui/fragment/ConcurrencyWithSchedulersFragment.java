package com.ya.mei.mlrxjava.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.ui.adapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by chenliang3 on 2016/3/10.
 *
 * 并发性调度
 */
public class ConcurrencyWithSchedulersFragment extends Fragment {

    @Bind(R.id.progress_operation_running)
    ProgressBar progressBar;
    @Bind(R.id.list_threading_log)
    ListView logList;

    private LogAdapter adapter;
    private List<String> logs;
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_concurrency_schedulers, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();
    }

    @OnClick(R.id.btn_start_operation)
    public void startLongOperation(){
        progressBar.setVisibility(View.VISIBLE);
        printLog("Button Clicked");

        Observable<Boolean> observable = getObservable();
        subscription = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getObserver());
    }

    private Observable<Boolean> getObservable(){
        return Observable.just(true).map(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean aBoolean) {
                printLog("Within Observable");//在非主线程运行
                doSomeLongOperation_thatBlocksCurrentThread();
                return aBoolean;
            }
        });
    }

    private Observer<Boolean> getObserver(){
        return new Observer<Boolean>() {
            @Override
            public void onCompleted() {
                printLog("On complete");
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Error in RxJava concurrency");
                printLog(String.format("Boo! Error %s", e.getMessage()));
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onNext(Boolean aBoolean) {
                printLog(String.format("onNext with return value \"%b\"", aBoolean));
            }
        };
    }

    private void setupLogger(){
        logs = new ArrayList<>();
        adapter = new LogAdapter(getActivity(), new ArrayList<String>());
        logList.setAdapter(adapter);
    }

    private void doSomeLongOperation_thatBlocksCurrentThread(){
        printLog("performing long operation");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Timber.d("Operation was interrupted");
        }
    }

    private boolean isCurrentlyOnMainThread(){
        return Looper.myLooper() == Looper.getMainLooper();
    }

    private void printLog(String logMsg){
        if (isCurrentlyOnMainThread()){
            logs.add(0, logMsg + " (main thread)");
            adapter.clear();
            adapter.addAll(logs);
        }else {
            logs.add(0, logMsg + " (NOT main thread)");

            //this stuff belows can only be done on main thread
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    adapter.clear();
                    adapter.addAll(logs);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null){
            subscription.unsubscribe();
        }
    }

}
