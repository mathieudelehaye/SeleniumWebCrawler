//
//  JSONResultStructParser.java
//
//  Created by Mathieu Delehaye on 19/03/2023.
//
//  SeleniumWebCrawler: An app to automatically use search engines and store the results to a database.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
//  Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option)
//  any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
//  implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public
//  License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see
//  <https://www.gnu.org/licenses/>.ses/>.

package org.openqa.selenium.webcrawler.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.webcrawler.MyLogger;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class JSONResultStructParser extends JSONParser {
    private JSONObject mRoot;
    private JSONObject mCurrent;
    // Cache data
    private String mCurrentTag;
    private Map<String, JSONObject> mCurrentAttributes;
    private Map<String, String> mCurrentAttributeValues;
    private boolean mIsCurrentMultiple;
    private String mCurrentInfo;

    public JSONResultStructParser() {
        mCurrentTag = "";
        mCurrentAttributes = new HashMap<>();
        mCurrentAttributeValues = new HashMap<>();
        mIsCurrentMultiple = false;
        mCurrentInfo = "";
    }

    public JSONObject init(FileReader reader) throws ParseException {
        try {
            mRoot = (JSONObject) super.parse(reader);
            mCurrent = mRoot;
        } catch (Exception e) {
            throw new ParseException(0, "Error while parsing the file reader: " + e);
        }

        return mRoot;
    }

    public void startFrom(JSONObject node) {
        mCurrent = node;
    }

    public JSONObject goToChild(int index) {
        var childrenArray = (JSONArray) mCurrent.get("children");

        if (childrenArray == null) {
            MyLogger.log(Level.FINE, "Current JSON object has no `children` field, while trying to read it");
            return null;
        }

        final Iterator attribute = childrenArray.iterator();
        JSONObject innerObj = (JSONObject)attribute.next();

        for (int i = 0; i < index; i++) {
            if (attribute.hasNext()) {
                innerObj = (JSONObject)attribute.next();
            } else {
                return null;
            }
        }

        if (innerObj != null) {
            JSONObject parent = mCurrent;
            mCurrent = innerObj;

            // Reset the cache data for the child node
            mCurrentTag = "";
            mCurrentAttributes = new HashMap<>();
            mCurrentAttributeValues = new HashMap<>();
            mIsCurrentMultiple = false;
            mCurrentInfo = "";

            // Return the parent node, so it can be saved by the class user.
            return parent;
        }

        return null;
    }

    public String getCurrentTag() throws ParseException {
        if (!mCurrentTag.equals("")) {
            return mCurrentTag;
        }

        final var tag = (String) mCurrent.get("tag");

        if (tag == null) {
            throw new ParseException(0, "Current object has no `tag` field");
        }

        mCurrentTag = tag;
        return tag;
    }

    public Map<String, JSONObject> getCurrentAttributes() throws ParseException {
        var attributeArray = (JSONArray) mCurrent.get("attributes");

        if (attributeArray != null) {

            if (attributeArray.size() == mCurrentAttributes.size()) {
                return mCurrentAttributes;
            }

            final Iterator attribute = attributeArray.iterator();

            while (attribute.hasNext()) {
                var innerObj = (JSONObject) attribute.next();

                final var attributeKey = (String)innerObj.get("key");
                if (attributeKey == null) {
                    throw new ParseException(0, "Current `attributes` item has no `key` field");
                }

                mCurrentAttributes.put(attributeKey, innerObj);
            }
        }

        return mCurrentAttributes;
    }

    public JSONObject getCurrentAttribute(String key) throws ParseException {
        if (mCurrentAttributes.get(key) != null) {
            return (JSONObject) mCurrentAttributes.get(key);
        }

        var attributeArray = (JSONArray) mCurrent.get("attributes");

        if (attributeArray == null) {
            //MyLogger.log(Level.FINE, "Current JSON object has no `attributes` field, while trying to read it");
            return null;
        }

        final Iterator attribute = attributeArray.iterator();

        String attributeValue;
        while (attribute.hasNext()) {
            var innerObj = (JSONObject) attribute.next();
            final var attributeKey = (String)innerObj.get("key");

            if (attributeKey == null) {
                throw new ParseException(0, "Current `attributes` item has no `key` field");
            }

            if (attributeKey.equals(key)) {
                attributeValue = (String)innerObj.get("value");

                if (attributeValue == null) {
                    throw new ParseException(0, "Current `attributes` item has no `value` field, "
                        + "while trying to read it");
                }

                mCurrentAttributes.put(key, innerObj);
                return innerObj;
            }
        }

        //MyLogger.log(Level.FINE, "No attribute found with the given key for the current object");
        return null;
    }

    public String getCurrentAttributeValue(String key) throws ParseException {
        if (mCurrentAttributeValues.get(key) != null) {
            return mCurrentAttributeValues.get(key);
        }

        JSONObject attribute = getCurrentAttribute(key);

        if (attribute != null) {
            final String value = (String) attribute.get("value");
            mCurrentAttributeValues.put(key, value);
            return value;
        } else {
            return "";
        }
    }

    public boolean isCurrentMultiple() throws ParseException {
        if (!mIsCurrentMultiple) {
            return true;
        }

        var multiple = (Boolean) mCurrent.get("isMultiple");

        final boolean isMultiple = (multiple != null && multiple == true);
        mIsCurrentMultiple = isMultiple;

        return isMultiple;
    }

    public String getCurrentInfo() throws ParseException {
        if (!mCurrentInfo.equals("")) {
            return mCurrentInfo;
        }

        try {
            final String tag = getCurrentTag();
            final String id = getCurrentAttributeValue("id");
            final String elemClass = getCurrentAttributeValue("class");
            final boolean multiple = isCurrentMultiple();

            final String info = String.format("Result node with tag `%s`, %s, %s%s",
                tag,
                !id.equals("") ? ("id `" + id + "`") : "no id",
                !elemClass.equals("") ? ("class `" + elemClass + "`") : "no class",
                multiple ? " and multiple" : "");
            mCurrentInfo = info;

            return info;
        } catch (ParseException e) {
            throw new ParseException(0, "Error while trying to display node info: " + e);
        }
    }
}
