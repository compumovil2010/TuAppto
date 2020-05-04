package negocio;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    List<String> favoritos;
    List<String> interes;

    public Client(String email, String password, String name, String secondname, Long phone, String imagePath) {
        super(email, password, name, secondname, phone, imagePath);

        favoritos = new ArrayList<>();
        interes = new ArrayList<>();
    }

    public Client() {
        super();
        favoritos = new ArrayList<>();
        interes = new ArrayList<>();
    }

    public List<String> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<String> favoritos) {
        this.favoritos = favoritos;
    }

    public List<String> getInteres() {
        return interes;
    }

    public void setInteres(List<String> interes) {
        this.interes = interes;
    }
}
