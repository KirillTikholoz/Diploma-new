package auth.common.dtos;

import lombok.Data;

import java.util.List;

@Data
public class VkResponseListUserInfoDto {
    private List<VkResponseUserInfoDto> response;

    public VkResponseUserInfoDto getUser() {
        if (response != null && !response.isEmpty()) {
            return response.get(0);
        }
        else {
            return null;
        }
    }
}
