package pw.adithya.hawkerapp;

public class ParsingStructure {
    private String name;
    private String description;
    private String coordinates;
    private float lat, lon;

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
        if(this.name.trim().equalsIgnoreCase("Elimbah Creek (east)"))
            System.out.println(coordinates);
    }
}
