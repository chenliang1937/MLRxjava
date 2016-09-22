package com.ya.mei.mlrxjava.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.ya.mei.mlrxjava.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.functions.Func3;
import timber.log.Timber;

/**
 * Created by chenliang3 on 2016/3/14.
 *
 * 表单验证
 */
public class FormValidationCombineLatestFragment extends Fragment {

    @Bind(R.id.btn_form_valid)
    TextView btnValidIndicator;
    @Bind(R.id.combl_email)
    EditText email;
    @Bind(R.id.combl_password)
    EditText password;
    @Bind(R.id.combl_num)
    EditText number;

    private Observable<CharSequence> emailChangeObservable;
    private Observable<CharSequence> passwordChangeObservable;
    private Observable<CharSequence> numberChangeObservable;

    private Subscription subscription = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_form_validation_comb_latest, container, false);
        ButterKnife.bind(this, view);

        emailChangeObservable = RxTextView.textChanges(email).skip(1);
        passwordChangeObservable = RxTextView.textChanges(password).skip(1);
        numberChangeObservable = RxTextView.textChanges(number).skip(1);

        combineLatestEvents();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (subscription != null){
            subscription.unsubscribe();
        }
    }

    private void combineLatestEvents(){
        subscription = Observable.combineLatest(emailChangeObservable,
                passwordChangeObservable,
                numberChangeObservable,
                new Func3<CharSequence, CharSequence, CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence newEmail, CharSequence newPassword, CharSequence newNumber) {
                        boolean emailValid = !TextUtils.isEmpty(newEmail) &&
                                Patterns.EMAIL_ADDRESS.matcher(newEmail).matches();
                        if (!emailValid){
                            email.setError("Invalid Email!");
                        }

                        boolean passValid = !TextUtils.isEmpty(newPassword) && newPassword.length() > 8;
                        if (!passValid){
                            password.setError("Invalid Password!");
                        }

                        boolean numValid = !TextUtils.isEmpty(newNumber);
                        if (numValid){
                            int num = Integer.parseInt(newNumber.toString());
                            numValid = num > 0 && num <= 100;
                        }else {
                            number.setError("Invalid Number!");
                        }

                        return emailValid && passValid && numValid;
                    }
                })
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "there was an error");
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean){
                            btnValidIndicator.setBackgroundColor(Color.BLUE);
                        }else {
                            btnValidIndicator.setBackgroundColor(Color.GRAY);
                        }
                    }
                });
    }

}
