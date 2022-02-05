package ac.ou.tm470.user.rest;

import ac.ou.tm470.user.service.UserPersistenceService;
import ac.ou.tm470.user.utils.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AbstractTest {

    protected static final String SERVER_BASE = "http://localhost:8080/api/Users";

    @MockBean
    UserPersistenceService userPersistenceService;

    @MockBean
    CommonUtils commonUtils;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    protected ObjectMapper mapper;

    protected MockMvc mockMvc;

    @BeforeEach
    public void configure() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .build();
    }

}
