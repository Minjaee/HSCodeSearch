package HSCodeSearch.HSCodeSearch;

import jakarta.persistence.*;

@Entity
@Table(name = "hs_code")
public class HSCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hs_code")
    private String hsCode;

    @Column(name = "name_kor")
    private String nameKor;

    @Column(name = "name_eng")
    private String nameEng;

    @Column(name = "export_code")
    private String exportCode;

    @Column(name = "import_code")
    private String importCode;

    @Column(name = "unified_code")
    private String unifiedCode;

    @Column(name = "unified_name")
    private String unifiedName;

    public HSCode() {}

    public Long getId() { return id; }

    public String getHsCode() { return hsCode; }
    public void setHsCode(String hsCode) { this.hsCode = hsCode; }

    public String getNameKor() { return nameKor; }
    public void setNameKor(String nameKor) { this.nameKor = nameKor; }

    public String getNameEng() { return nameEng; }
    public void setNameEng(String nameEng) { this.nameEng = nameEng; }

    public String getExportCode() { return exportCode; }
    public void setExportCode(String exportCode) { this.exportCode = exportCode; }

    public String getImportCode() { return importCode; }
    public void setImportCode(String importCode) { this.importCode = importCode; }

    public String getUnifiedCode() { return unifiedCode; }
    public void setUnifiedCode(String unifiedCode) { this.unifiedCode = unifiedCode; }

    public String getUnifiedName() { return unifiedName; }
    public void setUnifiedName(String unifiedName) { this.unifiedName = unifiedName; }
}
