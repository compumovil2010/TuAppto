package negocio;

import java.util.ArrayList;
import java.util.List;

public class Client extends User {
    List<Integer> favoritos;
    List<Integer> interes;

    public Client(String email, String password, String name, String secondname, int phone) {
        super(email, password, name, secondname, phone);

        favoritos = new ArrayList<>();
        interes = new ArrayList<>();
    }

    public Client() {
        super();
        favoritos = new ArrayList<>();
        interes = new ArrayList<>();
    }

    public List<Integer> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<Integer> favoritos) {
        this.favoritos = favoritos;
    }

    public List<Integer> getInteres() {
        return interes;
    }

    public void setInteres(List<Integer> interes) {
        this.interes = interes;
    }
}
