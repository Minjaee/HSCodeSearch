package HSCodeSearch.HSCodeSearch;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    
    List<Bookmark> findByUser(User user);
    
    Optional<Bookmark> findByUserAndHsCode(User user, String hsCode);
    
    boolean existsByUserAndHsCode(User user, String hsCode);
    
    void deleteByUserAndHsCode(User user, String hsCode);
}