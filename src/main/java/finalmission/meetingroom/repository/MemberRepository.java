package finalmission.meetingroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import finalmission.meetingroom.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);
}
