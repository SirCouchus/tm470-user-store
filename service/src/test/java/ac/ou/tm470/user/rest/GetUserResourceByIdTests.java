package ac.ou.tm470.user.rest;

import ac.ou.tm470.user.generated.model.UserResourceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static ac.ou.tm470.user.utils.TestConstants.VALID_ID;
import static ac.ou.tm470.user.utils.Errors.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetUserResourceByIdTests extends AbstractTest{

    private static final String ENDPOINT = SERVER_BASE + "/{id}";

    @Test
    @WithMockUser
    @DisplayName("Get User :: Blank Username :: Return Bad Request")
    void getUser_missingUsername_return400() throws Exception {
        mockMvc.perform(get(ENDPOINT, "  ")
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Get User :: User Not Found :: Return Not Found")
    void getUser_userDoesNotExist_return404() throws Exception {

        when(userPersistenceService.getByUserId(eq(VALID_ID))).thenReturn(Optional.empty());

        mockMvc.perform(get(ENDPOINT, VALID_ID))
                .andExpect(status().isNotFound())
                .andExpect(content().string(USER_NOT_FOUND_ERROR));
    }

    @Test
    @WithMockUser
    @DisplayName("Get User :: Valid Request :: Return OK")
    void getUser_userExists_return200() throws Exception {
        UserResourceResponse userResource = new UserResourceResponse();
        userResource.setId(VALID_ID);

        when(userPersistenceService.getByUserId(eq(VALID_ID))).thenReturn(Optional.of(userResource));

        mockMvc.perform(get(ENDPOINT, VALID_ID))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(userResource)));
    }
}
