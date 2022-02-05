package ac.ou.tm470.user.service;

import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    ResponseEntity<UserResourceResponse> createUser(CreateUserResourceRequest request);

    ResponseEntity<UserResourceResponse> getUserByUsername(String username);

    ResponseEntity<List<UserResourceResponse>> getUsers();

    ResponseEntity<Void> deleteUser(String id);

    ResponseEntity<Void> checkUserExists(String username);

    ResponseEntity<UserResourceResponse> updateUser(String id, UpdateUserResourceRequest updateUserResourceRequest);
}
