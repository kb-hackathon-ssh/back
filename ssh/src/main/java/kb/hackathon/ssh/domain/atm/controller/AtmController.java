package kb.hackathon.ssh.domain.atm.controller;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import kb.hackathon.ssh.domain.atm.dto.AtmDto;
import kb.hackathon.ssh.domain.atm.service.AtmService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/atms")
public class AtmController {

    private final AtmService atmService;

    public AtmController(AtmService atmService) {
        this.atmService = atmService;
    }

    @GetMapping(value="/nearby", produces="application/json")
    public ResponseEntity<List<AtmDto>> nearby(
            @RequestParam @DecimalMin(value="-90") @DecimalMax("90") double lat,
            @RequestParam @DecimalMin(value="-180") @DecimalMax("180") double lng,
            @RequestParam(required=false) @Min(100) @Max(5000) Integer radius,
            @RequestParam(defaultValue="전체") String brand,
            @RequestParam(required=false) String q,
            @RequestParam(required=false) Double swLat, @RequestParam(required=false) Double swLng,
            @RequestParam(required=false) Double neLat, @RequestParam(required=false) Double neLng) {
        return ResponseEntity.ok(
                atmService.findNearby(lat, lng, radius, brand, q, swLat, swLng, neLat, neLng)
        );
    }
}