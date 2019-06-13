package oditek.com.tuurbus.adapter.dataholder;

/**
 * Created by prakash on 15/05/17.
 */

public class CountryObject {

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryDialCode() {
        return CountryDialCode;
    }

    public void setCountryDialCode(String countryDialCode) {
        CountryDialCode = countryDialCode;
    }

    private String countryName;
    private String countryCode;
    private String CountryDialCode;

    public CountryObject(String countryName, String countryCode, String CountryDialCode){
        this.countryName=countryName;
        this.countryCode=countryCode;
        this.CountryDialCode=CountryDialCode;
    }

    @Override
    public String toString() {
        return this.countryName;
    }

}
