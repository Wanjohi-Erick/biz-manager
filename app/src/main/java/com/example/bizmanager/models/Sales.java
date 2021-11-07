package com.example.bizmanager.models;

public class Sales {
    String date, particulars, commodity, amount;
    public Sales(String date, String particulars, String commodity, String amount) {
        this.date = date;
        this.particulars = particulars;
        this.commodity = commodity;
        this.amount = amount;
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

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }
}
