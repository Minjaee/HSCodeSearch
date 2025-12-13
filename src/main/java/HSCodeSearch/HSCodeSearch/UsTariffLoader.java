package HSCodeSearch.HSCodeSearch;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@Component
public class UsTariffLoader implements ApplicationRunner {

    private final UsTariffRepository usTariffRepository;

    public UsTariffLoader(UsTariffRepository usTariffRepository) {
        this.usTariffRepository = usTariffRepository;
    }

    @Override
    public void run(ApplicationArguments args) {

        int success = 0;
        int skipped = 0;

        try {
            ClassPathResource resource =
                    new ClassPathResource("static/dataset/HS_DUTY_US_2025.csv");

            if (!resource.exists()) {
                System.err.println("❌ US tariff CSV not found");
                return;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)
            );

            String line;
            reader.readLine(); // header skip

            while ((line = reader.readLine()) != null) {
                try {
                    String[] c = line.split(",", -1);

                    // 🔑 필수 컬럼
                    String hsRaw = c.length > 3 ? c[3].trim() : "";
                    String usCode = c.length > 8 ? c[8].trim() : "";

                    if (hsRaw.length() < 6 || usCode.isEmpty()) {
                        skipped++;
                        continue;
                    }

                    String hs6 = hsRaw.substring(0, 6);

                    UsTariff t = new UsTariff();
                    t.setHs6Code(hs6);
                    t.setUsCode(usCode);

                    t.setDescEng(get(c, 4));
                    t.setDescKor(get(c, 5));
                    t.setRateGeneral(String.valueOf(parseDecimal(c, 6)));
                    t.setRateSpecial(String.valueOf(parseDecimal(c, 7)));
                    t.setRateDuty2(String.valueOf(parseDecimal(c, 9)));
                    t.setYear(2025);

                    usTariffRepository.saveAndFlush(t);
                    success++;

                } catch (Exception e) {
                    skipped++;
                }
            }

            System.out.println("✅ US tariff CSV load finished");
            System.out.println("   - success : " + success);
            System.out.println("   - skipped : " + skipped);

        } catch (Exception e) {
            System.err.println("❌ US tariff loader failed");
            e.printStackTrace();
        }
    }

    private String get(String[] c, int i) {
        if (c.length <= i) return null;
        return c[i].trim();
    }

    private BigDecimal parseDecimal(String[] c, int i) {
        if (c.length <= i) return null;
        String v = c[i].trim();
        if (v.isEmpty() || v.equalsIgnoreCase("free")) return null;
        try {
            return new BigDecimal(v.replace("%", ""));
        } catch (Exception e) {
            return null;
        }
    }
}
