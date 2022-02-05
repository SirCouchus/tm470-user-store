package ac.ou.tm470.user.utils;

import ac.ou.tm470.user.service.UserPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ac.ou.tm470.user.generated.model.Email;

import java.util.List;

/**
 * Common Utility class that can be used for common operations in the business logic.
 */
@SuppressWarnings("unused")
@Service
public class CommonUtils {

    private final UserPersistenceService userPersistenceService;

    @Autowired
    public CommonUtils(UserPersistenceService userPersistenceService) {
        this.userPersistenceService = userPersistenceService;
    }

    /**
     * Check whether email is taken
     * @param emails The list of emails to check
     * @return True if email exists
     */
    public boolean emailExists(List<Email> emails){
        return userPersistenceService.checkEmailExists(emails).isPresent();
    }

    /**
     * Check whether the username is taken
     * @param username The username to check
     * @return True if username exists
     */
    public boolean usernameExists(String username){
        return userPersistenceService.findByUsername(username).isPresent();
    }

}
