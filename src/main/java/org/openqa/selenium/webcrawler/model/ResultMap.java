//
//  ResultMap.java
//
//  Created by Mathieu Delehaye on 22/03/2023.
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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.openqa.selenium.webcrawler.MyLogger;

public class ResultMap extends HashMap<String, Object> {
    private Map<String, Object> mCurrentNestedMap;

    ResultMap() {
        super();
        mCurrentNestedMap = this;
    }

    public void put(String key, String value) {
        mCurrentNestedMap.put(key, value);
    }

    public void increaseNesting() {
        var nestedMap = new HashMap<String, Object>();
        // TODO: use a random key instead, as there can me sibling children
        mCurrentNestedMap.put("child", nestedMap);
        mCurrentNestedMap = nestedMap;
    }

    public String getDigest() {

        try {
            var tmp = new StringBuilder();
            tmp.append("\n");
            parseFrom(this, 0, tmp);

            // Remove the last newline character and return the digest
            return tmp.substring(0, tmp.length() - 1);
        } catch (Exception e) {
            MyLogger.log(Level.WARNING, "Cannot provide a result digest: " + e);
            return "";
        }
    }

    private void parseFrom(Map<String, Object> start, int nesting, StringBuilder digest) throws Exception {
        MyLogger.log(Level.FINE, "mdl parseFrom entered");

        // Build the indent prefix for the current nesting level
        var indentBuilder = new StringBuilder();
        for(int i = 0; i < nesting; i++) {
            indentBuilder.append("  ");
        }
        final String indent = indentBuilder.toString();

        Set<String> keys = start.keySet();

        for(String key: keys) {
            Object value = start.get(key);

            if (value instanceof String) {
                digest.append(indent + key + ": " + value + "\n");
            } else {
                // TODO: improve the way we detect the Object type
                Map<String, Object> mapValue;

                try {
                    mapValue = (Map<String, Object>) start.get(key);
                    parseFrom(mapValue, ++nesting, digest);
                } catch (Exception e) {
                    throw new Exception("Result map contains a value which isn't a String or another map");
                }
            }
        }
    }
}
