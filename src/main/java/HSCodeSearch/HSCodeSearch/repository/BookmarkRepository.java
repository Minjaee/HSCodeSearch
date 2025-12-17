package HSCodeSearch.HSCodeSearch.repository;

import HSCodeSearch.HSCodeSearch.entity.Bookmark;
import HSCodeSearch.HSCodeSearch.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    List<Bookmark> findByUser(User user);
    
    Optional<Bookmark> findByUserAndHsCode(User user, String hsCode);
    
    boolean existsByUserAndHsCode(User user, String hsCode);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Bookmark b WHERE b.user = :user AND b.hsCode = :hsCode")
    void deleteByUserAndHsCode(@Param("user") User user, @Param("hsCode") String hsCode);
}
