package ac.ou.tm470.user.service;

import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.Email;
import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.persistence.UserEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public interface UserPersistenceService {

    UserResourceResponse saveUser(CreateUserResourceRequest user);
    Optional<UserResourceResponse> getByUserId(String userId);
    List<UserResourceResponse> getAll();
    Optional<UserResourceResponse> updateUser(UpdateUserResourceRequest updatedUserEntity);
    boolean getByUsername(String username);

    @Transactional
    void deleteByUserId(String userId);

    Optional<UserEntity> checkEmailExists(List<Email> emails);

    Optional<UserEntity> findByUsername(String username);
}
