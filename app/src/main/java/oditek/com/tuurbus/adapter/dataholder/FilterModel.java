package oditek.com.tuurbus.adapter.dataholder;

public class FilterModel {
    String name;

    public String getId() {
        return id;
    }

    String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public FilterModel(String name, String id) {

        this.name = name;
        this.id = id;
    }
}
