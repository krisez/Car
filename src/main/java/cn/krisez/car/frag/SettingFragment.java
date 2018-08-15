package cn.krisez.car.frag;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.shuyu.gsyvideoplayer.GSYVideoManager;

import cn.krisez.car.App;
import cn.krisez.car.R;
import cn.krisez.car.utils.GlideCacheUtil;
import cn.krisez.car.utils.SPUtil;

public class SettingFragment extends Fragment {

    private RadioGroup mRadioGroup;
    private TextView mTvClear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_setting,container,false);
        mRadioGroup = view.findViewById(R.id.set_choose_player);
        mTvClear = view.findViewById(R.id.set_clear);
        RadioButton media = view.findViewById(R.id.android_player);
        RadioButton ijk = view.findViewById(R.id.ijk_player);
        switch (SPUtil.getPlayerType()){
            case 0:
                ijk.setChecked(true);
                break;
            case 4:
                media.setChecked(true);
                break;
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.android_player:
                    SPUtil.setPlayerType(4);
                    GSYVideoManager.instance().setVideoType(App.getContext(),4);
                    break;
                case R.id.ijk_player:
                    SPUtil.setPlayerType(0);
                    GSYVideoManager.instance().setVideoType(App.getContext(),0);
                    break;
            }
        });

        mTvClear.setOnClickListener(v->{
            GSYVideoManager.instance().clearAllDefaultCache(getContext());
            Toast.makeText(getContext(), GlideCacheUtil.getInstance().getCacheSize(getContext()), Toast.LENGTH_SHORT).show();
            GlideCacheUtil.getInstance().clearImageAllCache(getContext());
        });
    }
}
