package kb.hackathon.ssh.domain.phishing.controller;

import kb.hackathon.ssh.domain.phishing.entity.Phishing;
import kb.hackathon.ssh.domain.phishing.service.PhishingService;
import kb.hackathon.ssh.global.util.ApiUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PhishingController {

    private final PhishingService phishingService;

    public PhishingController(PhishingService phishingService) {
        this.phishingService = phishingService;
    }

    // 단순 응답 포맷터 (ApiUtils 대체)
    private Map<String, Object> ok(Object data) {
        return Map.of("success", true, "data", data);
    }
    private Map<String, Object> err(String message, HttpStatus status) {
        return Map.of(
                "success", false,
                "data", null,
                "error", Map.of(
                        "message", message,
                        "status", status.value()
                )
        );
    }

    @GetMapping("/lookup")
    public ResponseEntity<?> lookup(@RequestParam String type, @RequestParam String q) {
        final String t = type.toLowerCase();
        if (!t.equals("phone") && !t.equals("account")) {
            return ResponseEntity.badRequest().body(err("type=phone|account", HttpStatus.BAD_REQUEST));
        }

        final String normalized = t.equals("phone")
                ? Phishing.Phone.normalize(q)
                : Phishing.Account.normalize(q);

        final String errMsg = t.equals("phone")
                ? Phishing.Phone.validate(normalized)
                : Phishing.Account.validate(normalized);

        if (errMsg != null) {
            return ResponseEntity.badRequest().body(err(errMsg, HttpStatus.BAD_REQUEST));
        }

        System.out.printf("[LOOKUP] type=%s, raw=%s, normalized=%s%n", t, q, normalized);

        return ResponseEntity.ok(ok(phishingService.lookup(t, normalized)));
    }

    @GetMapping("/reports/recent")
    public ResponseEntity<?> recent() {
        return ResponseEntity.ok(ok(phishingService.recentTop5()));
    }

    @PostMapping("/reports")
    public ResponseEntity<?> report(@RequestBody ReportReq req) {
        if (req == null || req.type == null || req.type.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiUtils.error("type은 필수입니다.", HttpStatus.BAD_REQUEST));
        }
        String type = req.type.toLowerCase();
        if (!type.equals("phone") && !type.equals("account")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiUtils.error("type은 phone|account 만 허용", HttpStatus.BAD_REQUEST));
        }

        String raw = (req.value != null && !req.value.isBlank()) ? req.value : req.normalized;
        if (raw == null || raw.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiUtils.error("value(또는 normalized)는 필수입니다.", HttpStatus.BAD_REQUEST));
        }

        String normalizedUsed = type.equals("phone")
                ? Phishing.Phone.normalize(raw)
                : Phishing.Account.normalize(raw);

        String err = type.equals("phone")
                ? Phishing.Phone.validate(normalizedUsed)
                : Phishing.Account.validate(normalizedUsed);
        if (err != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiUtils.error(err, HttpStatus.BAD_REQUEST));
        }

        phishingService.report(type, normalizedUsed);

        return ResponseEntity.ok(ApiUtils.success(true));
    }
    static class ReportReq {
        public String type;
        public String value;
        public String normalized;
    }

    private String normalizePhone(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[^0-9]", "");
    }

    private String normalizeAccount(String raw) {
        if (raw == null) return "";
        return raw.replaceAll("[^0-9]", "");
    }

    private String validatePhone(String digits) {
        if (digits.length() < 9 || digits.length() > 12) return "전화번호 형식이 올바르지 않습니다.";
        return null;
    }

    private String validateAccount(String digits) {
        if (digits.length() < 8 || digits.length() > 16) return "계좌번호 형식이 올바르지 않습니다.";
        return null;
    }
}

