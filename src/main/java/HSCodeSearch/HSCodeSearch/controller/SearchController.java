package HSCodeSearch.HSCodeSearch.controller;

import HSCodeSearch.HSCodeSearch.entity.HsCode;
import HSCodeSearch.HSCodeSearch.repository.HsCodeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchController {

    private final HsCodeRepository repo;

    public SearchController(HsCodeRepository repo) {
        this.repo = repo;
    }

    // 🔍 검색 API
    @GetMapping("/api/search")
    public List<HsCode> search(@RequestParam("q") String q) {

        if (q == null || q.trim().isEmpty()) return List.of();

        String keyword = q.trim();

        return repo.findByHsCodeContainingOrNameKorContainingOrNameEngContaining(
                keyword, keyword, keyword
        );
    }

    // 📌 상세 조회 API (이거 추가!)
    @GetMapping("/api/detail")
    public HsCode detail(@RequestParam("code") String code) {
        if (code == null || code.trim().isEmpty()) return null;

        return repo.findByHsCode(code.trim());
    }
}
