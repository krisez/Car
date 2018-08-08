package cn.krisez.car.network;

import cn.krisez.car.ui.trace.IView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class MySubscribe<T> implements Observer<T> {
    private IView mIView;

    protected MySubscribe(IView IView) {
        mIView = IView;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public abstract void onNext(T t);

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        mIView.error(e.getMessage());
    }

    @Override
    public abstract void onComplete();

}
