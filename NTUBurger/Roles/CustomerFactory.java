package NTUBurger.Roles;

// Decides which kind of customer arrives. Customers are numbered in arrival order, so the id is enough to choose
// Stage 1: everyone is a Regular customer

public final class CustomerFactory {

    private CustomerFactory() { }

    public static Customer create(int id){
        return new CustomerRegular(id);
    }
}
