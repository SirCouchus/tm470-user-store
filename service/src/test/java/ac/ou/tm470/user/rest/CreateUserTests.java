package ac.ou.tm470.user.rest;

import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.Email;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.List;

import static ac.ou.tm470.user.utils.TestConstants.VALID_USERNAME;
import static ac.ou.tm470.user.utils.Constants.USERNAME_MAX_LENGTH;
import static ac.ou.tm470.user.utils.Constants.USERNAME_MIN_LENGTH;
import static ac.ou.tm470.user.utils.Errors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

public class CreateUserTests extends AbstractTest {

    @Test
    @WithMockUser
    @DisplayName("Create User :: Username Empty :: Bad Request")
    void createUser_usernameEmpty_returnBadRequest() throws Exception {
        mockMvc.perform(post(SERVER_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(new CreateUserResourceRequest()))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Create User :: Username Too Short :: Bad Request")
    void createUser_usernameTooShort_returnBadRequest() throws Exception {
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(StringUtils.repeat("*", USERNAME_MIN_LENGTH-1));
        user.setEmails(List.of(new Email()));

        mockMvc.perform(post(SERVER_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Create User :: Username Too Long :: Bad Request")
    void createUser_usernameTooLong_returnBadRequest() throws Exception {
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(StringUtils.repeat("*", USERNAME_MAX_LENGTH+1));
        user.setEmails(List.of(new Email()));

        mockMvc.perform(post(SERVER_BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Create User :: Emails Empty :: Bad Request")
    void createUser_emailsEmpty_returnBadRequest() throws Exception {
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(VALID_USERNAME);

        mockMvc.perform(post(SERVER_BASE)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(EMAIL_RESOLUTION_ERROR));
    }

    @Test
    @WithMockUser
    @DisplayName("Create User :: Duplicate Username :: Conflict")
    void createUser_usernameTaken_returnConflict() throws Exception {
        List<Email> emails = Collections.singletonList(new Email());
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(VALID_USERNAME);
        user.setEmails(emails);

        when(commonUtils.usernameExists(VALID_USERNAME))
                .thenReturn(true);

        mockMvc.perform(post(SERVER_BASE)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isConflict())
                .andExpect(content().string(USER_ALREADY_EXISTS));
    }

    @Test
    @WithMockUser
    @DisplayName("Create User :: Email Taken :: Conflict")
    void createUser_emailTaken_returnConflict() throws Exception {
        List<Email> emails = List.of(new Email());
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(VALID_USERNAME);
        user.setEmails(emails);

        when(commonUtils.emailExists(eq(emails))).thenReturn(true);

        mockMvc.perform(post(SERVER_BASE)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isConflict())
                .andExpect(content().string(USER_ALREADY_EXISTS));
    }

    @Test
    @WithMockUser
    @DisplayName("Create User :: Internal Error :: Internal Server Error")
    void createUser_errorSavingUser_returnInternalServerError() throws Exception {
        List<Email> emails = List.of(new Email());
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(VALID_USERNAME);
        user.setEmails(emails);

        when(commonUtils.usernameExists(VALID_USERNAME)).thenReturn(false);
        when(commonUtils.emailExists(eq(emails))).thenReturn(false);
        when(userPersistenceService.saveUser(eq(user))).thenReturn(null);

        mockMvc.perform(post(SERVER_BASE)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(INTERNAL_PROCESSOR_ERROR));
    }

    @Test
    @WithMockUser
    @DisplayName("Create User :: Valid Request :: Return Created User")
    void createUser_validRequest_returnNewUser() throws Exception {
        List<Email> emails = List.of(new Email());
        CreateUserResourceRequest user = new CreateUserResourceRequest();
        user.setUserName(VALID_USERNAME);
        user.setEmails(emails);

        UserResourceResponse userEntity = new UserResourceResponse();

        when(commonUtils.usernameExists(VALID_USERNAME)).thenReturn(false);
        when(commonUtils.emailExists(eq(emails))).thenReturn(false);
        when(userPersistenceService.saveUser(eq(user))).thenReturn(userEntity);

        mockMvc.perform(post(SERVER_BASE)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsBytes(user)))
                .andExpect(status().isOk());
    }
}
