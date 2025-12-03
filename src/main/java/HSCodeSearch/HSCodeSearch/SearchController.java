package HSCodeSearch.HSCodeSearch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final HSCodeRepository repo;

    public SearchController(HSCodeRepository repo) {
        this.repo = repo;
    }

    // 🔍 검색 API
    @GetMapping("/api/search")
    public List<HSCode> search(@RequestParam("q") String q) {

        if (q == null || q.trim().isEmpty()) return List.of();

        String keyword = q.trim();

        return repo.findByHsCodeContainingOrNameKorContainingOrNameEngContaining(
                keyword, keyword, keyword
        );
    }

    // 📌 상세 조회 API (이거 추가!)
    @GetMapping("/api/detail")
    public HSCode detail(@RequestParam("code") String code) {
        if (code == null || code.trim().isEmpty()) return null;

        return repo.findByHsCode(code.trim());
    }
}