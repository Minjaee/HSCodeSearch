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

    @GetMapping("/api/search")
    public List<HSCode> search(@RequestParam("q") String q) {

        if (q == null || q.trim().isEmpty()) return List.of();

        String keyword = q.trim();

        return repo.findByHsCodeContainingOrNameKorContainingOrNameEngContaining(
                keyword, keyword, keyword
        );
    }
}