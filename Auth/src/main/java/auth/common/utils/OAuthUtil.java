package auth.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
@Component
public class OAuthUtil {
    @Value("${vk.client}")
    private String CLIENT_ID;
    @Value("${vk.secret}")
    private String CLIENT_SECRET;
    @Value("${vk.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${vk.authorization-grant-type}")
    private String AUTHORIZATION_GRANT_TYPE;
    @Value("${vk.token}")
    private String TOKEN_URL;
    @Value("${vk.scope}")
    private String SCOPE;
    @Value("${vk.response-type}")
    private String RESPONSE_TYPE;
    @Value("${vk.version}")
    private String VERSION;

    private final RestTemplate restTemplate;

    public OAuthUtil() {
            this.restTemplate = new RestTemplate();
        }

    public String generateVkAuthUrl(){
        return String.format("https://oauth.vk.com/authorize?client_id=%s&redirect_uri=%s&display=page&scope=%s&response_type=%s&v=%s",
                CLIENT_ID, REDIRECT_URI, SCOPE, RESPONSE_TYPE, VERSION);
    }
    public String requestAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(TOKEN_URL)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("client_secret", CLIENT_SECRET)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("code", code)
                .queryParam("authorization-grant-type", AUTHORIZATION_GRANT_TYPE);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                builder.toUriString(),
                entity,
                String.class);

        // Получение тела ответа в виде JSON-строки
        String responseBody = responseEntity.getBody();
        System.out.println("responseBody=" + responseBody);

        // Парсинг JSON и извлечение токена доступа
        String accessToken = extractAccessToken(responseBody);
        System.out.println("accessToken=" + accessToken);

        return accessToken;
    }
    private String extractAccessToken(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
            return (String) responseMap.get("access_token");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.vk.com/method/users.get")
                .queryParam("access_token", accessToken)
                .queryParam("v", "5.131");

        ResponseEntity<String> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            return null;
        }
    }

    public JsonNode processJsonResponse(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonResponse);

            JsonNode responseArrayNode = jsonNode.get("response");
                JsonNode responseObjectNode = responseArrayNode.get(0);
                return responseObjectNode;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }

    }

}
