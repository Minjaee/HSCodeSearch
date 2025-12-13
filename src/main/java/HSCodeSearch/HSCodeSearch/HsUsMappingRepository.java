package HSCodeSearch.HSCodeSearch;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface HsUsMappingRepository extends JpaRepository<HsUsMapping, Long> {
    Optional<HsUsMapping> findByHs6Code(String hs6Code);

    Optional<HsUsMapping> findByKoreaHs10(String koreaHs10);

    Optional<HsUsMapping> findFirstByHs6CodeOrderByPriorityAscIdAsc(String hs6Code);
}
