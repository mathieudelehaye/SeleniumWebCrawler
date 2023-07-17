//
//  MyLogger.java
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

package selenium.webcrawler.templates;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyLogger {
    private static MyLogger mInstance;
    final private Logger mLogger;
    final Handler mSystemOut;

    private MyLogger() {
        mLogger = Logger.getGlobal();
        mSystemOut = new ConsoleHandler();
        updateLevel(Level.ALL);
        mLogger.addHandler(mSystemOut);

        // Prevent logs from processed by default Console handler.
        mLogger.setUseParentHandlers(false);
    }

    private static MyLogger getInstance() {
        if (mInstance == null) {
            mInstance = new MyLogger();
        }

        return mInstance;
    }

    public static void log(Level level, String format, String... args) {
        getInstance().writeLog(level, format, args);
    }

    public static void setLevel(Level newLevel) {
        getInstance().updateLevel(newLevel);
    }

    private void writeLog(Level level, String format, String... args) {
        mLogger.log(level, format, args);
    }

    private void updateLevel(Level newLevel) {
        // Set handler
        mSystemOut.setLevel(newLevel);
        mLogger.setLevel(newLevel);
    }
}
