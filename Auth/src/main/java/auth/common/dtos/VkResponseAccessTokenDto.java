package auth.common.dtos;

import lombok.Data;

@Data
public class VkResponseAccessTokenDto {
    private String access_token;
    private Long expires_in;
    private Long user_id;
}
