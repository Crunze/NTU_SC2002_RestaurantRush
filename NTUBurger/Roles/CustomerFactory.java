package NTUBurger.Roles;

import NTUBurger.BaseSetting;

// Decides which kind of customer arrives. Customers are numbered in arrival order, so the id is enough to choose
// Stage 2: VIP and Critic arrivals are listed in BaseSetting, everyone else is Regular

public final class CustomerFactory {

    private CustomerFactory() { }

    public static Customer create(int id){
        if (BaseSetting.VIP_CUSTOMER_IDS.contains(id)) return new CustomerVIP(id);
        if (BaseSetting.CRITIC_CUSTOMER_IDS.contains(id)) return new CustomerCritic(id);
        return new CustomerRegular(id);
    }
}
