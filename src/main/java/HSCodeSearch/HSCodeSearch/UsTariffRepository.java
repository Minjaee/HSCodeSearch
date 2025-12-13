package HSCodeSearch.HSCodeSearch;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsTariffRepository extends JpaRepository<UsTariff, Long> {
    Optional<UsTariff> findByHs6Code(String hs6Code);

    Optional<UsTariff> findByUsCode(String usCode);

    Optional<UsTariff> findFirstByHs6CodeOrderByIdAsc(String hs6Code);
}

