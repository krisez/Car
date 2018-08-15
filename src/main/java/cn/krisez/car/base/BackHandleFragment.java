package cn.krisez.car.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import cn.krisez.car._interface.BackHandleInterface;

public abstract class BackHandleFragment extends Fragment {

    private BackHandleInterface backHandleInterface;
    public abstract boolean onBackPressed();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() instanceof BackHandleInterface){
            this.backHandleInterface = (BackHandleInterface)getActivity();
        }else{
            throw new ClassCastException("Hosting Activity must implement BackHandledInterface");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        backHandleInterface.onSelectedFragment(this);
    }
}