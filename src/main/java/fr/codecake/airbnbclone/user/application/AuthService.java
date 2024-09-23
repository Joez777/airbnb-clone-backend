package fr.codecake.airbnbclone.user.application;

import fr.codecake.airbnbclone.infrastructure.config.SecurityUtils;
import fr.codecake.airbnbclone.user.application.dto.ReadUserDTO;
import fr.codecake.airbnbclone.user.domain.Authority;
import fr.codecake.airbnbclone.user.domain.User;
import fr.codecake.airbnbclone.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Add LANDLORD role to user
    public void addLandlordRoleToUser(ReadUserDTO readUserDTO) {
        User user = userRepository.findByEmail(readUserDTO.email())
                .orElseThrow(() -> new UserException(String.format("User with email %s not found", readUserDTO.email())));

        if (readUserDTO.authorities().stream().noneMatch(role -> role.equals(SecurityUtils.ROLE_LANDLORD))) {
            // Add ROLE_LANDLORD to the user
            Authority landlordRole = new Authority();
            landlordRole.setName(SecurityUtils.ROLE_LANDLORD);
            user.getAuthorities().add(landlordRole);
            userRepository.save(user);
        }
    }

    // Check if current user has LANDLORD role
    public boolean currentUserHasLandlordRole() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getName().equals(SecurityUtils.ROLE_LANDLORD));
    }
}
