package NTUBurger.Restaurant;

// Record of Receipts
// 1 Payment = 1 Receipt
// For event logging via displayController

public record Receipt (int customerId, int priceCents, int tableNo) {}
