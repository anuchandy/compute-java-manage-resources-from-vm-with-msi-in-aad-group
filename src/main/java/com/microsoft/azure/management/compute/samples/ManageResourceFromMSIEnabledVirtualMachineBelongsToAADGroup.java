/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 */

package com.microsoft.azure.management.compute.samples;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public final class ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup {
    private final String resource = "https://management.azure.com/";
    private final String apiVersion = "2018-02-01";
    private final String objectId = null;
    private final String clientId = null;
    private final String identityId = "/subscriptions/ec0aa5f7-9e78-40c9-85cd-535c6305b380/resourcegroups/1c2b4bf7e1f448e/providers/Microsoft.ManagedIdentity/userAssignedIdentities/msi-idd7b42487";

    public void Foo() throws MalformedURLException, IOException {
        StringBuilder payload = new StringBuilder();
        payload.append("apiVersion");
        payload.append("=");
        payload.append(URLEncoder.encode(this.apiVersion, "UTF-8"));
        payload.append("&");
        payload.append("resource");
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

        URL url = new URL(String.format("http://169.254.169.254/metadata/identity/oauth2/token?%s", payload.toString()));

        System.out.println("Connecting to:" + url.toString());
        //
        HttpURLConnection connection = null;
        //
        try {
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Metadata", "true");
            connection.setDoOutput(true);

            connection.connect();

            System.out.println("Connected");

            OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
            wr.flush();

            InputStream stream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 100);
            String result = reader.readLine();
            //
            System.out.println("Done, result is:");
            //
            System.out.println(result);
            //

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    public static void main(String[] args) throws MalformedURLException, IOException {
        ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup msi = new ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup();
        msi.Foo();
    }

    private ManageResourceFromMSIEnabledVirtualMachineBelongsToAADGroup() {
    }
}