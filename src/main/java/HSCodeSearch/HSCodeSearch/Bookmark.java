package HSCodeSearch.HSCodeSearch;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bookmarks", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "hs_code"})
})
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "hs_code", nullable = false)
    private String hsCode;

    @Column(name = "name_kor")
    private String nameKor;

    @Column(name = "name_eng")
    private String nameEng;

    public Bookmark(User user, String hsCode, String nameKor, String nameEng) {
        this.user = user;
        this.hsCode = hsCode;
        this.nameKor = nameKor;
        this.nameEng = nameEng;
    }
}