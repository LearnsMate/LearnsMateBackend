package intbyte4.learnsmate.couponbycampaign.service;

import intbyte4.learnsmate.campaign.domain.dto.CampaignDTO;
import intbyte4.learnsmate.coupon.domain.dto.CouponDTO;

public interface CouponByCampaignService {
    void registerCouponByCampaign(CouponDTO coupon, CampaignDTO campaign);
}