package oditek.com.tuurbus.adapter.dataholder;

public class MyWishlistModel {
    String name;
    String date;
    String image;
    String wishid;
    int rating;

    public String getWishid() {
        return wishid;
    }

    public void setWishid(String wishid) {
        this.wishid = wishid;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public MyWishlistModel(String name, String date, String image, int rating,String wishid ) {

        this.name = name;
        this.date = date;
        this.image = image;
        this.rating = rating;
        this.wishid = wishid;
    }
}
