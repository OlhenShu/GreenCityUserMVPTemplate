package greencity.entity;

import javax.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "social_networks")
public class SocialNetwork {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "social_network_url",length = 500)
    String url;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    User user;
}
