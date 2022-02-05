package ac.ou.tm470.user.rest;

import ac.ou.tm470.user.generated.model.UserResourceResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GetUserResourcesTests extends AbstractTest{

    @Test
    @WithMockUser
    @DisplayName("Get Users :: No Users Found :: Return OK Empty List")
    void getUsers_noUsersFound_return200() throws Exception {
        when(userPersistenceService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get(SERVER_BASE)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(Collections.emptyList())));
    }

    @Test
    @WithMockUser
    @DisplayName("Get Users :: One User Found :: Return OK List Size 1")
    void getUsers_oneUserFound_return200() throws Exception {
        UserResourceResponse userEntity = new UserResourceResponse();

        List<UserResourceResponse> listOfUsers = Collections.singletonList(userEntity);

        when(userPersistenceService.getAll()).thenReturn(listOfUsers);

        mockMvc.perform(get(SERVER_BASE)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(listOfUsers)));
    }

    @Test
    @WithMockUser
    @DisplayName("Get Users :: Two Users Found :: Return OK List Size 2")
    void getUsers_twoUsersFound_return200() throws Exception {
        UserResourceResponse userEntity1 = new UserResourceResponse();
        UserResourceResponse userEntity2 = new UserResourceResponse();

        List<UserResourceResponse> listOfUsers = Arrays.asList(userEntity1, userEntity2);

        when(userPersistenceService.getAll()).thenReturn(listOfUsers);

        mockMvc.perform(get(SERVER_BASE)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(mapper.writeValueAsString(listOfUsers)));
    }

}
