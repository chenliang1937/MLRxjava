package com.ya.mei.mlrxjava.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ya.mei.mlrxjava.R;
import com.ya.mei.mlrxjava.retrofit.Contributor;
import com.ya.mei.mlrxjava.retrofit.GithubApi;
import com.ya.mei.mlrxjava.ui.adapter.LogAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * Created by chenliang3 on 2016/3/14.
 */
public class PseudoCacheMergeFragment extends Fragment {

    @Bind(R.id.log_list)
    ListView resultList;

    private Subscription subscription = null;
    private HashMap<String, Long> contributionMap = null;
    private HashMap<Contributor, Long> resultAgeMap = new HashMap<>();
    private LogAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pseudo_cache_concat, container, false);
        ButterKnife.bind(this, view);
        initializeCache();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (subscription != null){
            subscription.unsubscribe();
        }
    }

    @OnClick(R.id.btn_start_pseudo_cache)
    public void onDemoPseudoCacheClicked(){
        adapter = new LogAdapter(getActivity(), new ArrayList<String>());
        resultList.setAdapter(adapter);
        initializeCache();

        Observable.merge(getCachedDate(), getFreshData())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<Contributor, Long>>() {
                    @Override
                    public void onCompleted() {
                        Timber.d("done loading ail date");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "arr something went wrong");
                    }

                    @Override
                    public void onNext(Pair<Contributor, Long> contributorLongPair) {
                        Contributor contributor = contributorLongPair.first;

                        if (resultAgeMap.containsKey(contributor) &&
                                resultAgeMap.get(contributor) > contributorLongPair.second){
                            return;
                        }

                        contributionMap.put(contributor.login, contributor.contribution);
                        resultAgeMap.put(contributor, contributorLongPair.second);

                        adapter.clear();
                        adapter.addAll(getListStringFromMap());
                    }
                });
    }

    private List<String> getListStringFromMap(){
        List<String> list = new ArrayList<>();
        for (String username : contributionMap.keySet()){
            String rowLog = String.format("%s [%d]", username, contributionMap.get(username));
            list.add(rowLog);
        }
        return list;
    }

    private Observable<Pair<Contributor, Long>> getCachedDate(){
        List<Pair<Contributor, Long>> list = new ArrayList<>();

        Pair<Contributor, Long> dataWithAgePair;

        for (String username : contributionMap.keySet()){
            Contributor c = new Contributor();
            c.login = username;
            c.contribution = contributionMap.get(username);

            dataWithAgePair = new Pair<>(c, System.currentTimeMillis());
            list.add(dataWithAgePair);
        }
        return Observable.from(list);
    }

    private Observable<Pair<Contributor, Long>> getFreshData(){
        return createGithubApi().contributors("square", "retrofit")
                .flatMap(new Func1<List<Contributor>, Observable<Contributor>>() {
                    @Override
                    public Observable<Contributor> call(List<Contributor> contributors) {
                        return Observable.from(contributors);
                    }
                })
                .map(new Func1<Contributor, Pair<Contributor, Long>>() {
                    @Override
                    public Pair<Contributor, Long> call(Contributor contributor) {
                        return new Pair<>(contributor, System.currentTimeMillis());
                    }
                });
    }

    private GithubApi createGithubApi(){
        RestAdapter.Builder builder = new RestAdapter.Builder().setEndpoint(
                "https://api.github.com/");

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

    private void initializeCache(){
        contributionMap = new HashMap<>();
        contributionMap.put("JakeWharton", 0l);
        contributionMap.put("pforhan", 0l);
        contributionMap.put("edenman", 0l);
        contributionMap.put("swankjesse", 0l);
        contributionMap.put("bruceLee", 0l);
    }

}
