package com.usg.BookAndBeanstalkMember.adaptor.out.persistance;

import com.usg.BookAndBeanstalkMember.application.port.out.MemberCheckOutputPort;
import com.usg.BookAndBeanstalkMember.application.port.out.MemberFindOutputPort;
import com.usg.BookAndBeanstalkMember.application.port.out.MemberJoinOutputPort;
import com.usg.BookAndBeanstalkMember.domain.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepository implements MemberFindOutputPort, MemberJoinOutputPort, MemberCheckOutputPort {

    private final PasswordEncoder passwordEncoder;
    private final JpaMemberRepository repository;

    public MemberRepository(PasswordEncoder passwordEncoder, JpaMemberRepository repository) {
        this.passwordEncoder = passwordEncoder;
        this.repository = repository;
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return repository.existsByNickname(nickname);
    }

    @Override
    public Optional<Member> findOne(String email) {

        return repository.findByEmail(email);
    }

    @Override
    public Long join(String email, String password, String nickname) {
        Member member = Member.createUser(email, password, nickname, passwordEncoder);
        validateDuplicateMember(member);
        repository.save(member);

        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        repository.findByEmail(member.getEmail())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }
}
