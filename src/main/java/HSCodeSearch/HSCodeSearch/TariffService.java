package HSCodeSearch.HSCodeSearch;

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

        if (hsCode == null || hsCode.length() < 6) {
            return Optional.empty();
        }

        String hs6 = hsCode.substring(0, 6);

        return usTariffRepository.findByHs6Code(hs6);
    }
}
