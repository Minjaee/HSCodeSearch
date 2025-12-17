package HSCodeSearch.HSCodeSearch.dto;

public class HsCodeEntry {

    private String code;
    private String name;
    private String description;

    public HsCodeEntry() {}

    public HsCodeEntry(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}
