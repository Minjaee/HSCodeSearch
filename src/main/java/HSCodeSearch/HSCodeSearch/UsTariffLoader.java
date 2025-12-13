package HSCodeSearch.HSCodeSearch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Order(1)
public class UsTariffLoader implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UsTariffLoader.class);
    private static final String CSV_PATH = "static/dataset/HS_DUTY_US_2025.csv";
    private static final int TARGET_YEAR = 2025;

    private final UsTariffRepository usTariffRepository;

    public UsTariffLoader(UsTariffRepository usTariffRepository) {
        this.usTariffRepository = usTariffRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        // 이미 데이터가 있으면 중복 적재 방지
        if (usTariffRepository.count() > 0) {
            log.info("US tariff data already exists. Skipping CSV load.");
            return;
        }

        int success = 0;
        int skipped = 0;

        try {
            ClassPathResource resource = new ClassPathResource(CSV_PATH);

            if (!resource.exists()) {
                log.error("US tariff CSV not found at {}", CSV_PATH);
                return;
            }

            try (InputStream is = resource.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

                // skip header
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        String[] row = parseCsvLine(line);

                        String usCode = safeTrim(row, 3);

                        // HS6 추출 (숫자만 취합)
                        String hs6 = extractHs6(usCode);

                        if (hs6 == null || usCode.isEmpty()) {
                            skipped++;
                            continue;
                        }

                        UsTariff tariff = new UsTariff();
                        tariff.setHs6Code(hs6);
                        tariff.setHsCode(usCode);
                        tariff.setUsCode(usCode);
                        tariff.setDescEng(safeTrim(row, 4));
                        tariff.setDescKor(safeTrim(row, 5));
                        tariff.setUnit(safeTrim(row, 6));
                        tariff.setRateGeneral(cleanValue(safeTrim(row, 7)));
                        tariff.setRateSpecial(cleanValue(safeTrim(row, 8)));
                        tariff.setRateDuty2(cleanValue(safeTrim(row, 9)));
                        tariff.setYear(parseYear(safeTrim(row, 1)));

                        usTariffRepository.save(tariff);
                        success++;

                    } catch (Exception e) {
                        skipped++;
                    }
                }
            }

            log.info("✅ US tariff CSV load finished - success: {}, skipped: {}", success, skipped);

        } catch (Exception e) {
            log.error("❌ US tariff loader failed", e);
        }
    }

    private String[] parseCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }

    private String safeTrim(String[] row, int index) {
        if (row.length <= index || row[index] == null) return "";
        return row[index].trim();
    }

    private String extractHs6(String usCode) {
        if (usCode == null) return null;
        String digitsOnly = usCode.replaceAll("\\D", "");
        if (digitsOnly.length() < 6) return null;
        return digitsOnly.substring(0, 6);
    }

    private String cleanValue(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Integer parseYear(String rawYear) {
        if (rawYear == null || rawYear.isEmpty()) {
            return TARGET_YEAR;
        }
        try {
            if (rawYear.length() >= 4) {
                return Integer.parseInt(rawYear.substring(0, 4));
            }
            return Integer.parseInt(rawYear);
        } catch (NumberFormatException e) {
            return TARGET_YEAR;
        }
    }
}
