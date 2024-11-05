package intbyte4.learnsmate.campaigntemplate.domain;

import intbyte4.learnsmate.admin.domain.entity.Admin;
import intbyte4.learnsmate.campaigntemplate.domain.dto.CampaignTemplateDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity(name = "CampaignTemplate")
@Table(name = "campaign_template")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CampaignTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "campaign_template_code", nullable = false, unique = true)
    private Long campaignTemplateCode;

    @Column(name = "campaign_template_title", nullable = false)
    private String campaignTemplateTitle;

    @Column(name = "campaign_template_contents", nullable = false)
    private String campaignTemplateContents;

    @Column(name = "campaign_template_flag", nullable = false)
    @ColumnDefault("true")
    private Boolean campaignTemplateFlag;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "admin_code", nullable = false)
    private Admin admin;

    public CampaignTemplateDTO convertToCampaignDTO() {
        return CampaignTemplateDTO.builder()
                .campaignTemplateCode(this.campaignTemplateCode)
                .campaignTemplateTitle(this.campaignTemplateTitle)
                .campaignTemplateContents(this.campaignTemplateContents)
                .campaignTemplateFlag(this.campaignTemplateFlag)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .adminCode(this.admin.getAdminCode())
                .build();
    }
}
