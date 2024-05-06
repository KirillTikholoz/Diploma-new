package auth.common.controllers;

import auth.common.UserDetails.OAuthUserDetails;
import auth.common.domain.OAuthUser;
import auth.common.services.OAuthUserService;
import auth.common.utils.JwtTokenUtils;
import auth.common.utils.OAuthUtil;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequiredArgsConstructor
public class OAuthController {
    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
    private final OAuthUtil oAuthUtil;
    private final OAuthUserService oAuthUserService;
    private final JwtTokenUtils jwtTokenUtils;

    @GetMapping("/oauth")
    public RedirectView  redirectVkAuth(){
        return new RedirectView(oAuthUtil.generateVkAuthUrl());
    }

    @GetMapping("/callback")
    public String getUserInfo(@RequestParam("code") String code){
        String accessTokenVk = oAuthUtil.requestAccessToken(code);
        logger.info("accessToken=" + accessTokenVk);

        String jsonUserInfo = oAuthUtil.getUserInfo(accessTokenVk);
        logger.info("UserInfo=" + jsonUserInfo);

        JsonNode userInfo = oAuthUtil.processJsonResponse(jsonUserInfo);

        Long id = userInfo.get("id").asLong();

        oAuthUserService.findById(id)
                .orElseGet(() -> {
                    OAuthUser newOAuthUser = new OAuthUser();
                    newOAuthUser.setId(id);
                    newOAuthUser.setFirstName(userInfo.get("first_name").asText());
                    newOAuthUser.setLastName(userInfo.get("last_name").asText());
                    return oAuthUserService.createNewUser(newOAuthUser);
                });

        OAuthUserDetails oAuthUserDetails = oAuthUserService.loadUserById(id);

        String accessToken = jwtTokenUtils.generateAccessTokenForUserVk(oAuthUserDetails);

        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setMaxAge(86400);
        accessTokenCookie.setHttpOnly(true);
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        response.addCookie(accessTokenCookie);

        return "Все ок";
    }
}
