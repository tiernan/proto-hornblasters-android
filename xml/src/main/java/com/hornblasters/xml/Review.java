package com.hornblasters.xml;

class Review implements Data {
    private final int id;
    private final String title;
    private final String content;
    private final int userId;
    private final String userName;

    public Review(int id, String title, String content, int userId, String userName) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.userName = userName;
    }
}
