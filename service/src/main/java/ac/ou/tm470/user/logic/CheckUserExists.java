package ac.ou.tm470.user.logic;

import ac.ou.tm470.user.service.UserPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static ac.ou.tm470.user.utils.Errors.USER_NOT_FOUND_ERROR;

@Service
public class CheckUserExists {

    private static final Logger log = LoggerFactory.getLogger(CheckUserExists.class);

    private final UserPersistenceService userPersistenceService;

    @SuppressWarnings("unused")
    @Autowired
    public CheckUserExists(UserPersistenceService userPersistenceService) {
        this.userPersistenceService = userPersistenceService;
    }

    /**
     * Check if user exists by specified username
     * @param username The username to search for
     * @return Response indicating whether user exists
     */
    public ResponseEntity<?> check(String username) {

        log.debug("Checking is user with username [{}] exists.", username);

        if(userPersistenceService.getByUsername(username)){
            log.info("User found.");
            return ResponseEntity.noContent().build();
        }

        log.info(USER_NOT_FOUND_ERROR);
        return new ResponseEntity<>(USER_NOT_FOUND_ERROR, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

}
