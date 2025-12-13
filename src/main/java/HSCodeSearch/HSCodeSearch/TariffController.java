package HSCodeSearch.HSCodeSearch;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tariff")
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping("/us/{hsCode}")
    public ResponseEntity<?> getUsTariff(@PathVariable String hsCode) {

        return tariffService.getUsTariffByHsCode(hsCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
