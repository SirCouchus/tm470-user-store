package ac.ou.tm470.user.utils;

public class Constants {

    public static final String SCIM_USER_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User";

    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 36;

    // Hide public ctor
    private Constants() {
        throw new IllegalStateException();
    }


}
