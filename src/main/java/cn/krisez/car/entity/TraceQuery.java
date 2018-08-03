package cn.krisez.car.entity;

public class TraceQuery {
    private String start;
    private String end;
    private String time;
    private String distance;
    private String id;//通过id得到trace_json

    public TraceQuery(String start, String end, String time, String distance, String id) {
        this.start = start;
        this.end = end;
        this.time = time;
        this.distance = distance;
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
