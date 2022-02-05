package ac.ou.tm470.user.rest;

import ac.ou.tm470.user.generated.model.UserResourceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static ac.ou.tm470.user.utils.TestConstants.VALID_ID;
import static ac.ou.tm470.user.utils.Errors.USER_NOT_FOUND_ERROR;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DeleteUserResourceTests extends AbstractTest {

    private static final String ENDPOINT = SERVER_BASE + "/{id}";

    @Test
    @WithMockUser
    @DisplayName("Delete User :: User Does Not Exist :: Not Found")
    void deleteUser_userDoesNotExist_returnNotFound() throws Exception {
        mockMvc.perform(delete(ENDPOINT, VALID_ID)
                .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().string(USER_NOT_FOUND_ERROR));
    }

    @Test
    @WithMockUser
    @DisplayName("Delete User :: Deleted Successfully :: No Content")
    void deleteUser_deletedSuccessfully_returnNoContent() throws Exception {

        when(userPersistenceService.getByUserId(VALID_ID))
                .thenReturn(Optional.of(new UserResourceResponse()));

        mockMvc.perform(delete(ENDPOINT, VALID_ID)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
