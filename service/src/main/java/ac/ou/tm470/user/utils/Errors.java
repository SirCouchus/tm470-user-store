package ac.ou.tm470.user.utils;

public class Errors {

    public static final String USERNAME_RESOLUTION_ERROR = "Username could not be resolved";
    public static final String EMAIL_RESOLUTION_ERROR = "Email could not be resolved";

    public static final String USERID_MISSING_ERROR = "User ID missing";

    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String USER_NOT_FOUND_ERROR = "User not found";
    public static final String USER_ID_MISMATCH_ERROR = "Mismatching User Ids provided";

    public static final String INTERNAL_PROCESSOR_ERROR = "Internal error when processing request";

    // Hide public ctor
    private Errors() {
        throw new IllegalStateException();
    }
}
