package NTUBurger.Roles;

// Status is a nested class and any enum declared = static by default
// enum -> Strictly enforces the states here, meaning any typos of state = compile error
public enum CustomerStatus { WAITING, SEATED, SERVED, READY_TO_PAY, PAID, ABANDONED }