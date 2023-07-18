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

package selenium.webcrawler.model.JSON;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebElement;
import selenium.webcrawler.templates.MyLogger;

public class JSONResultStructParser extends JSONParser {
    public class State implements Cloneable {
        public JSONObject mRoot;
        public JSONObject mCurrent;
        public boolean mCurrentMatched = false;

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        public void update(State state) {
            mRoot = (JSONObject) state.mRoot.clone();
            mCurrent = (JSONObject) state.mCurrent.clone();
            mCurrentMatched = state.mCurrentMatched;
        }

        // TODO: implement getters-setters and make the properties private
    }

    private class Cache {
        public String mCurrentTag;
        public Map<String, JSONObject> mCurrentAttributes;
        public Map<String, String> mCurrentAttributeValues;
        public String mCurrentValue;
        public boolean mIsCurrentMultiple;
        public String mCurrentInfo;

        Cache() {
            reset();
        }

        // TODO: implement getters-setters and make the properties private

        public void reset() {
            mCurrentTag = "";
            mCurrentAttributes = new HashMap<>();
            mCurrentAttributeValues = new HashMap<>();
            mCurrentValue = "";
            mIsCurrentMultiple = false;
            mCurrentInfo = "";
        }
    }

    private State mState = new State();
    private Cache mCache = new Cache();

    public JSONObject init(FileReader reader) throws ParseException {
        try {
            mState.mRoot = (JSONObject) super.parse(reader);
            mState.mCurrent = mState.mRoot;
        } catch (Exception e) {
            throw new ParseException(0, "Error while parsing the file reader: " + e);
        }

        return mState.mRoot;
    }

    public State saveState() {
        try {
            return (State) mState.clone();
        } catch (CloneNotSupportedException cnse) {
            MyLogger.log(Level.SEVERE, "Error while saving the JSON parser state: " + cnse.getMessage());
            return null;
        }
    }

    public void restoreState(State state) {
        mState.update(state);

        // We need to clear the cache, otherwise we use the cached data before the restore
        mCache.reset();
    }

    public int childrenNumber() {
        var childrenArray = (JSONArray) mState.mCurrent.get("children");
        if (childrenArray == null) {
            MyLogger.log(Level.FINE, "Current JSON object has no `children` field, while trying to " +
                "get their number");

            return 0;
        }

        return childrenArray.size();
    }

    public void goToChild(int index) {
        if (mState.mCurrent == null) {
            MyLogger.log(Level.WARNING, "Cannot go to the child of a null pointer");
            return;
        }

        var childrenArray = (JSONArray) mState.mCurrent.get("children");

        if (childrenArray == null) {
            MyLogger.log(Level.FINE, "Current JSON object has no `children` field, while trying to read it");
            return;
        }

        final Iterator attribute = childrenArray.iterator();
        var innerObj = (JSONObject)attribute.next();

        for (int i = 0; i < index; i++) {
            if (attribute.hasNext()) {
                innerObj = (JSONObject)attribute.next();
            } else {
                return;
            }
        }

        if (innerObj != null) {
            mState.mCurrent = innerObj;

            mState.mCurrentMatched = false;
            mCache.reset();
        }
    }

    public Boolean isCurrentlyMatching(WebElement element) {
        // Check if the provided DOM element is matching the current JSON node

        boolean res = true;

        final String dOMNodeTag = element.getTagName();
        final String dOMNodeClass = element.getAttribute("class");
        final String dOMNodeId = element.getAttribute("id");

        try {
            final String jSONNodeTag = getCurrentTag();
            final String jSONNodeClass = getCurrentAttributeValue("class");
            final String jSONNodeId = getCurrentAttributeValue("id");

            if (dOMNodeTag != null && jSONNodeTag != null && !dOMNodeTag.equals(jSONNodeTag)) {
                res = false;
            }

            if (res && dOMNodeClass != null && jSONNodeClass != null && !dOMNodeClass.equals(jSONNodeClass)) {
                res = false;
            }

            if (res && dOMNodeId != null && jSONNodeId != null && !dOMNodeId.equals(jSONNodeId)) {
                res = false;
            }
        } catch (ParseException pe) {
            MyLogger.log(Level.SEVERE, "Error while trying to match the JSON and DOM nodes: "
                + pe.getMessage());
        }

        // Only update `mCurrentMatched` if matching
        if (res) {
            mState.mCurrentMatched = true;
        }

        return res;
    }

    public boolean wasCurrentMatched() {
        return mState.mCurrentMatched;
    }

    public String getCurrentTag() throws ParseException {
        if (!mCache.mCurrentTag.equals("")) {
            return mCache.mCurrentTag;
        }

        final var tag = (String) mState.mCurrent.get("tag");

        if (tag == null) {
            throw new ParseException(0, "Current object has no `tag` field");
        }

        mCache.mCurrentTag = tag;
        return tag;
    }

    public Map<String, JSONObject> getCurrentAttributes() throws ParseException {
        var attributeArray = (JSONArray) mState.mCurrent.get("attributes");

        if (attributeArray != null) {

            if (attributeArray.size() == mCache.mCurrentAttributes.size()) {
                return mCache.mCurrentAttributes;
            }

            final Iterator attribute = attributeArray.iterator();

            while (attribute.hasNext()) {
                var innerObj = (JSONObject) attribute.next();

                final var attributeKey = (String)innerObj.get("key");
                if (attributeKey == null) {
                    throw new ParseException(0, "Current `attributes` item has no `key` field");
                }

                mCache.mCurrentAttributes.put(attributeKey, innerObj);
            }
        }

        return mCache.mCurrentAttributes;
    }

    public JSONObject getCurrentAttribute(String key) throws ParseException {
        if (mCache.mCurrentAttributes.get(key) != null) {
            return (JSONObject) mCache.mCurrentAttributes.get(key);
        }

        var attributeArray = (JSONArray) mState.mCurrent.get("attributes");

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

                mCache.mCurrentAttributes.put(key, innerObj);
                return innerObj;
            }
        }

        //MyLogger.log(Level.FINE, "No attribute found with the given key for the current object");
        return null;
    }

    public String getCurrentAttributeValue(String key) throws ParseException {
        if (mCache.mCurrentAttributeValues.get(key) != null) {
            return mCache.mCurrentAttributeValues.get(key);
        }

        JSONObject attribute = getCurrentAttribute(key);

        if (attribute != null) {
            final String value = (String) attribute.get("value");
            mCache.mCurrentAttributeValues.put(key, value);
            return value;
        } else {
            return "";
        }
    }

    public String getCurrentValue() {
        final Object value = mState.mCurrent.get("value");

        // TODO: cover the case where value is a nested struct rather than a String
        mCache.mCurrentValue = ((value instanceof String))?
            (String)value : "";

        return mCache.mCurrentValue;
    }

    public boolean isCurrentMultiple() {
        final var multiple = (Boolean) mState.mCurrent.get("isMultiple");
        mCache.mIsCurrentMultiple = (multiple != null && multiple);
        return mCache.mIsCurrentMultiple;
    }

    public String getCurrentInfo() throws ParseException {
        if (!mCache.mCurrentInfo.equals("")) {
            return mCache.mCurrentInfo;
        }

        try {
            final String tag = getCurrentTag();
            final String id = getCurrentAttributeValue("id");
            final String elemClass = getCurrentAttributeValue("class");
            final boolean multiple = isCurrentMultiple();

            final String info = String.format("JSON node with tag `%s`, %s, %s%s",
                tag,
                !id.equals("") ? ("id `" + id + "`") : "no id",
                !elemClass.equals("") ? ("class `" + elemClass + "`") : "no class",
                multiple ? " and multiple" : "");
            mCache.mCurrentInfo = info;

            return info;
        } catch (ParseException e) {
            throw new ParseException(0, "Error while trying to display node info: " + e);
        }
    }
}
