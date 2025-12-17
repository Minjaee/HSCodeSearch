package HSCodeSearch.HSCodeSearch.service;

import HSCodeSearch.HSCodeSearch.entity.HsUsMapping;
import HSCodeSearch.HSCodeSearch.entity.UsTariff;
import HSCodeSearch.HSCodeSearch.repository.HsUsMappingRepository;
import HSCodeSearch.HSCodeSearch.repository.UsTariffRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class TariffService {

    private final HsUsMappingRepository hsUsMappingRepository;
    private final UsTariffRepository usTariffRepository;

    public TariffService(HsUsMappingRepository hsUsMappingRepository,
                         UsTariffRepository usTariffRepository) {
        this.hsUsMappingRepository = hsUsMappingRepository;
        this.usTariffRepository = usTariffRepository;
    }

    /**
     * HS 코드로 미국 관세 정보 조회
     */
    public Optional<UsTariff> getUsTariffByHsCode(String hsCode) {

        if (hsCode == null) {
            return Optional.empty();
        }

        String digits = hsCode.replaceAll("\\D", "");
        if (digits.length() < 6) {
            return Optional.empty();
        }

        String hs6 = digits.substring(0, 6);
        String hs10 = digits.length() >= 10 ? digits.substring(0, 10) : null;

        // 1) 한미 매핑 우선 조회
        Optional<HsUsMapping> mapping = Optional.empty();
        if (hs10 != null) {
            mapping = hsUsMappingRepository.findByKoreaHs10(hs10);
        }

        if (mapping.isEmpty()) {
            mapping = hsUsMappingRepository.findFirstByHs6CodeOrderByPriorityAscIdAsc(hs6);
        }

        if (mapping.isPresent()) {
            String usCode = mapping.get().getUsCode();

            Optional<UsTariff> direct = usTariffRepository.findByUsCode(usCode);
            if (direct.isPresent()) {
                return direct;
            }
        }

        return usTariffRepository.findFirstByHs6CodeOrderByIdAsc(hs6);
    }
}
