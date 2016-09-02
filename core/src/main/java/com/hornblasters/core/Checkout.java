package com.hornblasters.core;

public class Checkout {
    public final Cart cart;
    public final Address address;
    public final Payment payment;
    public Shipping shipping = null;

    public Checkout(Cart cart, Address address, Payment payment) {
        this.cart = cart;
        this.address = address;
        this.payment = payment;
    }

    public boolean ready() {
        return shipping != null;
    }

    public double getTotal() {
        return Math.scalb(cart.subtotal * address.getTaxRate() + shipping.rate, 2);
    }
}
