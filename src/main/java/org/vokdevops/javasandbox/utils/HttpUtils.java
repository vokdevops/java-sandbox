package org.vokdevops.javasandbox.utils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;

/**
 * Created by Vishakh Oommen Koshy on 29/02/2024
 */
public class HttpUtils {

    private static final TrustManager TRUST_ALL_TRUST_MANAGER = new X509ExtendedTrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket) {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
        }
    };

    /**
     * Utility method to execute an HTTP request using the core Java HTTP client (since Java ver. 11)
     * @param baseUrl
     * @param absolutePath
     * @param headers
     * @param method
     * @param body
     * @param trustAllCerts
     * @return
     * @throws URISyntaxException
     * @throws InterruptedException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     * @throws IOException
     */

    public static HttpResponse<String> executeHttpRequest(String baseUrl, String absolutePath, String[] headers, String method, String body,
                                            Boolean trustAllCerts) throws URISyntaxException, InterruptedException,
            NoSuchAlgorithmException, KeyManagementException, IOException {

        HttpClient client;
        HttpRequest.BodyPublisher httpReqBodyPub = HttpRequest.BodyPublishers.noBody();
        // Authenticate with AM and retrieve token
        String tokenId = "Lce1BfqfTmBpDZKSnIEv_bg8mjg.*AAJTSQACMDIAAlNLABxEVTZiZWtqdlZpb0pGOHNVV3I0YmdTSHFhU0k9AAR0eXBlAANDVFMAAlMxAAIwMQ..*";


        /* Skip Certificate validation if requested */
        if(trustAllCerts) {
            SSLContext sslContext = SSLContext.getInstance("SSL"); // OR TLS
            sslContext.init(null, new TrustManager[]{TRUST_ALL_TRUST_MANAGER}, new SecureRandom());
            client = HttpClient.newBuilder().sslContext(sslContext).build();
        } else{
            client = HttpClient.newHttpClient();
        }

        /* Generate Request Body for PUT and POST */
        switch(method.toUpperCase()){
            case "POST":
            case "PUT":
                httpReqBodyPub = HttpRequest.BodyPublishers.ofString(body);
            break;
            default:
                httpReqBodyPub = HttpRequest.BodyPublishers.noBody();

        }

        /* Build HTTP request */
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl.concat(absolutePath)))
                .timeout(Duration.ofSeconds(10))
                .version(HttpClient.Version.HTTP_2)
                .headers(headers)
                .method(method, httpReqBodyPub)
                .build();

        /* Execute HTTP Request */
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response;

    }
}
