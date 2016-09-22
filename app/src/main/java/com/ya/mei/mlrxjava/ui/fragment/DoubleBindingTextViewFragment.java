package com.ya.mei.mlrxjava.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ya.mei.mlrxjava.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

/**
 * Created by chenliang3 on 2016/3/11.
 */
public class DoubleBindingTextViewFragment extends Fragment {

    @Bind(R.id.double_binding_num1)
    EditText number1;
    @Bind(R.id.double_binding_num2)
    EditText number2;
    @Bind(R.id.double_binding_result)
    TextView result;

    Subscription subscription;
    PublishSubject<Float> resultEmitterSubject;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_double_binding_textview, container, false);
        ButterKnife.bind(this, view);

        resultEmitterSubject = PublishSubject.create();
        subscription = resultEmitterSubject.asObservable().subscribe(new Action1<Float>() {
            @Override
            public void call(Float aFloat) {
                result.setText(String.valueOf(aFloat));
            }
        });

        onNumberChanged();
        number2.requestFocus();

        return view;
    }

    @OnTextChanged({R.id.double_binding_num1, R.id.double_binding_num2})
    public void onNumberChanged(){
        float num1 = 0;
        float num2 = 0;

        if (!TextUtils.isEmpty(number1.getText().toString())){
            num1 = Float.parseFloat(number1.getText().toString());
        }
        if (!TextUtils.isEmpty(number2.getText().toString())){
            num2 = Float.parseFloat(number2.getText().toString());
        }

        resultEmitterSubject.onNext(num1 + num2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (subscription != null){
            subscription.unsubscribe();
        }
    }
}
