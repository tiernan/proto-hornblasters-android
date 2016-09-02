package com.hornblasters.core;
/*
 * Derivative of work from the Android Framework.
 * Author: Tiernan Cridland
 *
 * Original Copyright & License:
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.text.Html;
import android.text.Spanned;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;


public class HornBlastersHtml {
    private HornBlastersHtml() {}

    public static Spanned fromHtml(String source) {
        return fromHtml(source, null);//null,
    }

    private static Spanned fromHtml(String source,
                                    Html.TagHandler tagHandler) {
        Parser parser = new Parser();
        try {
            parser.setProperty(Parser.schemaProperty, HtmlParser.schema);
        } catch (org.xml.sax.SAXNotRecognizedException | org.xml.sax.SAXNotSupportedException e) {
            throw new RuntimeException(e);
        }

        HtmlToSpannedConverter converter =
                new HtmlToSpannedConverter(source, tagHandler,
                        parser);
        return converter.convert();
    }

    private static class HtmlParser {
        private static final HTMLSchema schema = new HTMLSchema();
    }
}
