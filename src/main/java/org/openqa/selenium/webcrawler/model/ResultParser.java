//
//  ResultParser.java
//
//  Created by Mathieu Delehaye on 15/03/2023.
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

import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.*;

public class ResultParser {
    private WebDriver mDriver;
    private JSONResultStructParser mStructParser;
    private Map<String, Object> mResults = new HashMap<>();

    public ResultParser(WebDriver driver, JSONResultStructParser structure) throws Exception {
        mDriver = driver;
        mStructParser = structure;

        // TODO: remove and parse it from the JSON struct file
        final String resultIdAttribute = "data-store-id";
        final String resultDivClass = "wpsl-store-location";
        final String resultAddressSpanClass = "wpsl-street";

        // The root element will generally be a div with an id attribute:
        try {
            final String topDivId = mStructParser.getCurrentAttribute("id");

            WebElement topDiv = mDriver.findElement(By.id(topDivId));

            WebElement resultListHead = topDiv.findElements(By.xpath("./child::*")).get(0);

            List<WebElement> resultListItems = resultListHead.findElements(By.xpath("./child::*"));
            final int resultCount =  resultListItems.size();
            System.out.println(resultCount);

            for (final WebElement listItem: resultListItems) {
                var result = new SearchResult();

                System.out.println("");

                // Id
                result.setId(Integer.valueOf(listItem.getAttribute(resultIdAttribute)));
                System.out.println("id = " + result.getId());

                // Name
                final WebElement itemDiv = listItem.findElements(By.className(resultDivClass)).get(0);
                final WebElement divParagraph = itemDiv.findElements(By.xpath("./child::*")).get(0);

                final List<WebElement> itemLines = divParagraph.findElements(By.xpath("./child::*"));
                final int itemLineCount = itemLines.size();
                System.out.println("itemLineCount = " + itemLineCount);

                result.setName(itemLines.get(0).getText());
                System.out.println("name = " + result.getName());

                // Address
                final List<WebElement> itemAddressLines = divParagraph.findElements(By.className(resultAddressSpanClass));
                final int addressLineCount = itemAddressLines.size();
                System.out.println("addressLineCount = " + addressLineCount);

                final ArrayList<String> addressLines = new ArrayList<>();
                for (final WebElement addressLinElement: itemAddressLines) {
                    addressLines.add(addressLinElement.getText());
                }
                result.setAddressLines(addressLines.toArray(new String[0]));
                for (final String addressLine: result.getAddressLines()) {
                    System.out.println("address line = " + addressLine);
                }

                // City
                final List<WebElement> itemCityLines = itemLines.subList(1 + addressLineCount, itemLineCount);

                final ArrayList<String> cityLines = new ArrayList<>();
                for (final WebElement itemCityLineElement: itemCityLines) {
                    cityLines.add(itemCityLineElement.getText());
                }
                result.setCityLines(cityLines.toArray(new String[0]));
                for (final String cityLine: result.getCityLines()) {
                    System.out.println("city line = " + cityLine);
                }

                // Write data to the DB
                //(new CrawledRPInfosDBEntry(String.valueOf(result.getId()), result)).createFields();

                //break;
            }
        } catch (ParseException e) {
            throw new Exception("Exception while parsing the search result: " + e.getMessage());
        }
    }
}
