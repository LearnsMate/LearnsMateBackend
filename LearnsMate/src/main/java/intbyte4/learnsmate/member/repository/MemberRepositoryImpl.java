package intbyte4.learnsmate.member.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import intbyte4.learnsmate.member.domain.MemberType;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.member.domain.entity.QMember;
import intbyte4.learnsmate.member.domain.dto.MemberFilterRequestDTO;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> searchBy(MemberFilterRequestDTO request) {
        QMember member = QMember.member;

        BooleanBuilder builder = new BooleanBuilder()
                .and(eqMemberCode(request.getMemberCode()))
                .and(eqMemberType(request.getMemberType()))
                .and(likeEmail(request.getMemberEmail()))
                .and(likeName(request.getMemberName()))
                .and(betweenAge(request.getMemberStartAge(), request.getMemberEndAge())) // 나이 범위
                .and(likePhone(request.getMemberPhone()))
                .and(likeAddress(request.getMemberAddress()))
                .and(eqMemberFlag(request.getMemberFlag())) // 멤버 플래그
                .and(eqMemberDormantFlag(request.getMemberDormantFlag())) // 휴면 멤버 플래그
                .and(betweenBirth(request.getBirthStartDate(), request.getBirthEndDate())) // 생년월일 범위
                .and(betweenCreatedAt(request.getCreatedStartDate(), request.getCreatedEndDate())); // 생성일 범위

        return queryFactory
                .selectFrom(member)
                .where(builder)
                .fetch();
    }

    // memberCode 검색 조건
    private BooleanExpression eqMemberCode(Long memberCode) {
        return memberCode == null ? null : QMember.member.memberCode.eq(memberCode);
    }

    // memberType 검색 조건
    private BooleanExpression eqMemberType(MemberType memberType) {
        return memberType == null ? null : QMember.member.memberType.eq(memberType);
    }

    // 이메일 검색 조건
    private BooleanExpression likeEmail(String email) {
        return email == null ? null : QMember.member.memberEmail.containsIgnoreCase(email);
    }

    // 이름 검색 조건
    private BooleanExpression likeName(String name) {
        return name == null ? null : QMember.member.memberName.containsIgnoreCase(name);
    }

    // 나이 범위 검색 조건
    private BooleanExpression betweenAge(Integer startAge, Integer endAge) {
        if (startAge == null && endAge == null) return null;
        if (startAge == null) return QMember.member.memberAge.loe(endAge);
        if (endAge == null) return QMember.member.memberAge.goe(startAge);
        return QMember.member.memberAge.between(startAge, endAge);
    }

    // 연락처 검색 조건
    private BooleanExpression likePhone(String phone) {
        return phone == null ? null : QMember.member.memberPhone.containsIgnoreCase(phone);
    }

    // 주소 검색 조건
    private BooleanExpression likeAddress(String address) {
        return address == null ? null : QMember.member.memberAddress.containsIgnoreCase(address);
    }

    // 멤버 플래그 검색 조건
    private BooleanExpression eqMemberFlag(Boolean memberFlag) {
        return memberFlag == null ? null : QMember.member.memberFlag.eq(memberFlag);
    }

    // 휴면 멤버 플래그 검색 조건
    private BooleanExpression eqMemberDormantFlag(Boolean memberDormantFlag) {
        return memberDormantFlag == null ? null : QMember.member.memberDormantStatus.eq(memberDormantFlag);
    }

    // 생년월일 범위 검색 조건
    private BooleanExpression betweenBirth(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) return null;
        if (startDate == null) return QMember.member.memberBirth.loe(endDate);
        if (endDate == null) return QMember.member.memberBirth.goe(startDate);
        return QMember.member.memberBirth.between(startDate, endDate);
    }

    // 생성일 범위 검색 조건
    private BooleanExpression betweenCreatedAt(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) return null;
        if (startDate == null) return QMember.member.createdAt.loe(endDate);
        if (endDate == null) return QMember.member.createdAt.goe(startDate);
        return QMember.member.createdAt.between(startDate, endDate);
    }
}
