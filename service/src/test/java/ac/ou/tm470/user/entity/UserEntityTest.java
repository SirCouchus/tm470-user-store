package ac.ou.tm470.user.entity;

import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.Email;
import ac.ou.tm470.user.persistence.UserEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import static ac.ou.tm470.user.utils.TestConstants.VALID_ID;
import static ac.ou.tm470.user.utils.TestConstants.VALID_USERNAME;
import static org.assertj.core.api.Assertions.assertThat;

public class UserEntityTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("Create User Entity :: With Arguments :: Save Successfully")
    void createUserEntity_withParams_saveSuccessfully() throws JsonProcessingException {
        UserEntity userEntity = new UserEntity(VALID_ID,
                VALID_USERNAME,
                mapper.writeValueAsBytes(List.of(new Email())),
                mapper.writeValueAsBytes(new CreateUserResourceRequest()));

        assertThat(userEntity.getUser()).isNotNull();
        assertThat(userEntity.getUserId()).isEqualTo(VALID_ID);
        assertThat(userEntity.getUsername()).isEqualTo(VALID_USERNAME);
    }

    @Test
    @DisplayName("Create User Entity :: Created From Json :: Save Successfully")
    void createUserEntity_fromJsonFile_saveSuccessfully() throws IOException, JSONException {
        UserEntity userEntity = new UserEntity(VALID_ID,
                VALID_USERNAME,
                mapper.writeValueAsBytes(List.of(new Email())),
                mapper.writeValueAsBytes(convertJsonToCreateRequest()));

        assertThat(userEntity.getUser()).isNotNull();
        assertThat(userEntity.getUserId()).isEqualTo(VALID_ID);
        assertThat(userEntity.getUsername()).isEqualTo(VALID_USERNAME);
        assertThat(userEntity.getUser()).isNotNull();

        validateUser(userEntity.getUser());
    }

    private CreateUserResourceRequest convertJsonToCreateRequest() throws IOException {
        URL jsonScimPath  = new File("src/test/java/ac/ou/tm470/user/utils/scim-user-resource.json").toURI().toURL();

        return mapper.readValue(new File(jsonScimPath.getPath()), CreateUserResourceRequest.class);
    }

    private void validateUser(byte[] user) throws JSONException {
        JSONObject userObject = new JSONObject(new String(user));

        assertThat(userObject.getString("userName")).isEqualTo("bjensen");

        JSONObject userNameObject = userObject.getJSONObject("name");

        assertThat(userNameObject.getString("formatted")).isEqualTo("Ms. Barbara J Jensen III");
        assertThat(userNameObject.getString("familyName")).isEqualTo("Jensen");
        assertThat(userNameObject.getString("givenName")).isEqualTo("Barbara");
    }
}
