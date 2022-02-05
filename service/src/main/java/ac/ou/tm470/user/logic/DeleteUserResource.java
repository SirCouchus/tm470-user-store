package ac.ou.tm470.user.logic;

import ac.ou.tm470.user.service.UserPersistenceService;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ac.ou.tm470.user.utils.Errors.*;

@Service
public class DeleteUserResource {

    private static final Logger log = LoggerFactory.getLogger(DeleteUserResource.class);

    private final UserPersistenceService userPersistenceService;

    @Autowired
    public DeleteUserResource(UserPersistenceService userPersistenceService) {
        this.userPersistenceService = userPersistenceService;
    }

    /**
     * Delete a user resource by their id
     * @param userId The id belonging to the user resource
     * @return ResponseEntity with HttpStatus
     */
    public ResponseEntity<?> delete(String userId) {

        //Try to retrieve User by provided Id.
        Optional<UserResourceResponse> optionalUserEntity = userPersistenceService.getByUserId(userId);

        // If User doesn't exist, return informative error.
        if(optionalUserEntity.isEmpty()) {
            log.info("User with id {} does not exist", userId);

            return new ResponseEntity<>(USER_NOT_FOUND_ERROR, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        userPersistenceService.deleteByUserId(userId);

        log.info("User with id {} successfully deleted", userId);
        return ResponseEntity.noContent().build();
    }
}
