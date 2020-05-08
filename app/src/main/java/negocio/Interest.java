package negocio;

public class Interest {
    public String owner;
    public String clientId;
    public String clientName;
    public String clientSecondName;
    public Long clientPhone;
    public String clientEmail;
    public String clientImagePath;
    public String id;
    public String property;

    public Interest() {
    }

    public Interest(String owner, String clientId, String clientName, String clientSecondName, Long clientPhone, String clientEmail, String clientImagePath, String id, String property) {
        this.owner = owner;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientSecondName = clientSecondName;
        this.clientPhone = clientPhone;
        this.clientEmail = clientEmail;
        this.clientImagePath = clientImagePath;
        this.id = id;
        this.property = property;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientSecondName() {
        return clientSecondName;
    }

    public void setClientSecondName(String clientSecondName) {
        this.clientSecondName = clientSecondName;
    }

    public Long getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(Long clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientImagePath() {
        return clientImagePath;
    }

    public void setClientImagePath(String clientImagePath) {
        this.clientImagePath = clientImagePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
