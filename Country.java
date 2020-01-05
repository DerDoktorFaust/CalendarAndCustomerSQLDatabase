package sample;

public class Country {
    private int countryId;
    private String countryName;

    public Country(int cityId, String cityName){
        this.countryId = cityId;
        this.countryName = cityName;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
