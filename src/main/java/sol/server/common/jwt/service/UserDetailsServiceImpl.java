package sol.server.common.jwt.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sol.server.core.entity.Product;
import sol.server.core.entity.User;
import sol.server.core.repository.ProductRepository;
import sol.server.core.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("1424124");
        try {
            User user = userRepository.findByUserName(username)
                    .orElseThrow();
            String pw = productRepository.findById(user.getUserId()).orElseThrow(() -> new UsernameNotFoundException("해당하는 기기를 찾을 수 없습니다.")).getUuidPw();
            UserContext userContext = UserContext.userContextBuilder().user(user).build();
            log.info(userContext.getUserName());
            return userContext;
        } catch (Exception ex) {
            // 예외가 발생하면 createUserDetails 메서드 호출

            log.info("createUserDetails("+username+")");
            return createUserDetails(username);
        }
    }

//    @Bean
    private UserDetails createUserDetails(String userName) {
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        log.info("1"+user.getUserName());
        Optional<Product> bId = productRepository.findById(user.getUserId());
        String pw = bId.orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다.")).getUuidPw();
        log.info("2"+pw);

        Collection authorities = authorities(userName);
        UserDetailsImpl build = UserDetailsImpl.builder().userName(userName).uuidPw(pw).build();
        log.info(build.getUsername());
//        log.info();
        //        UserContext userContext = UserContext.userContextBuilder().
//                username(userName).enabled(true)
//                .accountNonExpired(true).credentialsNonExpired(true)
//                .accountNonLocked(true).authorities(authorities).password(pw).userName(userName).uuidPw(pw).build();
        UserContext userContext = new UserContext(userName, pw, true, true, true, true, authorities, userName, pw);
        log.info("3", userContext.getUsername());
        return build;
    }
    private static Collection authorities(String userName){
        Collection authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        // DB에 저장한 USER_ROLE 이 1이면 ADMIN 권한, 아니면 ROLE_USER 로 준다.
//        if(memberDTO.getUser_role().equals("1")){
//            authorities.add(new SimpleGrantedAuthority("ADMIN"));
//        }else{
//            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//        }
        return authorities;
    }
}
