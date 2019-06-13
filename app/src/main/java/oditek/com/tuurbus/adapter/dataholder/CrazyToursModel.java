package oditek.com.tuurbus.adapter.dataholder;

public class CrazyToursModel {
    public int image;
    public String name = "",price ="",details = "", currSymbol = "", currCode = "", imageUrl = "", tourId = "",
    phone = "", email = "", expiryDate = "";


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
