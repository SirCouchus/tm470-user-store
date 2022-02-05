package ac.ou.tm470.user.persistence;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserEntity {

    @Id
    @GenericGenerator(
            name = "sequenceGenerator",
            strategy = "enhanced-sequence",
            parameters = {
                    @org.hibernate.annotations.Parameter(
                            name = "optimizer",
                            value = "pooled-lo"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "initial_value",
                            value = "1"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "increment_size",
                            value = "5"
                    ),
                    @org.hibernate.annotations.Parameter(
                            name = "sequence_name",
                            value = "hibernate_sequence"
                    )
            }
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sequenceGenerator"
    )
    private Long id;

    @Column(name = "userId", columnDefinition = "NVARCHAR(36)")
    private String userId;

    @Column(name = "username", columnDefinition = "NVARCHAR(36)")
    private String username;

    @Column(name = "emails")
    private byte[] emails;

    @Lob
    @Column(name = "userResource", columnDefinition = "BLOB")
    private byte[] user;

    public UserEntity(){

    }

    public UserEntity(String userId, String username, byte[] emails, byte[] user){
        this.userId = userId;
        this.username = username;
        this.emails = emails;
        this.user = user;
    }
}
