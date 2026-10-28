package NTUBurger.Roles;

import NTUBurger.BaseSetting;

// Decides which kind of customer arrives. Customers are numbered in arrival order, so the id is enough to choose
// The ids of the special customers live in BaseSetting, so the TA can change them in one place

public final class CustomerFactory {

    private CustomerFactory() { }

    public static Customer create(int id){
        if (BaseSetting.VIP_CUSTOMER_IDS.contains(id)){ return new CustomerVIP(id); }
        if (BaseSetting.CRITIC_CUSTOMER_IDS.contains(id)){ return new CustomerCritic(id); }
        return new CustomerRegular(id);
    }
}
