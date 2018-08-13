package cn.krisez.car.ui.trace;


import java.util.List;

import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car._interface.IView;

public interface ITraceView extends IView {
    void update(List<TraceQuery> list);

}
