package oditek.com.tuurbus.adapter.dataholder;

public class HomeModel {
    public int image = 0;
    public String name = "", add = "", rate = "", tour_id = "", car_id = "", imageUrl = "";

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }
}

