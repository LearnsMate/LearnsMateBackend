package intbyte4.learnsmate.member.service;

import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void saveMember(MemberDTO memberDTO) {
        LocalDateTime now = LocalDateTime.now();
        memberRepository.save(memberDTO.toEntity(now, now));
    }

    public List<MemberDTO> findAllMember() {

        List<Member> allMember = memberRepository.findAll();

        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (Member member : allMember) {
            memberDTOList.add(member.toDTO());
        }

        return memberDTOList;
    }
}
