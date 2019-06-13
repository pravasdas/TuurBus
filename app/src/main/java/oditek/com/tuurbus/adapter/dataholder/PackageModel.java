package oditek.com.tuurbus.adapter.dataholder;

import org.json.JSONArray;

public class PackageModel {
    public int image;
    public String name = "", location = "", tourtype = "", days = "", nights = "", speciality = "", starsCount = "",
                  imageUrl = "", descp = "", currCode = "", currSymbol = "", tourId = "", latitude = "", longitude = "",
                  appModule = "";
    public JSONArray priceArr;


    public String getTourtype() {
        return tourtype;
    }

    public void setTourtype(String tourtype) {
        this.tourtype = tourtype;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getNights() {
        return nights;
    }

    public void setNights(String nights) {
        this.nights = nights;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
