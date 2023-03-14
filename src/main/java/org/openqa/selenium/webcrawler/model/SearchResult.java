//
//  SearchResult.java
//
//  Created by Mathieu Delehaye on 14/03/2023.
//
//  BeautyAndroid: An Android app to order and recycle cosmetics.
//
//  Copyright © 2023 Mathieu Delehaye. All rights reserved.
//
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
//  FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.

package org.openqa.selenium.webcrawler.model;

public class SearchResult {
    private int mId;
    private String mName;
    private String[] mAddressLines;
    private String[] mCityLines;

    public int getId() {
        return mId;
    }

    public void setId(int value) {
        mId = value;
    }

    public String getName() {
        return mName;
    }

    public void setName(String value) {
        mName = value;
    }

    public String[] getAddressLines() {
        return mAddressLines;
    }

    public void setAddressLines(String[] value) {
        mAddressLines = value;
    }

    public String[] getCityLines() {
        return mCityLines;
    }

    public void setCityLines(String[] value) {
        mCityLines = value;
    }

    public SearchResult() {
        mId = 0;
        mName = new String("");
    }

    public SearchResult(int id, String name, String[] addressLines, String[] cityLines) {
        mId = id;
        mName = name;
        mAddressLines = addressLines;
        mCityLines = cityLines;
    }
}
