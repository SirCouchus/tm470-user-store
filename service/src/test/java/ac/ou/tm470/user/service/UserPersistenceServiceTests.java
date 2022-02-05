package ac.ou.tm470.user.service;

import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.Email;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.persistence.UserEntity;
import ac.ou.tm470.user.persistence.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ac.ou.tm470.user.generated.model.Name;
import ac.ou.tm470.user.generated.model.PhoneNumber;
import ac.ou.tm470.user.generated.model.Role;
import ac.ou.tm470.user.generated.model.Group;
import ac.ou.tm470.user.generated.model.Entitlement;
import ac.ou.tm470.user.generated.model.Address;
import ac.ou.tm470.user.generated.model.X509Certificate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ac.ou.tm470.user.utils.TestConstants.VALID_ID;
import static ac.ou.tm470.user.utils.TestConstants.VALID_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserPersistenceServiceTests {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserPersistenceService userPersistenceService;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Check User Exists :: Username Does Not Exist :: Return False")
    void checkUserExists_usernameDoesNotExist_returnFalse() {
        assertThat(userPersistenceService.getByUsername(VALID_USERNAME)).isFalse();
    }

    @Test
    @DisplayName("Check User Exists :: Username Exists :: Return True")
    void checkUserExists_usernameExists_returnTrue() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(VALID_ID);
        userEntity.setUsername(VALID_USERNAME);
        userRepository.save(userEntity);

        assertThat(userPersistenceService.getByUsername(VALID_USERNAME)).isTrue();
    }

    @Test
    @DisplayName("Find By Username :: Username Does Not Exist :: Return Empty Optional")
    void findByUsername_nonExistentUser_returnOptionalEmpty() {
        assertThat(userPersistenceService.findByUsername(VALID_USERNAME)).isEmpty();
    }

    @Test
    @DisplayName("Find By Username :: Username Exists :: Return Optional User")
    void findByUsername_userExists_returnOptionalUser() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(VALID_ID);
        userEntity.setUsername(VALID_USERNAME);
        userRepository.save(userEntity);

        Optional<UserEntity> optionalUserEntity = userPersistenceService.findByUsername(VALID_USERNAME);
        assertThat(optionalUserEntity).isNotEmpty();

        userEntity = optionalUserEntity.get();
        assertThat(userEntity.getUserId()).isEqualTo(VALID_ID);
        assertThat(userEntity.getUsername()).isEqualTo(VALID_USERNAME);
        assertThat(userEntity.getId()).isNotNull();
    }

    @Test
    @DisplayName("Check Email Exists :: Email Does Not Exist :: Return Optional Empty")
    void checkEmailExists_emailDoesNotExist_returnOptionalEmpty() {
        assertThat(userPersistenceService.checkEmailExists(Collections.emptyList())).isEmpty();
    }

    @Test
    @DisplayName("Get User By User ID :: User Does Not Exist :: Return Optional Empty")
    void getByUserId_nonExistentUser_returnOptionalEmpty() {
        assertThat(userPersistenceService.getByUserId(VALID_ID)).isEmpty();
    }

    @Test
    @DisplayName("Get User By User ID :: User Exists :: Return Optional User")
    void getByUserId_userExists_returnOptionalUser() throws JsonProcessingException {

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(VALID_ID);
        userEntity.setUser(objectMapper.writeValueAsBytes(new CreateUserResourceRequest()));
        userRepository.save(userEntity);

        Optional<UserResourceResponse> optionalUserEntity = userPersistenceService.getByUserId(VALID_ID);
        assertThat(optionalUserEntity).isNotEmpty();

        UserResourceResponse  userResourceResponse = optionalUserEntity.get();
        assertThat(userResourceResponse.getId()).isEqualTo(VALID_ID);
    }

    @Test
    @DisplayName("Save User :: Valid Request :: Return Saved User")
    void saveUser_validRequest_returnSavedEntity() {
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(VALID_USERNAME);
        user.setEmails(List.of(new Email()));
        user.userType("Random");
        UserResourceResponse response = userPersistenceService.saveUser(user);

        assertThat(response.getUserName()).isEqualTo(user.getUserName());
        assertThat(response.getEmails()).isEqualTo(user.getEmails());
        assertThat(response.getUserType()).isEqualTo(user.getUserType());
        assertThat(response.getId()).isNotEmpty();
    }

    @Test
    @DisplayName("Update User :: From Full Scim Json :: Return Updated User")
    void updateUser_fromFullScimJson_returnUpdatedEntity() throws IOException {
        String userId = UUID.randomUUID().toString();
        String username = UUID.randomUUID().toString();
        byte[] emails = objectMapper.writeValueAsBytes(new Email());
        byte[] user = objectMapper.writeValueAsBytes(new CreateUserResourceRequest());

        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setUsername(username);
        userEntity.setEmails(emails);
        userEntity.setUser(user);

        userRepository.save(userEntity);

        // Create request from JSON file and get properties
        UpdateUserResourceRequest updateUserResourceRequest = this.convertJsonToUpdateRequest();
        updateUserResourceRequest.setId(userId);
        Name nameJsonObjectRequest = updateUserResourceRequest.getName();
        List<Email> emailJsonObjectRequest = updateUserResourceRequest.getEmails();
        Email emailHomePropertyRequest = updateUserResourceRequest.getEmails().get(0);
        Email emailWorkPropertyRequest = updateUserResourceRequest.getEmails().get(1);
        List<PhoneNumber> phoneJsonObjectRequest = updateUserResourceRequest.getPhoneNumbers();
        PhoneNumber phoneHomePropertyRequest = updateUserResourceRequest.getPhoneNumbers().get(0);
        PhoneNumber phoneWorkPropertyRequest = updateUserResourceRequest.getPhoneNumbers().get(1);
        Address addressJsonObjectRequest = updateUserResourceRequest.getAddresses().get(0);
        Group groupJsonObjectRequest = updateUserResourceRequest.getGroups().get(0);
        Entitlement entitlementsJsonObjectRequest = updateUserResourceRequest.getEntitlements().get(0);
        Role roleJsonObjectRequest = updateUserResourceRequest.getRoles().get(0);
        X509Certificate certificateJsonObjectRequest = updateUserResourceRequest.getX509Certificates().get(0);

        // Save the request and get properties from response
        Optional<UserResourceResponse> optionalUpdateResponse = userPersistenceService.updateUser(updateUserResourceRequest);
        assertThat(optionalUpdateResponse).isNotEmpty();

        UserResourceResponse response = optionalUpdateResponse.get();
        Name nameJsonObjectResponse = response.getName();
        List<Email> emailListJsonObjectResponse = response.getEmails();
        Email emailHomePropertyResponse = response.getEmails().get(0);
        Email emailWorkPropertyResponse = response.getEmails().get(1);
        List<PhoneNumber> phoneJsonObjectResponse = response.getPhoneNumbers();
        PhoneNumber phoneHomePropertyResponse = response.getPhoneNumbers().get(0);
        PhoneNumber phoneWorkPropertyResponse = response.getPhoneNumbers().get(1);
        Address addressJsonObjectResponse = response.getAddresses().get(0);
        Group groupJsonObjectResponse = response.getGroups().get(0);
        Entitlement entitlementsJsonObjectResponse = response.getEntitlements().get(0);
        Role roleJsonObjectResponse = response.getRoles().get(0);
        X509Certificate certificateJsonObjectResponse = response.getX509Certificates().get(0);

        // Top level property validation
        validateTopLevelPropertiesInUpdateRequest(updateUserResourceRequest, response);

        // Second level property validation
        validateSecondLevelPropertiesInUpdateRequest(updateUserResourceRequest, response);

        // Validate name
        validateNameProperty(nameJsonObjectRequest, nameJsonObjectResponse);

        // Validate emails
        assertThat(emailJsonObjectRequest.size()).isEqualTo(emailListJsonObjectResponse.size());
        validateEmailProperty(emailHomePropertyRequest, emailHomePropertyResponse);
        validateEmailProperty(emailWorkPropertyRequest, emailWorkPropertyResponse);

        // Validate phone
        assertThat(phoneJsonObjectRequest.size()).isEqualTo(phoneJsonObjectResponse.size());
        validatePhoneProperty(phoneHomePropertyRequest, phoneHomePropertyResponse);
        validatePhoneProperty(phoneWorkPropertyRequest, phoneWorkPropertyResponse);

        // Validate address
        validateAddressProperty(addressJsonObjectRequest, addressJsonObjectResponse);

        // Validate group
        validateGroupProperty(groupJsonObjectRequest, groupJsonObjectResponse);

        // Validate role
        validateRoleProperty(roleJsonObjectRequest, roleJsonObjectResponse);

        // Validate entitlement
        validateEntitlementProperty(entitlementsJsonObjectRequest, entitlementsJsonObjectResponse);

        // Validate certificate
        validateCertificateProperty(certificateJsonObjectRequest, certificateJsonObjectResponse);
    }

    @Test
    @DisplayName("Save User :: From Full Scim Json :: Return Saved User")
    void saveUser_fromFullScimJson_returnSavedEntity() throws IOException {

        // Create request from JSON file and get properties
        CreateUserResourceRequest user = this.convertJsonToCreateRequest();
        Name nameJsonObjectRequest = user.getName();
        List<Email> emailJsonObjectRequest = user.getEmails();
        Email emailHomePropertyRequest = user.getEmails().get(0);
        Email emailWorkPropertyRequest = user.getEmails().get(1);
        List<PhoneNumber> phoneJsonObjectRequest = user.getPhoneNumbers();
        PhoneNumber phoneHomePropertyRequest = user.getPhoneNumbers().get(0);
        PhoneNumber phoneWorkPropertyRequest = user.getPhoneNumbers().get(1);
        Address addressJsonObjectRequest = user.getAddresses().get(0);
        Group groupJsonObjectRequest = user.getGroups().get(0);
        Entitlement entitlementsJsonObjectRequest = user.getEntitlements().get(0);
        Role roleJsonObjectRequest = user.getRoles().get(0);
        X509Certificate certificateJsonObjectRequest = user.getX509Certificates().get(0);

        // Save the request and get properties from response
        UserResourceResponse response = userPersistenceService.saveUser(user);
        Name nameJsonObjectResponse = response.getName();
        List<Email> emailListJsonObjectResponse = response.getEmails();
        Email emailHomePropertyResponse = response.getEmails().get(0);
        Email emailWorkPropertyResponse = response.getEmails().get(1);
        List<PhoneNumber> phoneJsonObjectResponse = response.getPhoneNumbers();
        PhoneNumber phoneHomePropertyResponse = response.getPhoneNumbers().get(0);
        PhoneNumber phoneWorkPropertyResponse = response.getPhoneNumbers().get(1);
        Address addressJsonObjectResponse = response.getAddresses().get(0);
        Group groupJsonObjectResponse = response.getGroups().get(0);
        Entitlement entitlementsJsonObjectResponse = response.getEntitlements().get(0);
        Role roleJsonObjectResponse = response.getRoles().get(0);
        X509Certificate certificateJsonObjectResponse = response.getX509Certificates().get(0);

        // Top level property validation
        validateTopLevelPropertiesInCreateRequest(user, response);

        // Second level property validation
        validateSecondLevelPropertiesInCreateRequest(user, response);

        // Validate name
        validateNameProperty(nameJsonObjectRequest, nameJsonObjectResponse);

        // Validate emails
        assertThat(emailJsonObjectRequest.size()).isEqualTo(emailListJsonObjectResponse.size());
        validateEmailProperty(emailHomePropertyRequest, emailHomePropertyResponse);
        validateEmailProperty(emailWorkPropertyRequest, emailWorkPropertyResponse);

        // Validate phone
        assertThat(phoneJsonObjectRequest.size()).isEqualTo(phoneJsonObjectResponse.size());
        validatePhoneProperty(phoneHomePropertyRequest, phoneHomePropertyResponse);
        validatePhoneProperty(phoneWorkPropertyRequest, phoneWorkPropertyResponse);

        // Validate address
        validateAddressProperty(addressJsonObjectRequest, addressJsonObjectResponse);

        // Validate group
        validateGroupProperty(groupJsonObjectRequest, groupJsonObjectResponse);

        // Validate role
        validateRoleProperty(roleJsonObjectRequest, roleJsonObjectResponse);

        // Validate entitlement
        validateEntitlementProperty(entitlementsJsonObjectRequest, entitlementsJsonObjectResponse);

        // Validate certificate
        validateCertificateProperty(certificateJsonObjectRequest, certificateJsonObjectResponse);
    }

    @Test
    @DisplayName("Get All Users :: Multiple Existing Users :: Return List of Users")
    void getAll_multipleUsers_returnListOfEntities() throws IOException {
        UserEntity userEntity1 = new UserEntity();
        userEntity1.setUserId(VALID_ID);
        userEntity1.setUsername(VALID_USERNAME);
        userEntity1.setUser(objectMapper.writeValueAsBytes(userEntity1));
        userRepository.save(userEntity1);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setUserId(VALID_ID + "1");
        userEntity2.setUsername(VALID_USERNAME + "1");
        userEntity2.setUser(objectMapper.writeValueAsBytes(userEntity2));
        userRepository.save(userEntity2);

        List<UserResourceResponse> listOfUsers = userPersistenceService.getAll();

        assertThat(listOfUsers).hasSize(2);

        UserResourceResponse firstUserInList = listOfUsers.get(0);
        UserResourceResponse secondUserInList = listOfUsers.get(1);

        assertThat(firstUserInList.getId()).isEqualTo(userEntity1.getUserId());
        assertThat(firstUserInList.getUserName()).isEqualTo(userEntity1.getUsername());

        assertThat(secondUserInList.getId()).isEqualTo(userEntity2.getUserId());
        assertThat(secondUserInList.getUserName()).isEqualTo(userEntity2.getUsername());
    }

    @Test
    @DisplayName("Get All Users :: No Users Exist :: Return Empty List")
    void getAll_noUsers_returnEmptyList() {
        List<UserResourceResponse> listOfUsers = userPersistenceService.getAll();

        assertThat(listOfUsers).isEmpty();
    }

    @Test
    @DisplayName("Update User :: Valid Request :: Return Updated User")
    void updateUser_validRequest_returnUpdatedUser() {
        UserEntity originalUserEntity = new UserEntity();
        originalUserEntity.setUserId(VALID_ID);
        originalUserEntity.setUsername(VALID_USERNAME);
        userRepository.save(originalUserEntity);

        UpdateUserResourceRequest updatedUserEntity = new UpdateUserResourceRequest();
        updatedUserEntity.setId(VALID_ID);
        updatedUserEntity.setUserName(VALID_USERNAME + "1");

        Optional<UserResourceResponse> optionalReturnedUserEntity = userPersistenceService.updateUser(updatedUserEntity);

        assertThat(optionalReturnedUserEntity).isNotEmpty();

        UserResourceResponse returnedUserEntity = optionalReturnedUserEntity.get();
        assertThat(returnedUserEntity.getId()).isEqualTo(VALID_ID);
        assertThat(returnedUserEntity.getUserName()).isEqualTo(updatedUserEntity.getUserName());
    }

    @Test
    @DisplayName("Delete User By Id :: Valid Request :: User Deleted")
    void deleteByUserId_validRequest_userDeleted() throws JsonProcessingException {
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(VALID_ID);
        userEntity.setUsername(VALID_USERNAME);
        userEntity.setUser(objectMapper.writeValueAsBytes(new CreateUserResourceRequest()));
        userRepository.save(userEntity);

        assertThat(userPersistenceService.getByUserId(userEntity.getUserId())).isNotEmpty();

        userPersistenceService.deleteByUserId(userEntity.getUserId());

        assertThat(userPersistenceService.getByUserId(userEntity.getUserId())).isEmpty();
    }

    /**
     * Validate x509Certificate SCIM property in POST request to value returned in UserResource response
     * @param certificateJsonObjectRequest x509Certificate in POST request
     * @param certificateJsonObjectResponse x509Certificate in UserResource response
     */
    private void validateCertificateProperty(X509Certificate certificateJsonObjectRequest, X509Certificate certificateJsonObjectResponse) {
        assertThat(certificateJsonObjectRequest)
                .usingRecursiveComparison()
                .isEqualTo(certificateJsonObjectResponse);
    }

    /**
     * Validate Entitlement SCIM property in POST request to value returned in UserResource response
     * @param entitlementJsonObjectRequest Entitlement in POST request
     * @param entitlementJsonObjectResponse Entitlement in UserResource response
     */
    private void validateEntitlementProperty(Entitlement entitlementJsonObjectRequest, Entitlement entitlementJsonObjectResponse) {
        assertThat(entitlementJsonObjectRequest)
                .usingRecursiveComparison()
                .isEqualTo(entitlementJsonObjectResponse);
    }

    /**
     * Validate Role SCIM property in POST request to value returned in UserResource response
     * @param roleJsonObjectRequest Role in POST request
     * @param roleJsonObjectResponse Role in UserResource response
     */
    private void validateRoleProperty(Role roleJsonObjectRequest, Role roleJsonObjectResponse) {
        assertThat(roleJsonObjectRequest)
                .usingRecursiveComparison()
                .isEqualTo(roleJsonObjectResponse);
    }

    /**
     * Validate Group SCIM property in POST request to value returned in UserResource response
     * @param groupJsonObjectRequest Group in request
     * @param groupJsonObjectResponse Group in UserResource response
     */
    private void validateGroupProperty(Group groupJsonObjectRequest, Group groupJsonObjectResponse) {
        assertThat(groupJsonObjectRequest)
                .usingRecursiveComparison()
                .isEqualTo(groupJsonObjectResponse);
    }

    /**
     * Validate Address SCIM property in request to value returned in UserResource response
     * @param addressJsonObjectRequest Address in request
     * @param addressJsonObjectResponse Address in UserResource response
     */
    private void validateAddressProperty(Address addressJsonObjectRequest, Address addressJsonObjectResponse) {
        assertThat(addressJsonObjectRequest)
                .usingRecursiveComparison()
                .isEqualTo(addressJsonObjectResponse);
    }

    /**
     * Validate PhoneNumber SCIM property in request to value returned in UserResource response
     * @param phonePropertyRequest PhoneNumber in request
     * @param phonePropertyResponse PhoneNumber in UserResource response
     */
    private void validatePhoneProperty(PhoneNumber phonePropertyRequest, PhoneNumber phonePropertyResponse) {
        assertThat(phonePropertyRequest)
                .usingRecursiveComparison()
                .isEqualTo(phonePropertyResponse);
    }

    /**
     * Validate EmailAddress SCIM property in request with value in UserResource response
     * @param emailPropertyRequest EmailAddress in request
     * @param emailPropertyResponse PhoneNumber in UserResource response
     */
    private void validateEmailProperty(Email emailPropertyRequest, Email emailPropertyResponse) {
        assertThat(emailPropertyRequest)
                .usingRecursiveComparison()
                .isEqualTo(emailPropertyResponse);
    }

    /**
     * Validate Name SCIM property in request with value in UserResource response
     * @param nameJsonObjectRequest Name in request
     * @param nameJsonObjectResponse Name in UserResource response
     */
    private void validateNameProperty(Name nameJsonObjectRequest, Name nameJsonObjectResponse) {
        assertThat(nameJsonObjectRequest)
                .usingRecursiveComparison()
                .isEqualTo(nameJsonObjectResponse);
    }

    /**
     * Validate the top-level properties in the CreateUserResourceRequest with the values returned in the UserResource response
     * @param user The CreateUserResourceRequest
     * @param response The UserResource response
     */
    private void validateTopLevelPropertiesInCreateRequest(CreateUserResourceRequest user, UserResourceResponse response) {
        assertThat(response.getUserName()).isEqualTo(user.getUserName());
        assertThat(response.getEmails()).isEqualTo(user.getEmails());
        assertThat(response.getUserType()).isEqualTo(user.getUserType());
        assertThat(response.getId()).isNotEmpty();
    }

    /**
     * Validate the top-level properties in the UpdateUserResourceRequest with the values returned in the UserResource response
     * @param user The UpdateUserResourceRequest
     * @param response The UserResource response
     */
    private void validateTopLevelPropertiesInUpdateRequest(UpdateUserResourceRequest user, UserResourceResponse response) {
        assertThat(response.getUserName()).isEqualTo(user.getUserName());
        assertThat(response.getEmails()).isEqualTo(user.getEmails());
        assertThat(response.getUserType()).isEqualTo(user.getUserType());
        assertThat(response.getId()).isNotEmpty();
    }

    /**
     * Validate second-level properties in the CreateUserResourceRequest with the values returned in the UserResource response
     * @param user The CreateUserResourceRequest
     * @param response The UserResource response
     */
    private void validateSecondLevelPropertiesInCreateRequest(CreateUserResourceRequest user, UserResourceResponse response) {
        assertThat(user.getDisplayName()).isEqualTo(response.getDisplayName());
        assertThat(user.getNickName()).isEqualTo(response.getNickName());
        assertThat(user.getProfileUrl()).isEqualTo(response.getProfileUrl());
        assertThat(user.getTitle()).isEqualTo(response.getTitle());
        assertThat(user.getPreferredLanguage()).isEqualTo(response.getPreferredLanguage());
        assertThat(user.getLocale()).isEqualTo(response.getLocale());
        assertThat(user.getTimezone()).isEqualTo(response.getTimezone());
        assertThat(user.getActive()).isEqualTo(response.getActive());
        assertThat(user.getPassword()).isEqualTo(response.getPassword());
    }

    /**
     * Validate the second-level properties in the UpdateUserResourceRequest with the values returned in the UserResource response
     * @param user The UpdateUserResourceRequest
     * @param response The UserResource response
     */
    private void validateSecondLevelPropertiesInUpdateRequest(UpdateUserResourceRequest user, UserResourceResponse response) {
        assertThat(user.getDisplayName()).isEqualTo(response.getDisplayName());
        assertThat(user.getNickName()).isEqualTo(response.getNickName());
        assertThat(user.getProfileUrl()).isEqualTo(response.getProfileUrl());
        assertThat(user.getTitle()).isEqualTo(response.getTitle());
        assertThat(user.getPreferredLanguage()).isEqualTo(response.getPreferredLanguage());
        assertThat(user.getLocale()).isEqualTo(response.getLocale());
        assertThat(user.getTimezone()).isEqualTo(response.getTimezone());
        assertThat(user.getActive()).isEqualTo(response.getActive());
        assertThat(user.getPassword()).isEqualTo(response.getPassword());
    }

    /**
     * Convert a SCIM JSON file to a CreateUserResourceRequest
     * @return The CreateUserResourceRequest
     * @throws IOException Mapper error during conversion
     */
    private CreateUserResourceRequest convertJsonToCreateRequest() throws IOException {
        URL jsonScimPath  = new File("src/test/java/ac/ou/tm470/user/utils/scim-user-resource.json").toURI().toURL();

        return objectMapper.readValue(new File(jsonScimPath.getPath()), CreateUserResourceRequest.class);
    }

    /**
     * Convert a SCIM JSON file to an UpdateUserResourceRequest
     * @return The UpdateUserResourceRequest
     * @throws IOException Mapper error during conversion
     */
    private UpdateUserResourceRequest convertJsonToUpdateRequest() throws IOException {
        URL jsonScimPath  = new File("src/test/java/ac/ou/tm470/user/utils/scim-user-resource.json").toURI().toURL();

        return objectMapper.readValue(new File(jsonScimPath.getPath()), UpdateUserResourceRequest.class);
    }
}
