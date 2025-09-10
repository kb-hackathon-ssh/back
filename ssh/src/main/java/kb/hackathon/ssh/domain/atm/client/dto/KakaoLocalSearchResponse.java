package kb.hackathon.ssh.domain.atm.client.dto;

import java.util.List;

public class KakaoLocalSearchResponse {
    private List<Document> documents;
    private Meta meta;

    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }
    public Meta getMeta() { return meta; }
    public void setMeta(Meta meta) { this.meta = meta; }

    public static class Document {
        private String id;
        private String place_name;
        private String road_address_name;
        private String address_name;
        private String x;
        private String y;
        private String category_group_code;
        private String category_name;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getPlace_name() { return place_name; }
        public void setPlace_name(String place_name) { this.place_name = place_name; }
        public String getRoad_address_name() { return road_address_name; }
        public void setRoad_address_name(String road_address_name) { this.road_address_name = road_address_name; }
        public String getAddress_name() { return address_name; }
        public void setAddress_name(String address_name) { this.address_name = address_name; }
        public String getX() { return x; }
        public void setX(String x) { this.x = x; }
        public String getY() { return y; }
        public void setY(String y) { this.y = y; }
        public String getCategory_group_code() { return category_group_code; }
        public void setCategory_group_code(String category_group_code) { this.category_group_code = category_group_code; }
        public String getCategory_name() { return category_name; }
        public void setCategory_name(String category_name) { this.category_name = category_name; }
    }

    public static class Meta {
        private Integer total_count;
        private Integer pageable_count;
        private Boolean is_end;

        public Integer getTotal_count() { return total_count; }
        public void setTotal_count(Integer total_count) { this.total_count = total_count; }
        public Integer getPageable_count() { return pageable_count; }
        public void setPageable_count(Integer pageable_count) { this.pageable_count = pageable_count; }
        public Boolean getIs_end() { return is_end; }
        public void setIs_end(Boolean is_end) { this.is_end = is_end; }
    }
}