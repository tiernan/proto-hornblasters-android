package com.hornblasters.soundboard2;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

public class CheckoutPurchaseParser {
    private static final String TAG = "CheckoutPurchaseParser";
    private static final String ns = null;
    private static final String C_RESPONSE          = "response";
    private static final String F_APPROVED        = "approved";
    private XmlPullParser parser;

    public boolean parse(String xml) throws XmlPullParserException, IOException {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Starting parser");
        }

        Log.d(TAG, xml);

        StringReader in = new StringReader(xml);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        factory.setFeature(XmlPullParser.FEATURE_VALIDATION, false);

        parser = factory.newPullParser();
        parser.setInput(in);
        return readFeed();
    }

    private boolean readFeed() throws XmlPullParserException, IOException {
        parser.nextTag();
        String tagName = parser.getName();
        switch (tagName) {
            case C_RESPONSE:
                return readApproved();
            case "error":
            default:
                return false;
        }
    }

    private boolean readApproved() throws XmlPullParserException, IOException {
        boolean approved = false;

        parser.require(XmlPullParser.START_TAG, ns, C_RESPONSE);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();

            switch (tagName) {
                case F_APPROVED:
                    approved = Boolean.parseBoolean(readText(tagName));
                    break;
                default:
                    skip();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, C_RESPONSE);
        return approved;
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
