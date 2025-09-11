package kb.hackathon.ssh.domain.will.controller;

import jakarta.validation.Valid;
import kb.hackathon.ssh.domain.will.dto.request.WillCreateRequest;
import kb.hackathon.ssh.domain.will.dto.request.WillUpdateRequest;
import kb.hackathon.ssh.domain.will.dto.response.WillResponse;
import kb.hackathon.ssh.domain.will.entity.DonationType;
import kb.hackathon.ssh.domain.will.service.WillService;
import kb.hackathon.ssh.global.dto.ApiResponse;
import kb.hackathon.ssh.global.util.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wills")
public class WillController {

    private final WillService willService;

    @PostMapping
    public ApiResponse<WillResponse> create(@RequestBody @Valid WillCreateRequest request){
        WillResponse response = willService.create(request, request.userId());
        return ApiUtils.success(response);
    }

    @PostMapping("/voice")
    public ApiResponse<WillResponse> createFromVoice(
            @RequestParam Long userId,
            @RequestParam Long donationOrgId,
            @RequestParam DonationType donationType,
            @RequestParam Long donationAmount,
            @RequestParam Double donationPercentage,
            @RequestParam String contentText,
            @RequestParam String audioFileUrl,
            @RequestParam String witnessEmail){
        WillResponse response = willService.createFromVoice(
                userId, donationOrgId, donationType, donationAmount, donationPercentage, contentText, audioFileUrl, witnessEmail, userId);
        return ApiUtils.success(response);
    }

    @GetMapping("/{willId}/users/{userId}")
    public ApiResponse<WillResponse> getWill(
            @PathVariable Long willId,
            @PathVariable Long userId){
        WillResponse response = willService.findById(willId,userId);
        return ApiUtils.success(response);
    }

    @PutMapping("/{wildId}/users/{userId}")
    public ApiResponse<WillResponse> update(
            @PathVariable Long willId,
            @PathVariable Long userId,
            @RequestBody @Valid WillUpdateRequest willUpdateRequest){
        WillResponse response = willService.update(willId, willUpdateRequest, userId);
        return ApiUtils.success(response);
    }
    @PutMapping("/{willId}/users/{userId}/voice")
    public ApiResponse<WillResponse> updateFromVoice(
            @PathVariable Long willId,
            @PathVariable Long userId,
            @RequestParam String contentText,
            @RequestParam String audioFileUrl,
            @RequestParam String witnessEmail) {

        WillResponse response = willService.updateFromVoice(
                willId, contentText, audioFileUrl, witnessEmail, userId);
        return ApiUtils.success(response);
    }


    @DeleteMapping("/{willId}/users/{userId}")
    public ApiResponse<Void> delete(
            @PathVariable Long willId,
            @PathVariable Long userId) {

        willService.delete(willId, userId);
        return ApiUtils.success(null);
    }
}
