package ac.ou.tm470.user.logic;

import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.service.UserPersistenceService;
import org.apache.commons.lang.StringUtils;
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
public class GetUserResourceById {

    private static final Logger log = LoggerFactory.getLogger(GetUserResourceById.class);

    private final UserPersistenceService userPersistenceService;

    @Autowired
    public GetUserResourceById(UserPersistenceService userPersistenceService){
        this.userPersistenceService = userPersistenceService;
    }

    /**
     * Retrieve User Resource by its unique Id.
     * @param id The User Resource Id to query.
     * @return A Http Response.
     */
    public ResponseEntity<?> get(String id) {
        log.info("Retrieving User Resource with id {}", id);

        // If Id in path is empty, return informative error.
        if(StringUtils.isBlank(id)){
            log.info(USERID_MISSING_ERROR);
            return new ResponseEntity<>(USERID_MISSING_ERROR, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        // Validate that User exists by provided Id.
        Optional<UserResourceResponse> userEntityOptional = userPersistenceService.getByUserId(id);

        if(userEntityOptional.isEmpty()){
            log.info(USER_NOT_FOUND_ERROR);
            return new ResponseEntity<>(USER_NOT_FOUND_ERROR, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        UserResourceResponse userResourceResponse = userEntityOptional.get();

        log.info(String.format("User with id %s found", id));

        return ResponseEntity.ok(userResourceResponse);
    }
}
