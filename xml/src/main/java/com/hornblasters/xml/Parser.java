package com.hornblasters.xml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;

public class Parser {
    private static final String ns = null;
    // Classes
    private static final String C_PRODUCT           = "product";
    private static final String C_CATEGORY          = "category";
    private static final String C_STORE             = "store";
    private static final String C_VIDEO             = "video";
    private static final String C_MEDIA             = "media";
    // Groups
    private static final String G_PRODUCTS          = "products";
    private static final String G_CATEGORIES        = "categories";
    private static final String G_IMAGES            = "images";
    private static final String G_AUDIOS            = "audios";
    private static final String G_VIDEOS            = "videos";
    // Fields; maybe change these to an array
    private static final String F_ID                = "id";
    private static final String F_STOCK             = "stock";
    private static final String F_AVAILABILITY      = "available";
    private static final String F_BRAND             = "brand";
    private static final String F_TITLE             = "title";
    private static final String F_PART_NUMBER       = "partnumber";
    private static final String F_PRICE             = "price";
    private static final String F_FREE_SHIPPING     = "free-shipping";
    private static final String F_SPECIFICATIONS    = "specifications";
    private static final String F_SUMMARY           = "summary";

    private XmlPullParser parser;


    public Data parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    public Data parse(String string) throws XmlPullParserException, IOException {
        StringReader in = new StringReader(string);
        parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(in);
        return readFeed(parser);
    }

    private Data readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.nextTag();
        String tagName = parser.getName();
        switch (tagName) {
            case C_STORE:
                return readStore();
            case C_CATEGORY:
                return readCategory();
            case C_PRODUCT:
                return readProduct();
            case C_MEDIA:
                return readMedia();
            case C_VIDEO:
                return readVideo();
            default:
                return null;
        }
    }

    private Store readStore() throws XmlPullParserException, IOException {
        ArrayList<Category> categories = null;

        parser.require(XmlPullParser.START_TAG, ns, C_STORE);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals(G_CATEGORIES)) {
                categories = readCategories();
            } else {
                skip();
            }

        }
        parser.require(XmlPullParser.END_TAG, ns, C_STORE);
        return new Store(categories);
    }

    private Media readMedia() throws XmlPullParserException, IOException {
        ArrayList<Video> videos = null;

        parser.require(XmlPullParser.START_TAG, ns, C_MEDIA);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals(G_VIDEOS)) {
                videos = readVideos();
            } else {
                skip();
            }

        }
        parser.require(XmlPullParser.END_TAG, ns, C_MEDIA);
        return new Media(videos);
    }

    private ArrayList<Category> readCategories() throws XmlPullParserException, IOException {
        ArrayList<Category> categories = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, G_CATEGORIES);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals(C_CATEGORY)) {
                categories.add(readCategory());
            } else {
                skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, G_CATEGORIES);
        return categories;
    }

    private Category readCategory() throws XmlPullParserException, IOException {
        int id = 0;
        String title = null;
        ArrayList<Product> products = null;

        parser.require(XmlPullParser.START_TAG, ns, C_CATEGORY);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();

            switch (tagName) {
                case F_ID:
                    id = Integer.parseInt(readText(F_ID));
                    break;
                case F_TITLE:
                    title = readText(F_TITLE);
                    break;
                case G_PRODUCTS:
                    products = readProducts();
                    break;
                default:
                    skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, C_CATEGORY);
        return new Category(id, title, products);
    }

    private ArrayList<Product> readProducts() throws XmlPullParserException, IOException {
        ArrayList<Product> products = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, G_PRODUCTS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals(C_PRODUCT)) {
                products.add(readProduct());
            } else {
                skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, G_PRODUCTS);
        return products;
    }

    @SuppressWarnings("ConstantConditions")
    private Product readProduct() throws XmlPullParserException, IOException  {
        int id = 0;
        boolean stock = false;
        boolean availability = false;
        String brand = null;
        String title = null;
        String number = null;
        ArrayList<String> images = null;
        ArrayList<Review> reviews = null;
        ArrayList<String> audios = null;
        ArrayList<Video> videos = null;
        double price = 0.0;
        boolean freeShipping = false;
        String summary = null;
        String specifications = null;

        parser.require(XmlPullParser.START_TAG, ns, C_PRODUCT);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();

            switch (tagName) {
                case F_ID:
                    id = Integer.parseInt(readText(tagName));
                    break;
                case F_STOCK:
                    stock = Boolean.parseBoolean(readText(tagName));
                    break;
                case F_AVAILABILITY:
                    availability = Boolean.parseBoolean(readText(tagName));
                    break;
                case F_BRAND:
                    brand = readText(tagName);
                    break;
                case F_TITLE:
                    title = readText(tagName);
                    break;
                case F_PART_NUMBER:
                    number = readText(tagName);
                    break;
                case G_IMAGES:
                    images = readImages();
                    break;
                case G_AUDIOS:
                    audios = readAudio();
                    break;
                case G_VIDEOS:
                    videos = readVideos();
                    break;
                case F_PRICE:
                    price = Double.parseDouble(readText(tagName));
                    break;
                case F_FREE_SHIPPING:
                    freeShipping = Boolean.parseBoolean(readText(tagName));
                    break;
                case F_SPECIFICATIONS:
                    specifications = readText(tagName);
                    break;
                case F_SUMMARY:
                    summary = readText(tagName);
                    break;
                default:
                    skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, C_PRODUCT);
        return new Product(id, stock, availability, brand, title, number, images, reviews, audios, videos, price, freeShipping, summary, specifications, 1);
    }



    private Video readVideo() throws XmlPullParserException, IOException {
        int id = 0;
        String title = null;
        String uri = null;
        Video.VideoType type = null;

        parser.require(XmlPullParser.START_TAG, ns, "video");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch(tagName) {
                case "id":
                    id = Integer.parseInt(readText(tagName));
                    break;
                case "title":
                    title = readText(tagName);
                    break;
                case "locator":
                    uri = readText(tagName);
                    break;
                case "type":
                    String typeString = readText(tagName);
                    switch(typeString) {
                        case "youtube":
                            type = Video.VideoType.YOUTUBE;
                            break;
                        default:
                            type = Video.VideoType.UNSUPPORTED;
                    }
                    break;
                default:
                    skip();
                    break;
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "video");
        return new Video(id, title, uri, type);
    }

    private ArrayList<String> readImages() throws XmlPullParserException, IOException {
        ArrayList<String> images = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, G_IMAGES);

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch(tagName) {
                case "image":
                    images.add(readText(tagName));
                    break;
                default:
                    skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, G_IMAGES);
        return images;
    }

    private ArrayList<String> readAudio() throws XmlPullParserException, IOException {
        ArrayList<String> audios = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, G_AUDIOS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch(tagName) {
                case "audio":
                    audios.add(readText(tagName));
                    break;
                default:
                    skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, G_AUDIOS);
        return audios;
    }

    private ArrayList<Video> readVideos() throws XmlPullParserException, IOException {
        ArrayList<Video> videos = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG, ns, G_VIDEOS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch(tagName) {
                case C_VIDEO:
                    videos.add(readVideo());
                    break;
                default:
                    skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, G_VIDEOS);
        return videos;
    }

    private String readText(String tagName) throws IOException, XmlPullParserException {
        String result = "";
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return result;
    }

    private void skip() throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
            }
        }
    }
}