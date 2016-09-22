package com.ya.mei.mlrxjava.Utils;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by chenliang3 on 2016/3/11.
 */
public class RxUtils {

    public static void unsubscribeIfNotNull(Subscription subscription){
        if (subscription != null){
            subscription.unsubscribe();
        }
    }

    public static CompositeSubscription getNewCompositeSubIfUnsubscribed(CompositeSubscription compositeSubscription){
        if (compositeSubscription == null || compositeSubscription.isUnsubscribed()){
            return new CompositeSubscription();
        }
        return compositeSubscription;
    }

}
