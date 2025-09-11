package kb.hackathon.ssh.domain.phishing.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PhishingDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private String id;
        private String type;
        private String value;
        private int reports;
        private String lastReported;
        private String source;
        private String risk;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LookupResponse {
        private String ok = "true";
        private Map<String, String> query;
        private Map<String, String> meta;
        private List<Item> items = new ArrayList<>();
    }
}