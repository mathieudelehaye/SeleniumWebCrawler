//
//  SearchResult.java
//
//  Created by Mathieu Delehaye on 14/03/2023.
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

package selenium.webcrawler.model.DB;

import java.util.HashMap;

public class SearchResult {
    private String mId;
    private String mName;
    private HashMap<String, String> mAddressLines = new HashMap<>();
    private HashMap<String, String> mCityLines = new HashMap<>();

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public String[] getAddressLines() {
        return mAddressLines.values().toArray(new String[0]);
    }

    public String[] getCityLines() {
        return mCityLines.values().toArray(new String[0]);
    }

    public void setField(String name, String value, String line) throws Exception {
        switch (name) {
            case "point_id":
                mId = value;
                break;
            case "point_name":
            default:
                mName = value;
                break;
            case "point_address":
                if (line == null || line.equals("")) {
                    throw new Exception("Setting address line without providing a line number");
                }

                mAddressLines.put(line, value);
                break;
            case "point_city":
                if (line == null || line.equals("")) {
                    throw new Exception("Setting city line without providing a line number");
                }

                mCityLines.put(line, value);
                break;
        }
    }

    public SearchResult() {
        mId = "";
        mName = "";
    }

    public SearchResult(String id, String name, HashMap<String, String> addressLines, HashMap<String, String> cityLines) {
        mId = id;
        mName = name;
        mAddressLines = addressLines;
        mCityLines = cityLines;
    }
}
