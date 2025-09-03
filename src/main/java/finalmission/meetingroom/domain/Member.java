package finalmission.meetingroom.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    public Member(final Long id, final String name, final String email, final String password) {
        validate(name, email, password);
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Member(final String name, final String email, final String password) {
        this(null, name, email, password);
    }

    private void validate(final String name, final String email, final String password) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("사용자 이름이 존재하지 않습니다.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("사용자 이메일이 존재하지 않습니다.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("패스워드가 존재하지 않습니다.");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Member member)) {
            return false;
        }
        return Objects.equals(getId(), member.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
