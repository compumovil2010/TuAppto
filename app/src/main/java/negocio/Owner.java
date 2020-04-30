package negocio;

import java.util.ArrayList;
import java.util.List;

public class Owner extends User {

    List<String> properties;

    public Owner(String email, String password, String name, String secondname, Long phone, String imagePath, List<String>properties) {
        super(email, password, name, secondname, phone, imagePath);

       this.properties = properties;
    }

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
