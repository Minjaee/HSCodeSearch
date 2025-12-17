package HSCodeSearch.HSCodeSearch.loader;

import HSCodeSearch.HSCodeSearch.entity.HsCode;
import HSCodeSearch.HSCodeSearch.entity.HsUsMapping;
import HSCodeSearch.HSCodeSearch.entity.UsTariff;
import HSCodeSearch.HSCodeSearch.repository.HsCodeRepository;
import HSCodeSearch.HSCodeSearch.repository.HsUsMappingRepository;
import HSCodeSearch.HSCodeSearch.repository.UsTariffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Order(2)
public class HsUsMappingLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(HsUsMappingLoader.class);

    private final HsCodeRepository hsCodeRepository;
    private final HsUsMappingRepository hsUsMappingRepository;
    private final UsTariffRepository usTariffRepository;

    public HsUsMappingLoader(HsCodeRepository hsCodeRepository,
                             HsUsMappingRepository hsUsMappingRepository,
                             UsTariffRepository usTariffRepository) {
        this.hsCodeRepository = hsCodeRepository;
        this.hsUsMappingRepository = hsUsMappingRepository;
        this.usTariffRepository = usTariffRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        if (hsUsMappingRepository.count() > 0) {
            log.info("HS↔US mapping already exists. Skipping mapping load.");
            return;
        }

        List<UsTariff> tariffs = usTariffRepository.findAll();
        if (tariffs.isEmpty()) {
            log.warn("No US tariff data available. HS↔US mapping will be skipped.");
            return;
        }

        Map<String, UsTariff> byHs6 = new HashMap<>();
        for (UsTariff tariff : tariffs) {
            String hs6 = safeSix(tariff.getHs6Code());
            if (hs6 == null) continue;

            UsTariff current = byHs6.get(hs6);
            if (current == null || isMoreSpecific(tariff.getUsCode(), current.getUsCode())) {
                byHs6.put(hs6, tariff);
            }
        }

        long success = 0;
        long skipped = 0;

        for (HsCode hsCode : hsCodeRepository.findAll()) {
            String digits = extractDigits(hsCode.getHsCode());
            if (digits.length() < 10) {
                skipped++;
                continue;
            }

            String hs6 = digits.substring(0, 6);
            String koreaHs10 = digits.substring(0, 10);

            UsTariff candidate = byHs6.get(hs6);
            if (candidate == null) {
                skipped++;
                continue;
            }

            HsUsMapping mapping = new HsUsMapping();
            mapping.setHs6Code(hs6);
            mapping.setKoreaHs10(koreaHs10);
            mapping.setHsCode(koreaHs10);
            mapping.setUsCode(candidate.getUsCode());
            mapping.setPriority(1);

            try {
                hsUsMappingRepository.save(mapping);
                success++;
            } catch (Exception e) {
                skipped++;
            }
        }

        log.info("HS↔US mapping load finished - success: {}, skipped: {}", success, skipped);
    }

    private boolean isMoreSpecific(String candidate, String current) {
        if (candidate == null) return false;
        if (current == null) return true;
        String c1 = candidate.replaceAll("\\D", "");
        String c2 = current.replaceAll("\\D", "");
        return c1.length() > c2.length();
    }

    private String extractDigits(String value) {
        if (value == null) return "";
        return value.replaceAll("\\D", "");
    }

    private String safeSix(String value) {
        String digits = extractDigits(value);
        if (digits.length() < 6) return null;
        return digits.substring(0, 6);
    }
}
