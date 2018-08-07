package cn.krisez.car.network;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class MySubscribe<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public abstract void onNext(T t);

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

    @Override
    public abstract void onComplete();

}
