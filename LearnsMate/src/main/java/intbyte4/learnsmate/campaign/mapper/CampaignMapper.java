package intbyte4.learnsmate.campaign.mapper;


import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.campaign.domain.dto.CampaignDTO;
import intbyte4.learnsmate.campaign.domain.entity.Campaign;
import intbyte4.learnsmate.campaign.domain.entity.CampaignTypeEnum;
import intbyte4.learnsmate.campaign.domain.vo.request.RequestEditCampaignVO;
import intbyte4.learnsmate.campaign.domain.vo.request.RequestFindCampaignByCampaignCodeVO;
import intbyte4.learnsmate.campaign.domain.vo.request.RequestRegisterCampaignVO;
import intbyte4.learnsmate.campaign.domain.vo.response.ResponseEditCampaignVO;
import intbyte4.learnsmate.campaign.domain.vo.response.ResponseFindCampaignVO;
import intbyte4.learnsmate.campaign.domain.vo.response.ResponseRegisterCampaignVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CampaignMapper {

    public CampaignDTO toDTO(Campaign entity) {
        return CampaignDTO.builder()
                .campaignCode(entity.getCampaignCode())
                .campaignTitle(entity.getCampaignTitle())
                .campaignContents(entity.getCampaignContents())
                .campaignType(String.valueOf(entity.getCampaignType()))
                .campaignSendDate(entity.getCampaignSendDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .adminCode(entity.getAdmin().getAdminCode())
                .build();
    }

    public Campaign toEntity(CampaignDTO dto, Admin admin) {
        return Campaign.builder()
                .campaignCode(dto.getCampaignCode())
                .campaignTitle(dto.getCampaignTitle())
                .campaignContents(dto.getCampaignContents())
                .campaignType(CampaignTypeEnum.valueOf(dto.getCampaignType()))
                .campaignSendDate(dto.getCampaignSendDate())
                .createdAt(dto.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .admin(admin)
                .build();
    }

    public CampaignDTO fromRegisterRequestVOtoDTO(RequestRegisterCampaignVO vo) {
        return CampaignDTO.builder()
                .campaignCode(vo.getCampaignCode())
                .campaignTitle(vo.getCampaignTitle())
                .campaignContents(vo.getCampaignContents())
                .campaignType(String.valueOf(vo.getCampaignType()))
                .campaignSendDate(vo.getCampaignSendDate())
                .createdAt(vo.getCreatedAt())
                .updatedAt(vo.getUpdatedAt())
                .adminCode(vo.getAdminCode())
                .build();
    }

    public ResponseRegisterCampaignVO fromDtoToRegisterResponseVO(CampaignDTO dto){
        return ResponseRegisterCampaignVO.builder()
                .campaignCode(dto.getCampaignCode())
                .campaignTitle(dto.getCampaignTitle())
                .campaignContents(dto.getCampaignContents())
                .campaignType(dto.getCampaignType())
                .campaignSendDate(dto.getCampaignSendDate())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .adminCode(dto.getAdminCode())
                .build();
    }

    public CampaignDTO fromEditRequestVOtoDTO(RequestEditCampaignVO vo) {
        return CampaignDTO.builder()
                .campaignTitle(vo.getCampaignTitle())
                .campaignContents(vo.getCampaignContents())
                .campaignSendDate(vo.getCampaignSendDate())
                .updatedAt(vo.getUpdatedAt())
                .build();
    }

    public ResponseEditCampaignVO fromDtoToEditResponseVO(CampaignDTO dto){
        return ResponseEditCampaignVO.builder()
                .campaignCode(dto.getCampaignCode())
                .campaignTitle(dto.getCampaignTitle())
                .campaignContents(dto.getCampaignContents())
                .campaignType(dto.getCampaignType())
                .campaignSendDate(dto.getCampaignSendDate())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .adminCode(dto.getAdminCode())
                .build();
    }

    public List<ResponseFindCampaignVO> fromDtoListToFindCampaignVO(List<CampaignDTO> dtoList) {
        return dtoList.stream().map(dto -> ResponseFindCampaignVO.builder()
                .campaignCode(dto.getCampaignCode())
                .campaignTitle(dto.getCampaignTitle())
                .campaignContents(dto.getCampaignContents())
                .campaignType(dto.getCampaignType())
                .campaignSendDate(dto.getCampaignSendDate())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .adminCode(dto.getAdminCode())
                .build()).collect(Collectors.toList());
    }

    public CampaignDTO fromFindRequestVOtoDTO(RequestFindCampaignByCampaignCodeVO vo) {
        return CampaignDTO.builder()
                .campaignCode(vo.getCampaignCode())
                .build();
    }

    public ResponseFindCampaignVO fromDtoToFindResponseVO(CampaignDTO dto) {
        return ResponseFindCampaignVO.builder()
                .campaignCode(dto.getCampaignCode())
                .campaignTitle(dto.getCampaignTitle())
                .campaignType(dto.getCampaignType())
                .campaignSendDate(dto.getCampaignSendDate())
                .build();
    }

}
