package negocio;

public class Interest {
    public String owner;
    public String client;
    public String id;
    public String property;

    public Interest() {
    }

    public Interest(String owner, String client, String id, String property) {
        this.owner = owner;
        this.client = client;
        this.id = id;
        this.property = property;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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
