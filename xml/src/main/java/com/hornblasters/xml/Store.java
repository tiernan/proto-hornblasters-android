package com.hornblasters.xml;

import java.util.ArrayList;

public class Store implements Data {
    public final ArrayList<Category> categories;

    public Store(ArrayList<Category> categories) {
        this.categories = categories;
    }
}