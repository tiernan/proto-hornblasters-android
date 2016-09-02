package com.hornblasters.xml;

import java.util.ArrayList;

public class Category implements Data {
    public final int id;
    public final String title;
    public final ArrayList<Product> products;

    public Category(int id, String title, ArrayList<Product> products) {
        this.id = id;
        this.title = title;
        this.products = products;
    }
}