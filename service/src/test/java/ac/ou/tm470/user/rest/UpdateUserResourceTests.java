package ac.ou.tm470.user.rest;

import ac.ou.tm470.user.generated.model.Meta;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.generated.model.Email;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ac.ou.tm470.user.utils.TestConstants.VALID_ID;
import static ac.ou.tm470.user.utils.TestConstants.VALID_USERNAME;
import static ac.ou.tm470.user.utils.Constants.*;
import static ac.ou.tm470.user.utils.Errors.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UpdateUserResourceTests extends AbstractTest{

    private static final String ENDPOINT = SERVER_BASE + "/{id}";

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Invalid Body :: Bad Request")
    void updateUserResource_invalidBody_badRequest() throws Exception {
        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Missing UserName :: Bad Request")
    void updateUserResource_missingUserName_badRequest() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Missing Id :: Bad Request")
    void updateUserResource_missingId_badRequest() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Missing Schema :: Bad Request")
    void updateUserResource_missingSchema_badRequest() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(VALID_ID);
        updateRequest.setMeta(new Meta());

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Missing Meta :: Bad Request")
    void updateUserResource_missingMeta_badRequest() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: UserName Too Short :: Bad Request")
    void updateUserResource_userNameTooShort_badRequest() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(StringUtils.repeat("*", USERNAME_MIN_LENGTH-1));
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: UserName Too Long :: Bad Request")
    void updateUserResource_userNameTooLong_badRequest() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(StringUtils.repeat("*", USERNAME_MAX_LENGTH+1));
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Id Does Not Exist :: Not Found")
    void updateUserResource_idDoesNotExist_notFound() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(USER_NOT_FOUND_ERROR));
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Mismatch Id :: Bad Request")
    void updateUserResource_mismatchId_badRequest() throws Exception {

        String mismatchingId = UUID.randomUUID().toString();
        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(mismatchingId);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(USER_ID_MISMATCH_ERROR));
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Duplicate UserName :: Conflict")
    void updateUserResource_userNameIsTaken_conflict() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        when(commonUtils.usernameExists(VALID_USERNAME))
                .thenReturn(true);

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string(USER_ALREADY_EXISTS));
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Duplicate Email :: Conflict")
    void updateUserResource_emailIsTaken_conflict() throws Exception {

        List<Email> emails = new ArrayList<>();

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setEmails(emails);
        updateRequest.setMeta(new Meta());

        when(commonUtils.emailExists(emails))
                .thenReturn(true);

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isConflict())
                .andExpect(content().string(USER_ALREADY_EXISTS));
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Mapping Error :: Internal Server Error")
    void updateUserResource_mapperError_internalServerError() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        when(userPersistenceService.getByUserId(VALID_ID))
                .thenReturn(Optional.of(new UserResourceResponse()));

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser
    @DisplayName("Update User Resource :: Valid Request :: OK")
    void updateUserResource_validRequest_updateUser() throws Exception {

        UpdateUserResourceRequest updateRequest = new UpdateUserResourceRequest();
        updateRequest.setUserName(VALID_USERNAME);
        updateRequest.setId(VALID_ID);
        updateRequest.setSchemas(SCIM_USER_SCHEMA);
        updateRequest.setMeta(new Meta());

        UserResourceResponse userResourceResponse = new UserResourceResponse();
        userResourceResponse.setId(VALID_ID);
        userResourceResponse.setUserName(VALID_USERNAME);
        userResourceResponse.setSchemas(SCIM_USER_SCHEMA);
        userResourceResponse.setMeta(new Meta());

        when(userPersistenceService.getByUserId(VALID_ID))
                .thenReturn(Optional.of(new UserResourceResponse()));
        when(userPersistenceService.updateUser(updateRequest))
                .thenReturn(Optional.of(userResourceResponse));

        mockMvc.perform(put(ENDPOINT, VALID_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userResourceResponse)));
    }
}
