package intbyte4.learnsmate.campaigntemplate.service;

import intbyte4.learnsmate.campaigntemplate.repository.CampaignTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class CampaignTemplateServiceImpl implements CampaignTemplateService {

    private final CampaignTemplateRepository campaignTemplateRepository;
}
