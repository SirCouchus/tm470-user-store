package ac.ou.tm470.user.rest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static ac.ou.tm470.user.utils.Errors.USER_NOT_FOUND_ERROR;
import static ac.ou.tm470.user.utils.TestConstants.VALID_USERNAME;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class CheckUserExistsTests extends AbstractTest {

    private static final String EXISTING_USER_PATH = SERVER_BASE + "/existing/{username}";

    @Test
    @WithMockUser
    @DisplayName("Check User Exists :: Username Does Not Exist :: Return Not Found")
    void checkUserExists_usernameDoesNotExist_returnNotFound() throws Exception {

        mockMvc.perform(get(EXISTING_USER_PATH, VALID_USERNAME))
                .andExpect(status().isNotFound())
                .andExpect(content().string(USER_NOT_FOUND_ERROR));
    }

    @Test
    @WithMockUser
    @DisplayName("Check User Exists :: Username Exists :: Return No Content")
    void checkUserExists_usernameExists_returnNoContent() throws Exception {

        when(userPersistenceService.getByUsername(VALID_USERNAME))
                .thenReturn(true);

        mockMvc.perform(get(EXISTING_USER_PATH, VALID_USERNAME))
                .andExpect(status().isNoContent());
    }
}
