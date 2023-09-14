package sol.server.common.jwt.service;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


@Getter
@Setter
public class UserContext extends User {

    private String userName;

    private String uuidPw;

//    private sol.server.core.entity.User user;

    public UserContext(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserContext(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired,
                       boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String userName, String uuidPw) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userName = userName;
        this.uuidPw = uuidPw;
    }

    public static UserContextBuilder userContextBuilder() {
        return new UserContextBuilder();
    }

    public static class UserContextBuilder {
        private String userName;
        private String uuidPw;
        private sol.server.core.entity.User user;
        private String username;
        private String password;
        private boolean enabled;
        private boolean accountNonExpired;
        private boolean credentialsNonExpired;
        private boolean accountNonLocked;
        private Collection<? extends GrantedAuthority> authorities;

        public UserContextBuilder() {
        }

        public UserContextBuilder userName(String userName) {
            this.userName = userName;
            return this;
        }

        public UserContextBuilder uuidPw(String uuidPw) {
            this.uuidPw = uuidPw;
            return this;
        }

        public UserContextBuilder user(sol.server.core.entity.User user) {
            this.user = user;
            return this;
        }

        public UserContextBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserContextBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserContextBuilder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public UserContextBuilder accountNonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public UserContextBuilder credentialsNonExpired(boolean credentialsNonExpired) {
            this.credentialsNonExpired = credentialsNonExpired;
            return this;
        }

        public UserContextBuilder accountNonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public UserContextBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public UserContext build() {
            UserContext userContext = new UserContext(username, password, enabled, accountNonExpired, credentialsNonExpired,
                    accountNonLocked, authorities, userName, uuidPw);
//            userContext.setUser(user);
            return userContext;

        }
    }

}
