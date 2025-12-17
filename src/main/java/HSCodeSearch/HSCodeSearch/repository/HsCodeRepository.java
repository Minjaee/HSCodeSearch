package HSCodeSearch.HSCodeSearch.repository;

import HSCodeSearch.HSCodeSearch.entity.HsCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HsCodeRepository extends JpaRepository<HsCode, Long> {

    List<HsCode> findByHsCodeContainingOrNameKorContainingOrNameEngContaining(
            String hsCode, String nameKor, String nameEng
    );

    HsCode findByHsCode(String hsCode);
}
