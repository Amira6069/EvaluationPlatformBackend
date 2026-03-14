package com.governance.evaluation.security;
import com.governance.evaluation.entity.User;
import com.governance.evaluation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor

public class UserDetailsServiceImpl implements UserDetailsService{
	 private final UserRepository userRepository;
	    
	    @Override
	    @Transactional(readOnly = true)
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
	        
	        return buildUserDetails(user);
	    }
	    
	    private UserDetails buildUserDetails(User user) {
	        List<GrantedAuthority> authorities = Collections.singletonList(
	                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
	        );
	        
	        return org.springframework.security.core.userdetails.User
	                .builder()
	                .username(user.getEmail())
	                .password(user.getPassword())
	                .authorities(authorities)
	                .accountExpired(false)
	                .accountLocked(!user.getIsActive())
	                .credentialsExpired(false)
	                .disabled(!user.getIsActive())
	                .build();
	    }

}
