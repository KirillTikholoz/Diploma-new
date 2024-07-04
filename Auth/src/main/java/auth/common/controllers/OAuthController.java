package auth.common.controllers;

import auth.common.UserDetails.CustomUserDetails;
import auth.common.dtos.VkResponseUserInfoDto;
import auth.common.services.OAuthUserService;
import auth.common.utils.JwtTokenUtils;
import auth.common.utils.OAuthUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequiredArgsConstructor
public class OAuthController {
    private static final Logger logger = LoggerFactory.getLogger(OAuthController.class);
    private final OAuthUtil oAuthUtil;
    private final OAuthUserService oAuthUserService;
    private final JwtTokenUtils jwtTokenUtils;
    @Value("${vk.main-front-page}")
    private String mainFrontPage;
    @Value("${vk.error-page}")
    private String errorPage;

    @GetMapping("/oauth")
    public RedirectView  redirectVkAuth(){
        return new RedirectView(oAuthUtil.generateVkAuthUrl());
    }

    @GetMapping("/callback")
    public String getUserInfo(@RequestParam("code") String code){
        //RedirectView
        try {
            String accessTokenVk = oAuthUtil.requestAccessToken(code);
            VkResponseUserInfoDto vkResponseUserInfoDto = oAuthUtil.getUserInfo(accessTokenVk);

            oAuthUserService.findByUsername(vkResponseUserInfoDto.getId())
                    .orElseGet(() -> {
                        return oAuthUserService.createNewUser(vkResponseUserInfoDto);
                    });

            CustomUserDetails customUserDetails = oAuthUserService.loadUserByUsername(vkResponseUserInfoDto.getId());

            String accessToken = jwtTokenUtils.generateAccessToken(customUserDetails);
            String refreshToken = jwtTokenUtils.generateRefreshTokenUserVk(customUserDetails);

            jwtTokenUtils.makeCookies(accessToken, refreshToken);

            //return new RedirectView(mainFrontPage);
            return mainFrontPage;

        } catch (RestClientException | NullPointerException e){
            //return RedirectView(errorPage);
            return errorPage;
        }

    }
}
