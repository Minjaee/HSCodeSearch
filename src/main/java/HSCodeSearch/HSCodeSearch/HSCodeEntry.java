package HSCodeSearch.HSCodeSearch;

public class HSCodeEntry {

    private String code;
    private String name;
    private String description;

    public HSCodeEntry() {}

    public HSCodeEntry(String code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}