package cn.krisez.car.ui.video;

import java.util.List;

import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car._interface.IView;

public interface IVideoView extends IView {
    void update(List<VideoQuery> list);
    void skip(List<VideoQuery> next);
}
