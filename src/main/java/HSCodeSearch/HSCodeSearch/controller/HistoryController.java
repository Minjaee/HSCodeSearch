package HSCodeSearch.HSCodeSearch.controller;

import HSCodeSearch.HSCodeSearch.entity.History;
import HSCodeSearch.HSCodeSearch.entity.User;
import HSCodeSearch.HSCodeSearch.repository.HistoryRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryRepository historyRepository;

    @GetMapping("/history")
    public String historyPage() {
        return "history";  // templates/history.html
    }

    // History 목록 조회 (최근 20개)
    @GetMapping("/api/history")
    @ResponseBody
    public ResponseEntity<?> getHistory(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        List<History> historyList = historyRepository.findByUserOrderByCreatedAtDesc(loginUser);
        // 최근 20개만 반환
        List<Map<String, Object>> result = historyList.stream()
                .limit(20)
                .map(h -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("hsCode", h.getHsCode());
                    map.put("nameKor", h.getNameKor());
                    map.put("nameEng", h.getNameEng());
                    map.put("createdAt", h.getCreatedAt().toString());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // History 추가
    @PostMapping("/api/history")
    @ResponseBody
    public ResponseEntity<?> addHistory(
            @RequestParam String hsCode,
            @RequestParam(required = false) String nameKor,
            @RequestParam(required = false) String nameEng,
            HttpSession session
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 로그인 안 되어 있어도 200 반환 (조용히 실패)
            return ResponseEntity.ok("success");
        }

        try {
            // 동일한 hsCode의 기존 History 삭제 (최근 것만 남기기 위해)
            List<History> existingHistory = historyRepository.findByUserOrderByCreatedAtDesc(loginUser);
            existingHistory.stream()
                    .filter(h -> h.getHsCode().equals(hsCode))
                    .forEach(historyRepository::delete);

            // 새로운 History 추가
            History history = new History(loginUser, hsCode, nameKor, nameEng);
            historyRepository.save(history);

            // 최대 20개까지만 유지 (오래된 것 삭제)
            List<History> allHistory = historyRepository.findByUserOrderByCreatedAtDesc(loginUser);
            if (allHistory.size() > 20) {
                List<History> toDelete = allHistory.subList(20, allHistory.size());
                historyRepository.deleteAll(toDelete);
            }

            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("History 추가 중 오류가 발생했습니다.");
        }
    }

    // History 전체 삭제
    @DeleteMapping("/api/history")
    @ResponseBody
    public ResponseEntity<?> clearHistory(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            historyRepository.deleteAllByUser(loginUser);
            return ResponseEntity.ok("success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("History 초기화 중 오류가 발생했습니다.");
        }
    }
}
