package itacademy.pawalert.infrastructure.security;


import itacademy.pawalert.infrastructure.persistence.user.UserEntity;
import itacademy.pawalert.infrastructure.persistence.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

        // Adapt UserEntity to UserDetails
        return new UserDetailsAdapter(userEntity.toDomain(), userEntity.getPasswordHash());
    }

}

