package CreatedObjects;


public class Ingredient {
    
    private String name;
    private String quantity;
    private String expiration;

    public Ingredient(String name, String quantity, String expiration) {
        this.name = name;
        this.quantity = quantity;
        this.expiration = expiration;
    }

    public String getName() {
        return name;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getExpiration() {
        return expiration;
    }
}
