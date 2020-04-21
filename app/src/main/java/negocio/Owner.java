package negocio;

public class Owner extends User {

    public Owner(String email, String password, String name, String secondname, Long phone, String imagePath) {
        super(email, password, name, secondname, phone, imagePath);
    }

    public Owner() {
        super();
    }

}
