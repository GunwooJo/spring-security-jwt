package study.springsecurity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.springsecurity.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Optional<Member> findByEmail(String email);
}

