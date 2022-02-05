package ac.ou.tm470.user.logic;

import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.Email;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.service.UserPersistenceService;
import ac.ou.tm470.user.utils.CommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

import static ac.ou.tm470.user.utils.Errors.*;

@SuppressWarnings("ALL")
@Service
public class CreateUserResource {

    private static final Logger log = LoggerFactory.getLogger(CreateUserResource.class);

    private final UserPersistenceService userPersistenceService;
    private final CommonUtils commonUtils;

    @Autowired
    public CreateUserResource(UserPersistenceService userPersistenceService, CommonUtils commonUtils){
        this.userPersistenceService = userPersistenceService;
        this.commonUtils = commonUtils;
    }

    /**
     * Create a SCIM resource representation of a new user
     * @param request The CreateUserResourceRequest
     * @return A Http Response
     */
    public ResponseEntity<?> create(CreateUserResourceRequest request){
        String username = request.getUserName();

        // If the emails in the request are empty, return an error
        List<Email> emails = request.getEmails();
        if(CollectionUtils.isEmpty(emails)){
            log.info(EMAIL_RESOLUTION_ERROR);
            return new ResponseEntity<>(EMAIL_RESOLUTION_ERROR, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        // Return Http 409 if username is taken
        if(commonUtils.usernameExists(username)){
            log.info("Username [{}] already exists", username);
            return new ResponseEntity<>(USER_ALREADY_EXISTS, new HttpHeaders(), HttpStatus.CONFLICT);
        }

        log.info("Username validated successfully.");

        // Return Http 409 if email is taken
        if(commonUtils.emailExists(emails)) {
            log.info("Email already exists");
            return new ResponseEntity<>(USER_ALREADY_EXISTS, new HttpHeaders(), HttpStatus.CONFLICT);
        }

        log.info("Emails validated successfully.");

        // Save the user
        UserResourceResponse userResourceResponse = userPersistenceService.saveUser(request);

        log.info("User persisted successfully.");

        // If the Entity that is returned is null, there was a persistence error so return error
        if(userResourceResponse == null){
            log.warn("Null response from persistence service.");
            return new ResponseEntity<>(INTERNAL_PROCESSOR_ERROR, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Creation successful; return newly created Entity
        log.info(String.format("User created with Id [%s]", userResourceResponse.getId()));
        return new ResponseEntity<>(userResourceResponse, HttpStatus.OK);
    }
}
