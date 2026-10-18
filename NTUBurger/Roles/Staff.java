package NTUBurger.Roles;

// Just for tracking ID. .getClass.getSimpleName() returns the Class Name. Thus no need getRole
// And probably future tracking of no. of chef/waiter/cashier

public abstract class Staff {

    private final int id;

    public Staff(int id){
        this.id = id;
    }

    public int getId(){ return id; }

}
