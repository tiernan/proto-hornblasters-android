package com.hornblasters.xml;

import java.util.List;

public class Product implements Data {
    public final int id;
    public final boolean stock;
    public final boolean availability;
    public final String brand;
    public final String title;
    public final String number;
    public final List<String> images;
    public final List<Review> reviews;
    public final List<String> audios;
    public final List<Video> videos;
    public final double price;
    public final boolean freeShipping;
    public final String summary;
    public final String specifications;
    public final int quantity;
    public enum AvailabilityType {STOCK, SPECIAL, NONE}

    public Product(int id, boolean stock, boolean availability, String brand, String title,
                   String number, List<String> images, List<Review> reviews,
                   List<String> audios, List<Video> videos, double price, boolean freeShipping,
                   String summary, String specifications, int quantity) {
        this.id = id;
        this.stock = stock;
        this.availability = availability;
        this.brand = brand;
        this.title = title;
        this.number = number;
        this.images = images;
        this.reviews = reviews;
        this.audios = audios;
        this.videos = videos;
        this.price = price;
        this.freeShipping = freeShipping;
        this.summary = summary;
        this.specifications = specifications;
        this.quantity = quantity;
    }

    public AvailabilityType getAvailability() {
        if (stock) {
            return AvailabilityType.STOCK;
        } else if (availability) {
            return AvailabilityType.SPECIAL;
        } else {
            return AvailabilityType.NONE;
        }
    }
}
