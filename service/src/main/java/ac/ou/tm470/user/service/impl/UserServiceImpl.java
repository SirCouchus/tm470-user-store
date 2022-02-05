package ac.ou.tm470.user.service.impl;

import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.logic.*;
import ac.ou.tm470.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final CreateUserResource createUserResource;
    private final GetUserResourceById getUserResourceById;
    private final GetUserResources getUserResources;
    private final DeleteUserResource deleteUserResource;
    private final UpdateUserResource updateUserResource;
    private final CheckUserExists checkUserExists;

    @Autowired
    public UserServiceImpl(CreateUserResource createUserResource,
                           GetUserResourceById getUserResourceById,
                           GetUserResources getUserResources,
                           DeleteUserResource deleteUserResource,
                           UpdateUserResource updateUserResource,
                           CheckUserExists checkUserExists){
        this.createUserResource = createUserResource;
        this.getUserResourceById = getUserResourceById;
        this.getUserResources = getUserResources;
        this.deleteUserResource = deleteUserResource;
        this.updateUserResource = updateUserResource;
        this.checkUserExists = checkUserExists;
    }

    @Override
    public ResponseEntity<UserResourceResponse> createUser(CreateUserResourceRequest request) {
        return (ResponseEntity<UserResourceResponse>) createUserResource.create(request);
    }

    @Override
    public ResponseEntity<UserResourceResponse> getUserByUsername(String username){
        return (ResponseEntity<UserResourceResponse>) getUserResourceById.get(username);
    }

    @Override
    public ResponseEntity<List<UserResourceResponse>> getUsers() {
        return (ResponseEntity<List<UserResourceResponse>>) getUserResources.getAll();
    }

    @Override
    public ResponseEntity<Void> deleteUser(String id) {
        return (ResponseEntity<Void>) deleteUserResource.delete(id);
    }

    @Override
    public ResponseEntity<Void> checkUserExists(String username) {
        return (ResponseEntity<Void>) checkUserExists.check(username);
    }

    @Override
    public ResponseEntity<UserResourceResponse> updateUser(String id, UpdateUserResourceRequest updateUserResourceRequest) {
        return (ResponseEntity<UserResourceResponse>) updateUserResource.update(id, updateUserResourceRequest);
    }
}
