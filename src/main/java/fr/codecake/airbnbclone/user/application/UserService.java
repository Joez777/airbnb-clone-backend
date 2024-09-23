package fr.codecake.airbnbclone.user.application;

import fr.codecake.airbnbclone.user.application.dto.ReadUserDTO;
import fr.codecake.airbnbclone.user.domain.Authority;
import fr.codecake.airbnbclone.user.domain.User;
import fr.codecake.airbnbclone.user.mapper.UserMapper;
import fr.codecake.airbnbclone.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import fr.codecake.airbnbclone.infrastructure.config.SecurityUtils;


import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional(readOnly = true)
    public ReadUserDTO getAuthenticatedUserFromSecurityContext() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getUsername(); // Assuming username is email
        return getByEmail(email).orElseThrow();
    }

    @Transactional(readOnly = true)
    public Optional<ReadUserDTO> getByEmail(String email) {
        Optional<User> oneByEmail = userRepository.findByEmail(email);
        return oneByEmail.map(userMapper::readUserDTOToUser);
    }

    public void syncUserByEmail(String email, boolean forceResync) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            // Update the user if needed based on forceResync flag
            if (forceResync) {
                User user = existingUser.get();
                updateUser(user); // Update user details
            }
        } else {
            // Handle case where user does not exist
            // Optionally create a new user or handle accordingly
        }
    }

    private void updateUser(User user) {
        Optional<User> userToUpdateOpt = userRepository.findByEmail(user.getEmail());
        if (userToUpdateOpt.isPresent()) {
            User userToUpdate = userToUpdateOpt.get();
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setAuthorities(user.getAuthorities());
            userToUpdate.setImageUrl(user.getImageUrl());
            userRepository.saveAndFlush(userToUpdate);
        }
    }

    @Transactional(readOnly = true)
    public Optional<ReadUserDTO> getByPublicId(UUID publicId) {
        Optional<User> oneByPublicId = userRepository.findByPublicId(publicId);
        return oneByPublicId.map(userMapper::readUserDTOToUser);
    }

    @Transactional
    public void addLandlordRoleToUser(ReadUserDTO userDTO) {
        // Fetch the user by ID
        User user = userRepository.findByPublicId(userDTO.publicId())
                .orElseThrow(() -> new UserException("User not found"));

        // Create the landlord role if it doesn't exist
        Authority landlordRole = new Authority();
        landlordRole.setName(SecurityUtils.ROLE_LANDLORD);

        // Check if the role is already present
        if (!user.getAuthorities().contains(landlordRole)) {
            user.getAuthorities().add(landlordRole);
            userRepository.saveAndFlush(user);
        }
    }
}
