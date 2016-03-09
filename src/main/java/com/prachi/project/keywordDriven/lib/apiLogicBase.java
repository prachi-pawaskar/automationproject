package com.prachi.project.keywordDriven.lib;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import org.apache.log4j.Logger;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Created by prachi a. pawaskar on 11/30/2015.
 */
public class apiLogicBase extends base{

    // Variables
    public static String apiStatus, apiResponse;

    //Logger defined for class, logs created as per log4j.properties
    private static Logger log = Logger.getLogger(apiLogicBase.class);

    // Method to disable SSL certification validation, call in doGet
    public static void disableCertificateValidation()  throws Exception {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }};

        // Ignore differences between given hostname and certificate hostname
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) { return true; }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception e) {
        }
    }

    //Method to hit GET type APIs
    public static void doGet(String baseURL, String uri, String queryParam) throws Exception {
        // Disable SSL
        disableCertificateValidation();

        try {
            client = Client.create();

            //Appending uri and queryparams to base URL
            if (queryParam == null || queryParam == ""){
            webResource = client.resource(baseURL + uri);}
            else {webResource = client.resource(baseURL + uri + queryParam);}

            //Logging API to hit
            log.info("Hitting API: " + webResource.toString());
            response = webResource.accept("application/json")
                    .get(ClientResponse.class);

            //Logging apache code if not 200, no exception thrown to handle negative testing
            if (response.getStatus() != 200) {
                log.info("Apache status is not 200-Ok");
            }

            //Logging API apache code and API response
            String output = response.getEntity(String.class);
            log.info("API apache status: " + response.getStatus());
            log.info("API response: " + output);

            //Storing in base.class variables to use for assertions
            apiStatus = Integer.toString(response.getStatus());
            apiResponse = output;

        } catch (Exception e) {

            e.printStackTrace();

        }


    }

    //Method to hit POST type APIs
    public static void doPost(String baseURL, String uri, String queryParam, String postData, String postType) throws Exception {
        // Disable SSL
        disableCertificateValidation();

        try {
            client = Client.create();

            //Appending uri and queryparams to base URL
            if (queryParam == null || queryParam == ""){
                webResource = client.resource(baseURL + uri);}
            else {webResource = client.resource(baseURL + uri + queryParam);}

            //Logging API to hit
            log.info("Hitting API: " + webResource.toString());
            response = webResource.accept("application/"+postType)
                    .post(ClientResponse.class,postData);

            //Logging apache code if not 200, no exception thrown to handle negative testing
            if (response.getStatus() != 200) {
                log.info("Apache status is not 200-Ok");
            }

            //Logging API apache code and API response
            String output = response.getEntity(String.class);
            log.info("API apache status: " + response.getStatus());
            log.info("API response: " + output);

            //Storing in base.class variables to use for assertions
            apiStatus = Integer.toString(response.getStatus());
            apiResponse = output;

        } catch (Exception e) {

            e.printStackTrace();

        }


    }

}
