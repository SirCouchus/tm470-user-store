package ac.ou.tm470.user.logic;

import ac.ou.tm470.user.generated.model.Email;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.service.UserPersistenceService;
import ac.ou.tm470.user.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static ac.ou.tm470.user.utils.Errors.*;

@Service
public class UpdateUserResource {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserResource.class);

    private final UserPersistenceService userPersistenceService;
    private final CommonUtils commonUtils;

    @Autowired
    public UpdateUserResource(UserPersistenceService userPersistenceService, CommonUtils commonUtils) {
        this.userPersistenceService = userPersistenceService;
        this.commonUtils = commonUtils;
    }

    /**
     * Updates a User Resource by its unique identifier
     * @param id The unique identifier of the User Resource
     * @return The updated User Resource
     */
    public ResponseEntity<?> update(String id, UpdateUserResourceRequest updateUserResourceRequest) {

        // If the Id in the URI path is different to the Id provided in the Update Request Body, return mismatching error.
        if(!id.equalsIgnoreCase(updateUserResourceRequest.getId())) {
            log.info("Id in request does not match Id in path");
            return new ResponseEntity<>(USER_ID_MISMATCH_ERROR, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        // Return Http 409 if username is taken
        String username = updateUserResourceRequest.getUserName();
        if(commonUtils.usernameExists(username)){
            log.info("Username [{}] already exists", username);
            return new ResponseEntity<>(USER_ALREADY_EXISTS, new HttpHeaders(), HttpStatus.CONFLICT);
        }

        log.info("Username validated successfully.");

        // Return Http 409 if email is taken
        List<Email> emails = updateUserResourceRequest.getEmails();
        if(commonUtils.emailExists(emails)) {
            log.info("Email already exists");
            return new ResponseEntity<>(USER_ALREADY_EXISTS, new HttpHeaders(), HttpStatus.CONFLICT);
        }

        log.info("Email validated successfully.");

        // Validate User Resource to update exists
        Optional<UserResourceResponse> userToUpdateOptional = userPersistenceService.getByUserId(id);

        if(userToUpdateOptional.isEmpty()) {
            log.info("User with id {} could not be found", id);
            return new ResponseEntity<>(USER_NOT_FOUND_ERROR, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        UserResourceResponse userToUpdate = userToUpdateOptional.get();

        log.debug("User {} retrieved from persistence", userToUpdate);

        Optional<UserResourceResponse> updatedUser = userPersistenceService.updateUser(updateUserResourceRequest);

        // If the response from persistence is empty, the update request failed so return Internal Server Error.
        if(updatedUser.isEmpty()){
            log.info("Unexpected error when updating User Resource.");
            return new ResponseEntity<>(INTERNAL_PROCESSOR_ERROR, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.debug("User successfully updated {}", updatedUser);
        return new ResponseEntity<>(updatedUser, new HttpHeaders(), HttpStatus.OK);
    }
}
