package kb.hackathon.ssh.domain.will.dto;

import kb.hackathon.ssh.domain.will.dto.request.WillCreateRequest;
import kb.hackathon.ssh.domain.will.dto.request.WillUpdateRequest;
import kb.hackathon.ssh.domain.will.dto.response.WillResponse;
import kb.hackathon.ssh.domain.will.entity.DonationType;
import kb.hackathon.ssh.domain.will.entity.Will;

public final class WillMapper {

    private WillMapper() {
    }

    public static Will toEntity(WillCreateRequest willCreateRequest) {
        Will w = new Will();
        w.setUserId(willCreateRequest.userId());
        w.setDonationOrgId(willCreateRequest.donationOrgId());
        w.setDonationType(willCreateRequest.donationType());
        w.setDonationAmount(willCreateRequest.donationAmount());
        w.setDonationPercentage(willCreateRequest.donationPercentage());
        w.setContentText(willCreateRequest.contentText());
        w.setAudioFileUrl(willCreateRequest.audioFileUrl());
        w.setWitnessEmail(willCreateRequest.witnessEmail());
        return w;
    }

    public static Will createFromVoice(Long userId, Long donationOrgId,
                                       DonationType donationType, Long donationAmount,
                                       Double donationPercentage, String contentText,
                                       String audioFileUrl, String witnessEmail) {
        Will w = new Will();
        w.setUserId(userId);
        w.setDonationOrgId(donationOrgId);
        w.setDonationType(donationType);
        w.setDonationAmount(donationAmount);
        w.setDonationPercentage(donationPercentage);
        w.setContentText(contentText);
        w.setAudioFileUrl(audioFileUrl);
        w.setWitnessEmail(witnessEmail);
        return w;
    }

    public static void applyUpdate(Will target, WillUpdateRequest willUpdateRequest) {
        if(willUpdateRequest.donationType() != null) target.setDonationType(willUpdateRequest.donationType());
        if(willUpdateRequest.donationAmount() != null) target.setDonationAmount(willUpdateRequest.donationAmount());
        if(willUpdateRequest.donationPercentage() != null) target.setDonationPercentage(willUpdateRequest.donationPercentage());
        if (willUpdateRequest.contentText() != null) target.setContentText(willUpdateRequest.contentText());
        if (willUpdateRequest.audioFileUrl() != null) target.setAudioFileUrl(willUpdateRequest.audioFileUrl());
        if (willUpdateRequest.witnessEmail() != null) target.setWitnessEmail(willUpdateRequest.witnessEmail());
        if (willUpdateRequest.donationOrgId() != null) target.setDonationOrgId(willUpdateRequest.donationOrgId());
    }

    public static void updateFromVoice(Will target, String contentText, String audioFileUrl, String witnessEmail) {
        target.setContentText(contentText);
        target.setAudioFileUrl(audioFileUrl);
        target.setWitnessEmail(witnessEmail);
    }

    public static WillResponse toResponse(Will entity) {
        return new WillResponse(
                entity.getWillId(),
                entity.getUserId(),
                entity.getDonationOrgId(),
                entity.getDonationType(),
                entity.getDonationAmount(),
                entity.getDonationPercentage(),
                entity.getContentText(),
                entity.getAudioFileUrl(),
                entity.getWitnessEmail(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
