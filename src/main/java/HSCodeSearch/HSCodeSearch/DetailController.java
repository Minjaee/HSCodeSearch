package HSCodeSearch.HSCodeSearch;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class DetailController {

    @GetMapping("/detail")
    public String detailPage() {
        return "detail"; // templates/detail.html 또는 static/detail.html
    }
}