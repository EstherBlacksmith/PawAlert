package itacademy.pawalert.infrastructure.security;

import itacademy.pawalert.infrastructure.persistence.user.UserEntity;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Adaptar UserEntity a UserDetails
        return new UserDetailsAdapter(userEntity.toDomain(), userEntity.getPasswordHash());
    }
}

