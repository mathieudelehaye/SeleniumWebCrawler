//
//  CrawledResultInfosDBEntry.java
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
//  <https://www.gnu.org/licenses/>.

package selenium.webcrawler.model.DB;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.WriteResult;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class CrawledResultInfosDBEntry {
    private SearchResult mEntry;
    private String mEntryKey;
    private Firestore mDatabase;

    public CrawledResultInfosDBEntry(String keySeed, SearchResult data) throws IOException {

        final String DBName = "beautyorder-fa43e";

        // Generate a random entry key from the provided seed
        byte[] hash;
        StringBuilder uid = new StringBuilder();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            hash = md.digest(keySeed.getBytes(StandardCharsets.UTF_8));
            mEntryKey = UUID.nameUUIDFromBytes(hash).toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }

        var serviceAccount = new
            FileInputStream("/Volumes/portable-ssd/Web_Development/_J/SeleniumWebCrawler/"
                + "beautyorder-fa43e-firebase-adminsdk-92z34-985c9b2d0f.json");
        FirestoreOptions firestoreOptions =
            FirestoreOptions.getDefaultInstance().toBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setProjectId(DBName)
                .build();

        mDatabase = firestoreOptions.getService();

        mEntry = data;
    }

    public void createFields() {
        Map<String, String> entryData = new HashMap<>();

        entryData.put("PointName", mEntry.getName());

        final int addressLineCount = mEntry.getAddressLines().length;
        for (int i = 0; i < addressLineCount; i++) {
            entryData.put("Address" + i, mEntry.getAddressLines()[i]);
        }

        final int citLineCount = mEntry.getCityLines().length;
        for (int i = 0; i < citLineCount; i++) {
            entryData.put("City" + i, mEntry.getCityLines()[i]);
        }

        ApiFuture<WriteResult> future = mDatabase.collection("crawledRPInfos-Edinburgh")
            .document(mEntryKey).set(entryData);

        try {
            System.out.println("New info successfully written to the database for entry: " + mEntryKey
                + ". Update time : " + future.get().getUpdateTime());
        } catch (Exception e) {
            System.out.println("Error writing user info to the database: " + e);
        }
    }
}
