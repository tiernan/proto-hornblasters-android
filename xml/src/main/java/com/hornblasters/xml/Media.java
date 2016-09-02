package com.hornblasters.xml;

import java.util.ArrayList;

public class Media implements Data {
    public final ArrayList<Video> videos;

    public Media(ArrayList<Video> videos) {
        this.videos = videos;
    }
}