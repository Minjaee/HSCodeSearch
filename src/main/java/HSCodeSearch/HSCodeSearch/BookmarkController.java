package HSCodeSearch.HSCodeSearch;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;

    @GetMapping("/bookmark")
    public String bookmarkPage() {
        return "bookmark";  // templates/bookmark.html
    }

    // 북마크 목록 조회
    @GetMapping("/api/bookmarks")
    @ResponseBody
    public ResponseEntity<?> getBookmarks(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        List<Bookmark> bookmarks = bookmarkRepository.findByUser(loginUser);
        List<Map<String, Object>> result = bookmarks.stream()
                .map(b -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hsCode", b.getHsCode());
                    map.put("nameKor", b.getNameKor());
                    map.put("nameEng", b.getNameEng());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // 북마크 추가
    @PostMapping("/api/bookmarks")
    @ResponseBody
    public ResponseEntity<?> addBookmark(
            @RequestParam String hsCode,
            @RequestParam(required = false) String nameKor,
            @RequestParam(required = false) String nameEng,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 이미 존재하는지 확인
        Optional<Bookmark> existing = bookmarkRepository.findByUserAndHsCode(loginUser, hsCode);
        if (existing.isPresent()) {
            return ResponseEntity.ok("success"); // 이미 존재하지만 성공으로 반환 (중복 추가 시도)
        }

        Bookmark bookmark = new Bookmark(loginUser, hsCode, nameKor, nameEng);
        bookmarkRepository.save(bookmark);

        return ResponseEntity.ok("success");
    }

    // 북마크 삭제
    @DeleteMapping("/api/bookmarks")
    @ResponseBody
    public ResponseEntity<?> deleteBookmark(
            @RequestParam String hsCode,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        bookmarkRepository.deleteByUserAndHsCode(loginUser, hsCode);
        return ResponseEntity.ok("success");
    }

    // 북마크 확인 (특정 hsCode가 북마크에 있는지 확인)
    @GetMapping("/api/bookmarks/check")
    @ResponseBody
    public ResponseEntity<?> checkBookmark(
            @RequestParam String hsCode,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.ok(false);
        }

        boolean exists = bookmarkRepository.existsByUserAndHsCode(loginUser, hsCode);
        return ResponseEntity.ok(exists);
    }
}