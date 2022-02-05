package ac.ou.tm470.user.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Transactional
    void deleteByUserId(String userId);
    Optional<UserEntity> findByUserId(String userId);
    Optional<UserEntity> findByUsername(String username);

}
