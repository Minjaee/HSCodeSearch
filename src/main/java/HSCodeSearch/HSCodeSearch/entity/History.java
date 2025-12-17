package HSCodeSearch.HSCodeSearch.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "history")
public class History {

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public History(User user, String hsCode, String nameKor, String nameEng) {
        this.user = user;
        this.hsCode = hsCode;
        this.nameKor = nameKor;
        this.nameEng = nameEng;
        this.createdAt = LocalDateTime.now();
    }
}
