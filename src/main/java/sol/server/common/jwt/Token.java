package sol.server.common.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Token {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String key;

}

