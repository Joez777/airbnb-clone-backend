package fr.codecake.airbnbclone.user.presentation;

import fr.codecake.airbnbclone.user.application.UserService;
import fr.codecake.airbnbclone.user.application.dto.ReadUserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthResource {

    private final UserService userService;

    public AuthResource(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/get-authenticated-user")
    public ResponseEntity<ReadUserDTO> getAuthenticatedUser(
            @AuthenticationPrincipal UserDetails user, @RequestParam boolean forceResync) {
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            // Fetch the user's email from UserDetails
            String email = user.getUsername();
    
            // Sync user information using the email
            userService.syncUserByEmail(email, forceResync);
    
            // Fetch the authenticated user DTO
            ReadUserDTO connectedUser = userService.getAuthenticatedUserFromSecurityContext();
            return new ResponseEntity<>(connectedUser, HttpStatus.OK);
        }
    }
    

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        // Invalidate the session and return a generic logout message
        request.getSession().invalidate();
        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }
}
