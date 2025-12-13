package HSCodeSearch.HSCodeSearch;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "hs_us_mapping",
        indexes = {
                @Index(name = "idx_hs_code", columnList = "hs_code")
        })
public class HsUsMapping {

    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** HS 6단위 코드 */
    @Getter
    @Setter
    @Column(name = "hs6_code", nullable = false, length = 6)
    private String hs6Code;

    /** 한국 HS 10단위 */
    @Getter
    @Setter
    @Column(name = "korea_hs10", nullable = false, length = 10)
    private String koreaHs10;

    /** 한국 HS 코드 (DB 호환 컬럼) */
    @Getter
    @Setter
    @Column(name = "hs_code", nullable = false, length = 10)
    private String hsCode;

    /** 미국 코드 */
    @Getter
    @Setter
    @Column(name = "us_code", nullable = false, length = 10)
    private String usCode;

    /** 우선순위 */
    @Getter
    @Setter
    @Column(name = "priority")
    private Integer priority;

    /** 비고 */
    @Getter
    @Setter
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;
}
