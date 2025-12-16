package HSCodeSearch.HSCodeSearch;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    
    List<History> findByUserOrderByCreatedAtDesc(User user);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM History h WHERE h.user = :user")
    void deleteAllByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(h) FROM History h WHERE h.user = :user")
    long countByUser(@Param("user") User user);
}