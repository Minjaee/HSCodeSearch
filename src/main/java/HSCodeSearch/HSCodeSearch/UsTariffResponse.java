package HSCodeSearch.HSCodeSearch;

import java.math.BigDecimal;

/**
 * 미국 관세율 API 응답을 전달하기 위한 DTO.
 */
public class UsTariffResponse {
    private String usCode;
    private String descEng;
    private BigDecimal rateGeneral;
    private BigDecimal rateSpecial;

    public UsTariffResponse() {
    }

    public UsTariffResponse(String usCode, String descEng, BigDecimal rateGeneral, BigDecimal rateSpecial) {
        this.usCode = usCode;
        this.descEng = descEng;
        this.rateGeneral = rateGeneral;
        this.rateSpecial = rateSpecial;
    }

    public String getUsCode() {
        return usCode;
    }

    public void setUsCode(String usCode) {
        this.usCode = usCode;
    }

    public String getDescEng() {
        return descEng;
    }

    public void setDescEng(String descEng) {
        this.descEng = descEng;
    }

    public BigDecimal getRateGeneral() {
        return rateGeneral;
    }

    public void setRateGeneral(BigDecimal rateGeneral) {
        this.rateGeneral = rateGeneral;
    }

    public BigDecimal getRateSpecial() {
        return rateSpecial;
    }

    public void setRateSpecial(BigDecimal rateSpecial) {
        this.rateSpecial = rateSpecial;
    }
}
