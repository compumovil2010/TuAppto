package negocio;

import java.util.List;

public class Owner extends User {

    private List<String> properties;

    public Owner() {
        super();
    }

    public List<String> getProperties() {
        return properties;
    }

    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

}
