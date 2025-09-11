package kb.hackathon.ssh.domain.will.service;

import kb.hackathon.ssh.domain.will.dto.WillMapper;
import kb.hackathon.ssh.domain.will.dto.request.WillCreateRequest;
import kb.hackathon.ssh.domain.will.dto.request.WillUpdateRequest;
import kb.hackathon.ssh.domain.will.dto.response.WillResponse;
import kb.hackathon.ssh.domain.will.entity.DonationType;
import kb.hackathon.ssh.domain.will.entity.Will;
import kb.hackathon.ssh.domain.will.repository.WillRepository;
import kb.hackathon.ssh.global.error.F.ErrorCode;
import kb.hackathon.ssh.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WillService {

    private final WillRepository willRepository;

    public WillResponse create(WillCreateRequest willCreateRequest, Long authenticatedUserId) {
        if(!willCreateRequest.userId().equals(authenticatedUserId)){
            throw new BusinessException(ErrorCode.WILL_ACCESS_DENIED);
        }
        Will entity = WillMapper.toEntity(willCreateRequest);
        Will saved = willRepository.save(entity);
        return WillMapper.toResponse(saved);
    }

    public WillResponse createFromVoice(Long userId, Long donationOrgId,
                                        DonationType donationType, Long donationAmount,
                                        Double donationPercentage, String contentText,
                                        String audioFileUrl, String witnessEmail,
                                        Long authenticatedUserId){
        if(!userId.equals(authenticatedUserId)){
            throw new BusinessException(ErrorCode.WILL_ACCESS_DENIED);
        }
        Will entity = WillMapper.createFromVoice(userId, donationOrgId, donationType, donationAmount,
                donationPercentage, contentText, audioFileUrl, witnessEmail);
        Will saved = willRepository.save(entity);
        return WillMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public WillResponse findById(Long willId, Long authenticatedUserId) {
        Will will = willRepository.findByWillIdAndUserId(willId, authenticatedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WILL_NOT_FOUND));
        return WillMapper.toResponse(will);
    }

    public WillResponse update(Long willId, WillUpdateRequest request, Long authenticatedUserId) {
        Will will = willRepository.findByWillIdAndUserId(willId, authenticatedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WILL_NOT_FOUND));

        WillMapper.applyUpdate(will, request);
        Will saved = willRepository.save(will);
        return WillMapper.toResponse(saved);
    }

    public WillResponse updateFromVoice(Long willId, String contentText, String audioFileUrl,
                                        String witnessEmail, Long authenticatedUserId) {
        Will will = willRepository.findByWillIdAndUserId(willId, authenticatedUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.WILL_NOT_FOUND));

        WillMapper.updateFromVoice(will, contentText, audioFileUrl, witnessEmail);
        Will saved = willRepository.save(will);
        return WillMapper.toResponse(saved);
    }

    public void delete(Long willId, Long authenticatedUserId) {
        if (!willRepository.existsByWillIdAndUserId(willId, authenticatedUserId)) {
            throw new BusinessException(ErrorCode.WILL_ACCESS_DENIED);
        }
        willRepository.deleteById(willId);
    }
}
