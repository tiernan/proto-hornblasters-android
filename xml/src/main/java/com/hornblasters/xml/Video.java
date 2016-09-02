package com.hornblasters.xml;

public class Video implements Data {
    public final int id;
    public final String title;
    public final String uri;
    public final VideoType type;

    public Video(int id, String title, String uri, VideoType type) {
        this.id = id;
        this.title = title;
        this.uri = uri;
        this.type = type;
    }
    public enum VideoType {YOUTUBE, UNSUPPORTED}
}