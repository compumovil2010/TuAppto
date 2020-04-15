package negocio;

public class Owner extends User {

    public Owner(String email, String password, String name, String secondname, int phone) {
        super(email, password, name, secondname, phone);
    }

    public Owner() {
        super();
    }

}
