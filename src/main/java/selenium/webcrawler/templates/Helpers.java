//
//  Helpers.java
//
//  Created by Mathieu Delehaye on 13/07/2023.
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
//  <https://www.gnu.org/licenses/>.

package selenium.webcrawler.templates;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.logging.Level;

public class Helpers {
    public static void sleep(long timeInMSec) {
        try {
            Thread.sleep(timeInMSec);
        } catch (InterruptedException ie) {
            System.out.println(ie);
        }
    }

    public static String getUUID(String seed) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] hash = md.digest(seed.getBytes(StandardCharsets.UTF_8));
            return UUID.nameUUIDFromBytes(hash).toString();
        } catch (NoSuchAlgorithmException nsae) {
            MyLogger.log(Level.WARNING, "Cannot provide a result digest: " + nsae);
            return "";
        }
    }

    public static String generateIndent(int level) {
        // Build the indent tab prefix for the provided nesting level
        final var indentBuilder = new StringBuilder();
        for(int i = 0; i < level; i++) {
            indentBuilder.append("  ");
        }

        return indentBuilder.toString();
    }
}
