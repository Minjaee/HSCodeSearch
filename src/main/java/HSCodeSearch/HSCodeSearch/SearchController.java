package HSCodeSearch.HSCodeSearch;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    // 임시 데이터 (메모리)
    private final List<HSCodeEntry> data = Arrays.asList(
            new HSCodeEntry("3924.10", "플라스틱 식탁용품", "도시락통, 컵, 그릇 등 플라스틱 식탁·주방용품"),
            new HSCodeEntry("3924.90", "기타 플라스틱 가정용품", "기타 플라스틱 제품"),
            new HSCodeEntry("7323.93", "철강제 조리기구", "스테인리스 냄비, 프라이팬 등의 조리기구"),
            new HSCodeEntry("8516.60", "전기식 조리기구", "전기 오븐 및 취사용 조리기구")
    );

    @GetMapping("/api/search")
    public List<HSCodeEntry> search(@RequestParam("q") String q) {

        String keyword = q.trim().toLowerCase(Locale.KOREAN);

        if (keyword.isEmpty()) return List.of();

        // 코드 / 이름 / 설명 전체에서 검색
        return data.stream()
                .filter(e ->
                        e.getCode().toLowerCase().contains(keyword) ||
                                e.getName().toLowerCase().contains(keyword) ||
                                e.getDescription().toLowerCase().contains(keyword)
                )
                .collect(Collectors.toList());
    }
}
