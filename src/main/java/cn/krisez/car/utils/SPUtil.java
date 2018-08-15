package cn.krisez.car.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.krisez.car.App;

public class SPUtil {

    public static void setPlayerType(int type){
        SharedPreferences.Editor editor = App.getContext().getSharedPreferences("player_type",Context.MODE_PRIVATE).edit();
        editor.putInt("type",type);
        editor.apply();
    }

    public static int getPlayerType(){
        SharedPreferences sharedPreferences = App.getContext().getSharedPreferences("player_type",Context.MODE_PRIVATE);
        return sharedPreferences.getInt("type",0);
    }

}
