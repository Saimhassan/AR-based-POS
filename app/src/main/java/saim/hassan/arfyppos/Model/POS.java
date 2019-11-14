package saim.hassan.arfyppos.Model;

public class POS {
    private String name,image;

    public POS(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public POS() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
