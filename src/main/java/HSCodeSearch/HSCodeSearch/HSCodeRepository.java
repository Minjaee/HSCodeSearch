package HSCodeSearch.HSCodeSearch;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HSCodeRepository extends JpaRepository<HSCode, Long> {

    List<HSCode> findByHsCodeContainingOrNameKorContainingOrNameEngContaining(
            String hsCode, String nameKor, String nameEng
    );

    HSCode findByHsCode(String hsCode);
}