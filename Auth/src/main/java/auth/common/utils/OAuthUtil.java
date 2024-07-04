package auth.common.utils;

import auth.common.dtos.VkResponseAccessTokenDto;
import auth.common.dtos.VkResponseListUserInfoDto;
import auth.common.dtos.VkResponseUserInfoDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
    @Value("${vk.auth-url}")
    private String AUTH_URL;

    private final RestTemplate restTemplate;

    public OAuthUtil() {
            this.restTemplate = new RestTemplate();
        }

    public String generateVkAuthUrl(){
        return UriComponentsBuilder.fromHttpUrl(AUTH_URL)
                .queryParam("client_id", CLIENT_ID)
                .queryParam("redirect_uri", REDIRECT_URI)
                .queryParam("display", "page")
                .queryParam("scope", SCOPE)
                .queryParam("response_type", RESPONSE_TYPE)
                .queryParam("v", VERSION)
                .toUriString();
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

        ResponseEntity<VkResponseAccessTokenDto> responseEntity = restTemplate.postForEntity(
                builder.toUriString(),
                entity,
                VkResponseAccessTokenDto.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            VkResponseAccessTokenDto responseBody = responseEntity.getBody();
            return responseBody.getAccess_token();
        } else {
            throw new RestClientException("Ошибка с получением access токена: " + responseEntity.getStatusCode());
        }
    }

    public VkResponseUserInfoDto getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://api.vk.com/method/users.get")
                .queryParam("access_token", accessToken)
                .queryParam("v", "5.131");

        ResponseEntity<VkResponseListUserInfoDto> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                VkResponseListUserInfoDto.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody().getUser();
        } else {
            throw new RestClientException("Ошибка с получением информации о пользователе: " + response.getStatusCode());
        }
    }

}
