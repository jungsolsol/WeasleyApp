package sol.server.common.auth.service;

import sol.server.common.auth.dto.AppRequestDto;
import sol.server.common.auth.dto.EspRequestDto;
import sol.server.common.jwt.Token;

public interface AuthService {

    void saveProductUUID(EspRequestDto dto);

    Token saveUserInfoAndProductUUID(AppRequestDto dto);
}
