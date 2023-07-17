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

package selenium.webcrawler.model.CrawlResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import selenium.webcrawler.templates.Helpers;
import selenium.webcrawler.templates.MyLogger;

public class ResultMap {
    private Map<String, Object> mStart;
    private Map<String, Object> mCurrentNestedMap;

    ResultMap() {
        mStart = new HashMap<>();
        mCurrentNestedMap = mStart;
    }

    public void put(String key, String value) {
        mCurrentNestedMap.put(key, value);
    }

    public Map<String, Object> increaseNesting(String childKeySeed) {
        final String uUID = Helpers.getUUID(childKeySeed);

        var nestedMap = new HashMap<String, Object>();
        // TODO: use a random key instead, as there can be sibling children
        mCurrentNestedMap.put(uUID, nestedMap);

        // Return the parent, so we can change the current node back to it
        Map<String, Object> res = mCurrentNestedMap;

        mCurrentNestedMap = nestedMap;

        return res;
    }

    public void changeNodeTo(Map<String, Object> node) {
        mCurrentNestedMap = node;
    }

    public String find(String key, Boolean fromRoot) {
        String res = null;

        try {
            res = searchFrom(fromRoot ? mStart : mCurrentNestedMap, key, 0, new StringBuilder());
        } catch (Exception e) {
            MyLogger.log(Level.WARNING, "Cannot provide a result digest: " + e);
        }

        return res;
    }

    public String getDigest(Boolean fromRoot) {
        try {
            var tmp = new StringBuilder();
            tmp.append("\n");

            // No key is searched
            final String searchedKey = "";

            searchFrom(fromRoot ? mStart : mCurrentNestedMap, searchedKey, 0, tmp);

            // Remove the last newline character and return the digest
            return tmp.substring(0, tmp.length() - 1);
        } catch (Exception e) {
            MyLogger.log(Level.WARNING, "Cannot provide a result digest: " + e);
            return "";
        }
    }

    private String searchFrom(Map<String, Object> startNode, String searchedKey, int startNesting, StringBuilder output) throws Exception {
        Set<String> keys = startNode.keySet();
        ArrayList<Map<String, Object>> nodesToSearch = new ArrayList<>();

        // Breadth-first search:
        for(String key: keys) {
            // If we found the key, just return the value
            if (searchedKey != null &&
                !searchedKey.equals("") &&
                key.equals(searchedKey)) {

                output.setLength(0);
                output.append(startNode.get(key));
                return output.toString();
            }

            // Otherwise, print the data
            Object value = startNode.get(key);

            if (value instanceof String) {
                output.append(Helpers.generateIndent(startNesting) + key + ": " + value + "\n");
            } else {
                // TODO: improve the way we detect the Object type
                try {
                    nodesToSearch.add((Map<String, Object>) value);
                } catch (Exception e) {
                    throw new Exception("Result map contains a value which isn't a String or another map");
                }
            }
        }

        // Search the child nodes
        for (Map<String, Object> node : nodesToSearch) {
            final var res = searchFrom(node, searchedKey, ++startNesting, output);

            // If a key was found, return straightforward
            if (!res.equals("") &&
                searchedKey != null &&
                !searchedKey.equals("")) {

                return res;
            }
        }

        return "";
    }
}
