package com.example.bizmanager.models;

public class Sales {
    String id, date, particulars, commodity, quantity, unitPrice, totalPrice, paymentMethod, creditStatus;
    public Sales(String id, String date, String particulars, String commodity, String quantity, String unitPrice, String totalPrice, String paymentMethod, String creditStatus) {
        this.id = id;
        this.date = date;
        this.particulars = particulars;
        this.commodity = commodity;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
        this.creditStatus = creditStatus;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setCreditStatus(String creditStatus) {
        this.creditStatus = creditStatus;
    }

    public String getCreditStatus() {
        return creditStatus;
    }
}
