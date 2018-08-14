package cn.krisez.car.ui.main;

import java.util.List;

import cn.krisez.car._interface.IView;
import cn.krisez.car.entity.VideoQuery;

public interface IMainView extends IView {
    void traceOver();
    void speed(String v);
    void requestVideo(List<VideoQuery> list);
}
