package com.hornblasters.soundboard2;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class CheckoutShippingParser {
    private static final String TAG = "CheckoutShippingParser";
    private static final String ns = null;
    private static final String G_SHIPPING          = "shipping";
    private static final String C_METHOD            = "method";
    private static final String F_TYPE              = "type";
    private static final String F_RATE        = "rate";
    private XmlPullParser parser;

    public ArrayList<ShippingMethod> parse(String xml) throws XmlPullParserException, IOException {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Starting parser");
        }
        StringReader in = new StringReader(xml);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        factory.setFeature(XmlPullParser.FEATURE_VALIDATION, false);

        parser = factory.newPullParser();
        parser.setInput(in);
        return readFeed();
    }

    private ArrayList<ShippingMethod> readFeed() throws XmlPullParserException, IOException {
        parser.nextTag();
        String tagName = parser.getName();
        switch (tagName) {
            case G_SHIPPING:
                return readShipping();
            case "error":
            default:
                return null;
        }
    }

    private ArrayList<ShippingMethod> readShipping() throws XmlPullParserException, IOException {
        ArrayList<ShippingMethod> methods = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, G_SHIPPING);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            if (parser.getName().equals(C_METHOD)) {
                methods.add(readMethod());
            } else {
                skip();
            }

        }
        parser.require(XmlPullParser.END_TAG, ns, G_SHIPPING);
        return methods;
    }

    private ShippingMethod readMethod() throws XmlPullParserException, IOException {
        String type = null;
        float rate = 0;

        parser.require(XmlPullParser.START_TAG, ns, C_METHOD);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();

            switch (tagName) {
                case F_TYPE:
                    type = readText(tagName);
                    break;
                case F_RATE:
                    rate = Float.parseFloat(readText(tagName));
                    break;
                default:
                    skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, C_METHOD);
        return new ShippingMethod(type, rate);
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
