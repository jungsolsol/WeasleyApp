package sol.server.common.auth.service;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sol.server.common.auth.dto.AppRequestDto;
import sol.server.common.auth.dto.EspRequestDto;
import sol.server.common.jwt.Token;
import sol.server.common.jwt.util.JwtUtil;
import sol.server.core.entity.Product;
import sol.server.core.entity.User;
import sol.server.core.repository.ProductRepository;
import sol.server.core.repository.UserRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;

    private final AuthenticationManager authenticationManager;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     *
     * @param dto
     * @return
     */


    /**
     * Authentication completed
     * when device set-up
     * @param dto
     */
    @Transactional(readOnly = false)
    @Override
    public void saveProductUUID(EspRequestDto dto) {
        String uuidPw = passwordEncoder.encode(dto.getApi_key());
        Product product = EspRequestDto.toEntity(dto, uuidPw);
        productRepository.save(product);
    }


    /**
     * Authentication completed
     * when a user with a device uses the app.
     * @param dto
     */
    @Transactional(readOnly = false)
    @Override
    public Token saveUserInfoAndProductUUID(AppRequestDto dto) {


        Product p = productRepository.findByUuid(dto.getUuid()).orElseThrow(
                () -> new NoSuchElementException());
        User user = User.builder().product(p).userName(dto.getUserName()).build();
        userRepository.save(user);
        userRepository.flush();
        //Auth User
        System.out.println(user.getUserId());
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUserName(), p.getUuidPw()));

        return jwtUtil.generateToken(user.getUserName(),authentication);
    }

}
