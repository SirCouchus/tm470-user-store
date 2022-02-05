package ac.ou.tm470.user.service.impl;

import ac.ou.tm470.user.generated.model.UserResourceResponse;
import ac.ou.tm470.user.generated.model.CreateUserResourceRequest;
import ac.ou.tm470.user.generated.model.UpdateUserResourceRequest;
import ac.ou.tm470.user.generated.model.Email;
import ac.ou.tm470.user.persistence.UserEntity;
import ac.ou.tm470.user.persistence.UserRepository;
import ac.ou.tm470.user.service.UserPersistenceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
public class UserPersistenceServiceImpl implements UserPersistenceService {

    private final UserRepository repository;
    private final ObjectMapper mapper;

    @Autowired
    public UserPersistenceServiceImpl(UserRepository repository, ObjectMapper mapper){
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserResourceResponse saveUser(CreateUserResourceRequest user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(user.getUserName());
        try {
            userEntity.setEmails(mapper.writeValueAsBytes(user.getEmails()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            userEntity.setUser(mapper.writeValueAsBytes(user));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        userEntity.setUserId(UUID.randomUUID().toString());
        try {
            this.repository.save(userEntity);
        } catch(DataAccessException e){
            e.printStackTrace();
        }

        UserResourceResponse response = mapToResponse(userEntity);
        response.setId(userEntity.getUserId());

        return response;
    }

    @Override
    public Optional<UserResourceResponse> getByUserId(String userId) {
        Optional<UserEntity> optionalUserEntity = repository.findByUserId(userId);
        if(optionalUserEntity.isPresent()){
            UserEntity userEntity = optionalUserEntity.get();
            return Optional.of(this.mapToResponse(userEntity));
        }
        return Optional.empty();
    }

    @Override
    public List<UserResourceResponse> getAll() {
        List<UserResourceResponse> listOfUserResourceResponses = new ArrayList<>();
        List<UserEntity> listOfUserEntities = this.repository.findAll();

        for (UserEntity listOfUserEntity : listOfUserEntities) {
            listOfUserResourceResponses.add(mapToResponse(listOfUserEntity));
        }
        return listOfUserResourceResponses;
    }

    @Override
    public Optional<UserResourceResponse> updateUser(UpdateUserResourceRequest updatedUserEntity) {
        Optional<UserEntity> optionalUserEntity = repository.findByUserId(updatedUserEntity.getId());
        if(optionalUserEntity.isPresent()){
            UserEntity userEntity = optionalUserEntity.get();
            try {
                userEntity.setUser(mapper.writeValueAsBytes(updatedUserEntity));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            userEntity.setUsername(updatedUserEntity.getUserName());
            return Optional.of(mapToResponse(repository.save(userEntity)));
        }

        return Optional.empty();
    }

    @Override
    public boolean getByUsername(String username) {
        return repository.findByUsername(username).isPresent();
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return this.repository.findByUsername(username);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        this.repository.deleteByUserId(userId);
    }

    @Override
    public Optional<UserEntity> checkEmailExists(List<Email> emails) {
        return Optional.empty();
    }

    private UserResourceResponse mapToResponse(UserEntity userEntity) {
        UserResourceResponse userResourceResponse = null;
        try {
            userResourceResponse = mapper.readValue(userEntity.getUser(), UserResourceResponse.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(userResourceResponse).setId(userEntity.getUserId());
        userResourceResponse.setUserName(userEntity.getUsername());
        return userResourceResponse;
    }
}
