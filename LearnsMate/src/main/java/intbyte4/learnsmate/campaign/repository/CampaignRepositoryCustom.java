package intbyte4.learnsmate.campaign.repository;

import intbyte4.learnsmate.campaign.domain.entity.Campaign;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepositoryCustom {
    List<Campaign> searchBy(Campaign campaign, LocalDateTime startDate, LocalDateTime endDate);
}