package hexlet.code.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;


@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "labels")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Label implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true)
    private String name;

    @ManyToMany(mappedBy = "labels", cascade = CascadeType.MERGE)
    private Set<Task> tasks;

    @CreatedDate
    private LocalDateTime createdAt;


}
