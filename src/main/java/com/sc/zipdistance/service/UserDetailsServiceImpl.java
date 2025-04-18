package com.sc.zipdistance.service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sc.zipdistance.model.entity.CustomUserDetails;
import com.sc.zipdistance.model.entity.Role;
import com.sc.zipdistance.model.entity.User;
import com.sc.zipdistance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

        return new CustomUserDetails(user.getId(), user.getEmail(), user.getPasswordHash(),
                getAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles) {
        return roles.stream()
                .flatMap(role -> {
                    // Add role as authority (e.g., ROLE_ADMIN)
                    Stream<SimpleGrantedAuthority> roleAuthority =
                            Stream.of(new SimpleGrantedAuthority(role.getName()));
                    // Add permissions tied to the role
                    Stream<SimpleGrantedAuthority> permissionAuthorities = role.getPermissions().stream()
                            .map(permission -> new SimpleGrantedAuthority(permission.getName()));
                    return Stream.concat(roleAuthority, permissionAuthorities);
                })
                .collect(Collectors.toSet());
    }

}
