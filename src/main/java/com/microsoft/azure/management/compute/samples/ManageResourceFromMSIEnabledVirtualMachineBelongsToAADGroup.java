/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.compute.samples;

import com.microsoft.azure.serializer.AzureJacksonAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public final class ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup {
    private final List<Integer> retrySlots = new ArrayList<>(Arrays.asList(new Integer [] { 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765 }));
    private final int maxRetry = retrySlots.size();

    private final String resource = "https://management.azure.com/";
    private final String apiVersion = "2018-02-01";
    private final String objectId = null;
    private final String clientId = null;
    private final String identityId = "/subscriptions/ec0aa5f7-9e78-40c9-85cd-535c6305b380/resourcegroups/1c2b4bf7e1f448e/providers/Microsoft.ManagedIdentity/userAssignedIdentities/msi-idd7b42487";
    private final AzureJacksonAdapter adapter;

    public MSIToken retrieveTokenFromIDMSWithRetry() throws IOException {
        StringBuilder payload = new StringBuilder();
        //
        try {
            payload.append("api-version");
            payload.append("=");
            payload.append(URLEncoder.encode(this.apiVersion, "UTF-8"));
            payload.append("&");
            payload.append("resource1");
            payload.append("=");
            payload.append(URLEncoder.encode(this.resource, "UTF-8"));
            payload.append("&");
            if (this.objectId != null) {
                payload.append("object_id");
                payload.append("=");
                payload.append(URLEncoder.encode(this.objectId, "UTF-8"));
            } else if (this.clientId != null) {
                payload.append("client_id");
                payload.append("=");
                payload.append(URLEncoder.encode(this.clientId, "UTF-8"));
            } else if (this.identityId != null) {
                payload.append("msi_res_id");
                payload.append("=");
                payload.append(URLEncoder.encode(this.identityId, "UTF-8"));
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        int retry = 1;
        while (retry <= maxRetry) {
            URL url = new URL(String.format("http://169.254.169.254/metadata/identity/oauth2/token?%s", payload.toString()));
            //
            HttpURLConnection connection = null;
            //
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Metadata", "true");
                connection.connect();
                InputStream stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 100);
                String result = reader.readLine();
                return adapter.deserialize(result, MSIToken.class);
            } catch (Exception exception) {
                if (connection.getResponseCode() == 429) {
                    int retryTimeout = retrySlots.get(new Random().nextInt(retry)) * 1000;
                    Sleep(retryTimeout);
                    retry++;
                } else {
                    throw new RuntimeException(exception);
                }
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        //
        if (retry > maxRetry) {
            throw new RuntimeException(String.format("MSI: Failed to acquire tokens after %s times", maxRetry));
        }
        return null;
    }

    private static void Sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) throws MalformedURLException, IOException {
        ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup msi = new ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup();

        MSIToken token = msi.retrieveTokenFromIDMSWithRetry();
        System.out.println(token.accessToken());
        System.out.println(token.expireOn());
        System.out.println(token.tokenType());
    }

    private ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup() {
        this.adapter = new AzureJacksonAdapter();
    }
}