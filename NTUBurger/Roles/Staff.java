package NTUBurger.Roles;

public abstract class Staff {
    
    public enum Role { WAITER, CASHIER, CHEF }

    private final int id;
    private final Role role;

    public Staff(int id, Role role){
        this.id = id;
        this.role = role;
    }

    public int getId(){ return id; }
    public Role getRole(){ return role; }

    public String waiting(){
        return "Wait";
    }

    public abstract String describeToDos();
}
