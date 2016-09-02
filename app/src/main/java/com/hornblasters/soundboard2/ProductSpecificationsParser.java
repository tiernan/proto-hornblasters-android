package com.hornblasters.soundboard2;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

class ProductSpecificationsParser {
    private static final String TAG = "SpecificationsParser";
    private static final String F_TERM                = "dt";
    private static final String F_DEFINITION          = "dd";

    public ArrayList<Specification> parse(String xml) throws XmlPullParserException, IOException {
        if (BuildConfig.DEBUG) {
            Log.v(TAG, "Starting parser");
        }
        StringReader in = new StringReader(xml);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);

            factory.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            factory.setFeature(XmlPullParser.FEATURE_VALIDATION, false);

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(in);
            return readFeed(parser);
    }

    private ArrayList<Specification> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Specification> specifications = new ArrayList<>();
        String term = null;
        String definition = null;

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String tagName = parser.getName();
            switch (tagName) {
                case F_TERM:
                    // Commit last group:
                    if (term != null) {
                        if (definition != null) {
                            specifications.add(new Specification(term, definition));
                            term = null;
                            definition = null;
                        } else {
                            specifications.add(new Specification(term, null));
                        }
                    }
                    if (parser.next() == XmlPullParser.TEXT) {
                        term = parser.getText();
                        parser.nextTag();
                    }
                    break;
                case F_DEFINITION:
                    if (term == null) {
                        continue; // No term to add definition too; skip until hit
                    }
                    if (parser.next() == XmlPullParser.TEXT) {
                        if (definition != null) {
                            definition += "\n";
                        } else {
                            definition = "";
                        }
                        definition += parser.getText();
                    }
                    break;
            }
        }
        if (term != null && definition != null) {
            specifications.add(new Specification(term, definition));
        }
        return specifications;
    }

    public static class Specification {
        public final String term;
        public final String definition;

        private Specification(String term, String definition) {
            this.term = term;
            this.definition = definition;
        }
    }
}