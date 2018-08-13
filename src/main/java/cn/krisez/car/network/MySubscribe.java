package cn.krisez.car.network;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import cn.krisez.car._interface.IView;
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
        if (e instanceof TimeoutException || e instanceof SocketTimeoutException
                || e instanceof ConnectException){
            mIView.error("网络连接超时~");
        }else if(e instanceof UnknownHostException){
            mIView.error("网络有点问题~");
        }
    }

    @Override
    public abstract void onComplete();

}
