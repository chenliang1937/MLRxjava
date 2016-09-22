package com.ya.mei.mlrxjava.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.Utils.RxUtils;
import com.ya.mei.mlrxjava.retrofit.Contributor;
import com.ya.mei.mlrxjava.retrofit.GithubApi;
import com.ya.mei.mlrxjava.retrofit.User;
import com.ya.mei.mlrxjava.ui.adapter.LogAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

import static android.text.TextUtils.isEmpty;
import static java.lang.String.format;

/**
 * Created by chenliang3 on 2016/3/11.
 */
public class RetrofitFragment extends Fragment {

    @Bind(R.id.retrofit_contributors_username)
    EditText username;
    @Bind(R.id.retrofit_contributors_repository)
    EditText repo;
    @Bind(R.id.log_list)
    ListView resultList;

    private GithubApi api;
    private LogAdapter adapter;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = createGithubApi();
    }

    @Override
    public void onResume() {
        super.onResume();
        subscription = RxUtils.getNewCompositeSubIfUnsubscribed(subscription);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retrofit, container, false);
        ButterKnife.bind(this, view);

        adapter = new LogAdapter(getActivity(), new ArrayList<String>());
        resultList.setAdapter(adapter);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        RxUtils.unsubscribeIfNotNull(subscription);
    }

    @OnClick(R.id.btn_retrofit_contributors)
    public void onListContributorsClicked(){
        adapter.clear();

        subscription.add(
                api.contributors(username.getText().toString(), repo.getText().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("Retrofit call 1 completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "oops we got an error while getting the list of contributors");
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        for (Contributor c : contributors) {
                            adapter.add(String.format("%s has made %d contributions to %s",
                                    c.login,
                                    c.contribution,
                                    repo.getText().toString()));
                            Timber.d("%s has made %d contributions to %s",
                                    c.login,
                                    c.contribution,
                                    repo.getText().toString());
                        }
                    }
                })
        );
    }

    @OnClick(R.id.btn_retrofit_contributors_with_user_info)
    public void onListContributorsWithFullUserInfoClicked(){
        adapter.clear();

        subscription.add(api.contributors(username.getText().toString(),
                repo.getText().toString())
                .flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
                    @Override
                    public Observable<Contributor> call(List<Contributor> contributors) {
                        return Observable.from(contributors);
                    }
                })
                .flatMap(new Func1<Contributor, Observable<Pair<User, Contributor>>>() {
                    @Override
                    public Observable<Pair<User, Contributor>> call(Contributor contributor) {
                        Observable<User> _userObservable = api.user(contributor.login)
                                .filter(new Func1<User, Boolean>() {
                                    @Override
                                    public Boolean call(User user) {
                                        return !isEmpty(user.name) && !isEmpty(user.email);
                                    }
                                });

                        return Observable.zip(_userObservable,
                                Observable.just(contributor),
                                new Func2<User, Contributor, Pair<User, Contributor>>() {
                                    @Override
                                    public Pair<User, Contributor> call(User user,
                                                                        Contributor contributor) {
                                        return new Pair<>(user, contributor);
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Pair<User, Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("Retrofit call 2 completed ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e,
                                "error while getting the list of contributors along with full names");
                    }

                    @Override
                    public void onNext(Pair<User, Contributor> pair) {
                        User user = pair.first;
                        Contributor contributor = pair.second;

                        adapter.add(format("%s(%s) has made %d contributions to %s",
                                user.name,
                                user.email,
                                contributor.contribution,
                                repo.getText().toString()));

                        adapter.notifyDataSetChanged();

                        Timber.d("%s(%s) has made %d contributions to %s",
                                user.name,
                                user.email,
                                contributor.contribution,
                                repo.getText().toString());
                    }
                }));
    }

    private GithubApi createGithubApi(){
        RestAdapter.Builder builder = new RestAdapter.Builder().
                setEndpoint("https://api.github.com/");
//                .setLogLevel(RestAdapter.LogLevel.FULL)

        final String githubToken = getResources().getString(R.string.github_oauth_token);
        if (!TextUtils.isEmpty(githubToken)){
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", String.format("token %s", githubToken));
                }
            });
        }
        return builder.build().create(GithubApi.class);
    }

}
