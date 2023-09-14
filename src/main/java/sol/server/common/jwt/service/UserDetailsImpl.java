package sol.server.common.jwt.service;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sol.server.core.entity.User;

import java.util.ArrayList;
import java.util.Collection;

@Data
@AllArgsConstructor
@Builder
public class UserDetailsImpl implements UserDetails {


    private String userName;
    private String uuidPw;

    private Collection<? extends GrantedAuthority> authorities;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자의 권한 정보를 반환
        Collection authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ADMIN"));
        return authorities; // 권한 정보가 있다면 해당 정보를 반환하도록 구현
    }
    @Override
    public String getPassword() {
        return uuidPw;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
