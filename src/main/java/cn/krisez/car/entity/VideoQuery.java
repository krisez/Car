package cn.krisez.car.entity;

public class VideoQuery {
    private String thumb;
    private String time;// xxxx-xx-xx xx:xx:xx
    private String url;//播放地址
    private String trace_id;

    public VideoQuery(String thumb, String time, String url, String trace_id) {
        this.thumb = thumb;
        this.time = time;
        this.url = url;
        this.trace_id = trace_id;
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
