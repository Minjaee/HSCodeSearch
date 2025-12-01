package HSCodeSearch.HSCodeSearch;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookmarkController {

    @GetMapping("/bookmark")
    public String bookmarkPage() {
        return "bookmark";  // templates/bookmark.html
    }

}