package NTUBurger.Restaurant;
// Status is a class and any enum declared = static by default
// enum -> Strictly enforces the states here, meaning any typos of state = compile error
// AKA Represent a group of Constants

public enum OrderStatus{ PLACED, COOKING, READY, SERVED, PAID, CANCELLED }