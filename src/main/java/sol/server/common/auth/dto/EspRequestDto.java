package sol.server.common.auth.dto;

import lombok.Data;
import sol.server.core.entity.Product;

@Data
public class EspRequestDto {

    private String api_key;


    public static Product toEntity(EspRequestDto dto, String uuidPw) {
        return Product.builder().uuid(dto.getApi_key()).uuidPw(uuidPw).build();
    }
}
