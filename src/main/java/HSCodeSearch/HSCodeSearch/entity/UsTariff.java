package HSCodeSearch.HSCodeSearch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "us_tariff")
public class UsTariff {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * HS 6단위 코드 (DB 핵심 키)
     */
    @Getter
    @Setter
    @Column(name = "hs6_code", nullable = false, length = 6)
    private String hs6Code;

    /**
     * 전체 HS 코드(필수 컬럼 호환을 위해 저장)
     */
    @Getter
    @Setter
    @Column(name = "hs_code", nullable = false, length = 20)
    private String hsCode;

    /**
     * 영문 설명
     */
    @Getter
    @Setter
    @Column(name = "desc_eng", columnDefinition = "TEXT")
    private String descEng;

    /**
     * 한글 설명
     */
    @Getter
    @Setter
    @Column(name = "desc_kor", columnDefinition = "TEXT")
    private String descKor;

    /**
     * 일반 관세율 (TEXT 취급)
     */
    @Getter
    @Setter
    @Column(name = "rate_general", columnDefinition = "TEXT")
    private String rateGeneral;

    /**
     * 특혜 관세율
     */
    @Getter
    @Setter
    @Column(name = "rate_special", columnDefinition = "TEXT")
    private String rateSpecial;

    /**
     * 기타 관세
     */
    @Getter
    @Setter
    @Column(name = "rate_duty2", columnDefinition = "TEXT")
    private String rateDuty2;

    /**
     * 단위
     */
    @Getter
    @Setter
    @Column(name = "unit")
    private String unit;

    /**
     * 미국 코드
     */
    @Getter
    @Setter
    @Column(name = "us_code", nullable = false, length = 10)
    private String usCode;

    /**
     * 연도
     */
    @Getter
    @Setter
    @Column(name = "year")
    private Integer year;
}
