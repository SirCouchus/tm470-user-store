package ac.ou.tm470.user.logic;

import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.service.UserPersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserResources {

    private static final Logger log = LoggerFactory.getLogger(GetUserResources.class);

    private final UserPersistenceService userPersistenceService;

    @Autowired
    public GetUserResources(UserPersistenceService userPersistenceService){
        this.userPersistenceService = userPersistenceService;
    }

    /**
     * Retrieve a list of all users.
     * @return A list containing each user stored in persistence.
     */
    public ResponseEntity<?> getAll() {
        log.info("Retrieving all users from persistence service.");
        List<UserResourceResponse> users = userPersistenceService.getAll();

        if(users.isEmpty()){
            log.info("No users found.");
        } else {
            log.info(String.format("%s user(s) being retrieved from persistence service", users.size()));
            log.debug("List of users being returned {}", users.get(0));
        }
        return new ResponseEntity<>(users, new HttpHeaders(), HttpStatus.OK);
    }
}
