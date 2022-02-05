package ac.ou.tm470.user.rest;

import ac.ou.tm470.user.generated.api.ApiApi;
import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@CrossOrigin(origins = "http://localhost:8082")
public class UserRestController implements ApiApi {

    private final UserService userService;

    @Autowired
    public UserRestController(UserService userService){
        this.userService = userService;
    }

    @Override
    public ResponseEntity<UserResourceResponse> getUserResource(@PathVariable String id) {
        return userService.getUserByUsername(id);
    }

    @Override
    public ResponseEntity<UserResourceResponse> createUserResource(@RequestBody CreateUserResourceRequest createUserResourceRequest) {
        return userService.createUser(createUserResourceRequest);
    }

    @Override
    public ResponseEntity<List<UserResourceResponse>> getUserResources() {
        return userService.getUsers();
    }

    @Override
    public ResponseEntity<UserResourceResponse> updateUserResource(@PathVariable String id,
                                                                   @RequestBody UpdateUserResourceRequest updateUserResourceRequest) {
        return userService.updateUser(id, updateUserResourceRequest);
    }

    @Override
    public ResponseEntity<Void> deleteUserResource(@PathVariable String id) {
        return userService.deleteUser(id);
    }

    @Override
    public ResponseEntity<Void> checkUserExists(@PathVariable String username) {
        return userService.checkUserExists(username);
    }
}
