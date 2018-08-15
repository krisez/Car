package cn.krisez.car.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Krisez on 2017/8/8.
 */

public class FManager {
    public static void fmReplace(FragmentManager fm, Fragment fragment, int id){
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(id,fragment);
        transaction.commit();
    }

    public static void detach(FragmentManager fm, Fragment fragment){
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.detach(fragment);
        transaction.commit();
    }
    public static void addFg(FragmentManager fm, Fragment fragment, int id, String tag) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(id, fragment,tag);
        transaction.commit();
    }

    public static void addFg(FragmentManager fm, Fragment fragment, int id) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(id, fragment);
        transaction.commit();
    }

    public static void hideFg(FragmentManager fm, Fragment fragment){
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
    }

    public static void showFg(FragmentManager supportFragmentManager, Fragment fragment) {
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }
}
