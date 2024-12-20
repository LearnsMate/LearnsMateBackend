package intbyte4.learnsmate.login_history.service;

import intbyte4.learnsmate.common.exception.CommonException;
import intbyte4.learnsmate.common.exception.StatusEnum;
import intbyte4.learnsmate.login_history.domain.dto.LoginHistoryDTO;
import intbyte4.learnsmate.login_history.domain.entity.LoginHistory;
import intbyte4.learnsmate.login_history.mapper.LoginHistoryMapper;
import intbyte4.learnsmate.login_history.repository.LoginHistoryRepository;
import intbyte4.learnsmate.member.domain.dto.MemberDTO;
import intbyte4.learnsmate.member.domain.entity.Member;
import intbyte4.learnsmate.member.mapper.MemberMapper;
import intbyte4.learnsmate.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final LoginHistoryMapper loginHistoryMapper;
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    // 모든 로그인 내역 조회 (List)
    public List<LoginHistoryDTO> findAllLoginHistory() {
        List<LoginHistory> LoginHistoryList = loginHistoryRepository.findAll();

        // List를 통째로 넘겨주기
        return loginHistoryMapper
                .fromLoginHistoryToLoginHistoryDTO(LoginHistoryList);
    }

    // 1개의 로그인 내역 조회 (1개)
    public LoginHistoryDTO findById(Long loginHistoryCode) {
        LoginHistory loginHistory = loginHistoryRepository.findById(loginHistoryCode)
                .orElseThrow(() -> new CommonException(StatusEnum.LOGIN_HISTORY_NOT_FOUND));

        return loginHistoryMapper.fromLoginHistoryToLoginHistoryDTO(loginHistory);
    }

    // 특정 멤버의 로그인 내역 조회 (List)
    public List<LoginHistoryDTO> findAllLoginHistoryByMemberCode(Long memberCode) {
        if(memberCode == null)
            throw new CommonException(StatusEnum.MISSING_REQUEST_PARAMETER);

        List<LoginHistory> loginHistoryList = loginHistoryRepository.findByMember_MemberCode(memberCode);
        if(loginHistoryList == null || loginHistoryList.isEmpty()){
            throw new CommonException(StatusEnum.LOGIN_HISTORY_NOT_FOUND);
        }

        return loginHistoryMapper.fromLoginHistoryToLoginHistoryDTO(loginHistoryList);
    }

    // 특정 멤버가 로그인 할시 로그인 내역에 저장하는 메서드(로그아웃 시간은 없는 경우)
    public Long saveLoginHistory(LoginHistoryDTO loginHistoryDTO) {

        MemberDTO memberDTO = memberService.findById(loginHistoryDTO.getMemberCode());
        Member member = memberMapper.fromMemberDTOtoMember(memberDTO);

        LoginHistory loginHistory =
                loginHistoryMapper.fromLoginHistoryDTOtoLoginHistory(loginHistoryDTO, member);

        LoginHistory saveHistory = loginHistoryRepository.save(loginHistory);

        // 로그인 내역 코드 반환 -> 로그아웃 시간 저장 해야하기 때문
        return saveHistory.getLoginHistoryCode();
    }

    // 특정 멤버가 로그아웃 할 시에 저장해놓은 코드를 통해 업데이트 로직 수행
    public void saveLogoutTime(Long loginHistoryCode) {
        log.info("로그아웃 요청된 loginHistoryCode: {}", loginHistoryCode);  // 로그 추가

        LoginHistory loginHistory = loginHistoryRepository.findById(loginHistoryCode)
                .orElseThrow(() -> new CommonException(StatusEnum.LOGIN_HISTORY_NOT_FOUND));

        log.info("찾은 LoginHistory: {}", loginHistory);  // 로그 추가

        loginHistory.updateLogoutDate();
        loginHistoryRepository.save(loginHistory);

        log.info("로그아웃 처리 완료");  // 로그 추가
    }
}
