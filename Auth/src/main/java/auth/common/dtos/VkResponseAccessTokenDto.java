package auth.common.dtos;

import lombok.Data;

@Data
public class VkResponseDto {
    private String access_token;
    private Long expires_in;
    private Long user_id;
}
