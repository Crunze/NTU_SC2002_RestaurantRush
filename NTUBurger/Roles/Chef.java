package NTUBurger.Roles;

import NTUBurger.Restaurant.Order;

// Chef

public class Chef extends Staff {

    public Chef(int id){
        super(id, Role.CHEF);
    }

    public void cooking(Order order){
        order.preparing();
    }

    @Override
    public String describeToDos(){
        return "";
    }
    
}
