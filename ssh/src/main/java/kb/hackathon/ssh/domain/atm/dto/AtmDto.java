package kb.hackathon.ssh.domain.atm.dto;

public class AtmDto {
    private String id;
    private String brand;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private AtmType type;

    public AtmDto() {}

    public AtmDto(String id, String brand, String name, String address, double lat, double lng, AtmType type) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.type = type;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }

    public AtmType getType() { return type; }
    public void setType(AtmType type) { this.type = type; }
}