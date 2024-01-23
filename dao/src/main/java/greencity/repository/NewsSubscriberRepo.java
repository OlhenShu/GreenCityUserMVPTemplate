package greencity.repository;

import greencity.entity.NewsSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsSubscriberRepo extends JpaRepository<NewsSubscriber, Long> {
    /**
     * Checks if a news subscriber with the given email address exists.
     *
     * @param email The email address to check for existence.
     * @return true if a news subscriber with the given email address exists, false otherwise.
     */
    Boolean existsByEmail(String email);
}
