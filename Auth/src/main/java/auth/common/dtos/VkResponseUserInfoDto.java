package auth.common.dtos;

import lombok.Data;

@Data
public class VkResponseUserInfoDto {
    private String id;
    private String first_name;
    private String last_name;
    private boolean can_access_closed;
    private boolean is_closed;
}
