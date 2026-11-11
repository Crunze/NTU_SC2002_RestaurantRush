package NTUBurger.Restaurant;

// Record of Customer satisfaction
// 1 Customer = 1 Record
// For event logging via displayController

public record CustomerSatisfaction (int customerId, int initSat, int afterSat){}
