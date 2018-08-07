package cn.krisez.car.ui.trace;


import java.util.List;

import cn.krisez.car.entity.TraceQuery;

public interface ITraceView extends IView{
    void update(List<TraceQuery> list);

}
