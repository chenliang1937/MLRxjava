package com.ya.mei.mlrxjava.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.ui.adapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * Created by chenliang3 on 2016/3/11.
 *
 * 搜索仿抖动
 */
public class DebounceSearchEmitterFragment extends Fragment {

    @Bind(R.id.list_threading_log)
    ListView logsList;
    @Bind(R.id.input_txt_debounce)
    EditText inputSearchText;

    private LogAdapter adapter;
    private List<String> logs;
    private Subscription subscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debounce, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupLogger();

        subscription = RxTextView.textChangeEvents(inputSearchText)
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSeatchObserver());
    }

    @OnClick(R.id.clr_debounce)
    public void onClearLog(){
        logs = new ArrayList<>();
        adapter.clear();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscription.unsubscribe();
    }

    private Observer<TextViewTextChangeEvent> getSeatchObserver(){
        return new Observer<TextViewTextChangeEvent>() {
            @Override
            public void onCompleted() {
                Timber.d("-------------onComplete");
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "------------oops on error!");
                printLog("Dang error. check your logs");
            }

            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                printLog(String.format("search for %s", textViewTextChangeEvent.text().toString()));
            }
        };
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
