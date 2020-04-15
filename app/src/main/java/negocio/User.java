package negocio;

public class User {
    String email;
    String password;
    String Name;
    String secondname;
    int phone;

    public User(String email, String password, String name, String secondname, int phone) {
        this.email = email;
        this.password = password;
        Name = name;
        this.secondname = secondname;
        this.phone = phone;
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
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }
}
