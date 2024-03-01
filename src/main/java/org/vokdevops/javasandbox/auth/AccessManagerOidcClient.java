package org.vokdevops.javasandbox.auth;

import org.vokdevops.javasandbox.utils.HttpUtils;
import org.vokdevops.javasandbox.utils.JsonUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Vishakh Oommen Koshy on 01/03/2024
 */
public class AccessManagerOidcClient {
    private static Map<String, String> oidcClientMap = new HashMap<>();

    /**
     * create OAuth 2.0 client for use in test scope
     * @param baseUrl
     * @param clientName
     */
    public static void createAmOAuth2Client(String baseUrl, String clientName) {

        try {
            // Authenticate with AM and retrieve token
            String tokenId = "tvLVXF1nOGe_P5o3jOG1bondHps.*AAJTSQACMDIAAlNLABxKenM2RC8vWFlnSkg1TkpsdXB2bDhmcDdNZkk9AAR0eXBlAANDVFMAAlMxAAIwMQ..*";

            Path path = Paths.get("src/main/resources/data/oauth_client_req.json");
            Stream<String> lines = Files.lines(path);
            String jsonBody = lines.collect(Collectors.joining("\n"));
            lines.close();

            String[] headers = {"Accept-API-Version","resource=1.0",
                    "Content-Type","application/json",
                    "Accept","application/json",
                    "iplanetDirectoryPro",tokenId};

            /* Create OAuth 2.0 client */

            HttpResponse<String> response = HttpUtils.executeHttpRequest(baseUrl,
                    "/am/json/realms/root/realms/root/realm-config/agents/OAuth2Client/" + clientName,
                    headers,"PUT", jsonBody, true);

        } catch (InterruptedException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException |
                 IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String getAccessToken(String baseUrl, String clientName, String clientSecret){

        /* Create OAuth 2.0 client */
        createAmOAuth2Client(baseUrl, clientName);

        String auth = Base64.getEncoder().encodeToString(clientName.concat(":").concat(clientSecret).getBytes());
        String[] headers = {"Authorization","Basic "+auth, "Content-Type", "application/x-www-form-urlencoded"};
        String body = "grant_type=client_credentials&scope=dynamic_client_registration";

        /* Get access token using OAuth 2 client */
        try {
            HttpResponse<String> response = HttpUtils.executeHttpRequest(baseUrl,
                    "/am/oauth2/realms/root/access_token",
                    headers, "POST", body, true);
            Map<String, Object> flattenedJsonMap = JsonUtils.flatten(JsonUtils.jsonDataToMap(response.body()), null);
            return String.valueOf(flattenedJsonMap.get("access_token"));
        }
        catch (InterruptedException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException |
               IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * create oidc client for use in test scope
     * @param baseUrl
     * @param clientName
     * @param clientSecret
     */
    public static void createOidcClient(String baseUrl, String clientName, String clientSecret){

        if (oidcClientMap.get("access_token") == null)
            oidcClientMap.put("access_token", getAccessToken(baseUrl, clientName, clientSecret));
        try {
            Path path = Paths.get("src/main/resources/data/oidc_client_req.json");
            Stream<String> lines = null;
            lines = Files.lines(path);
            String jsonBody = lines.collect(Collectors.joining("\n"));
            lines.close();

            String[] headers = {"Content-Type", "application/json",
                    "Authorization", "Bearer " + oidcClientMap.get("access_token")
            };
            HttpResponse<String> response = HttpUtils.executeHttpRequest(baseUrl,
                    "/am/oauth2/register",
                    headers, "POST", jsonBody, true);
            Map<String, Object> jsonDataMap = JsonUtils.jsonDataToMap(response.body());
            oidcClientMap.put("client_id",String.valueOf(jsonDataMap.get("client_id")));
            oidcClientMap.put("client_secret", String.valueOf(jsonDataMap.get("client_secret")));
        }
        catch (InterruptedException | NoSuchAlgorithmException | KeyManagementException | URISyntaxException |
               IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * retrieve a map containing oidc client information
     * @param baseUrl
     * @return
     */
    public static Map<String,String> getOidcClientMap(String baseUrl){
        if(oidcClientMap.size() == 0)
            createOidcClient(baseUrl,"cdmTestClient","forgerock");

        return oidcClientMap;
    }

    public static void testOidcClient() throws URISyntaxException, NoSuchAlgorithmException, IOException, InterruptedException, KeyManagementException {
        String baseUrl = "https://www.vokdevops.com";

        String client_id = AccessManagerOidcClient.getOidcClientMap(baseUrl).get("client_id");
        String client_secret = AccessManagerOidcClient.getOidcClientMap(baseUrl).get("client_secret");
        String[] headers = {"Content-Type","application/json",
                "Accept-API-Version","resource=2.0",
                "X-OpenAM-Username" , client_id,
                "X-OpenAM-Password", client_secret
        };
        HttpResponse<String> response = HttpUtils.executeHttpRequest(baseUrl,
                "/am/json/realms/root/authenticate",
                headers, "POST", "{}", true);

        System.out.println(response.body());
    }
}
