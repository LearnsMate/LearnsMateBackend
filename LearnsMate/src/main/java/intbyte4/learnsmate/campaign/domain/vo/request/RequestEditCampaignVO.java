package intbyte4.learnsmate.campaign.domain.vo.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestEditCampaignVO {

    @JsonProperty("members")
    private List<RequestEditCampaignStudentVO> studentList;

    @JsonProperty("coupons")
    private List<RequestEditCampaignCouponVO> couponList;

    @JsonProperty("admin_code")
    private Long adminCode;

    @JsonProperty("campaign_code")
    private Long campaignCode;

    @JsonProperty("campaign_title")
    private String campaignTitle;

    @JsonProperty("campaign_contents")
    private String campaignContents;

    @JsonProperty("campaign_type")
    private String campaignType;

    @JsonProperty("campaign_method")
    private String campaignMethod;

    @JsonProperty("campaign_send_flag")
    private Boolean campaignSendFlag;

    @JsonProperty("campaign_send_date")
    private LocalDateTime campaignSendDate;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

}
