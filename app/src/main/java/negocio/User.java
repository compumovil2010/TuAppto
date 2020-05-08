package negocio;

public class User {
    String email;
    String password;
    String name;
    String secondname;
    String imagePath;
    Long phone;

    public User(String email, String password, String name, String secondname, Long phone, String imagePath) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.secondname = secondname;
        this.phone = phone;
        this.imagePath = imagePath;
    }

    public User() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
