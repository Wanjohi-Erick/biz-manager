package com.example.bizmanager.models;

public class Commodities {
    String ID, name, production_cost, unit_price;

    public Commodities(String id, String name, String production_cost, String unit_price) {
        this.ID = id;
        this.name = name;
        this.production_cost = production_cost;
        this.unit_price = unit_price;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduction_cost() {
        return production_cost;
    }

    public void setProduction_cost(String production_cost) {
        this.production_cost = production_cost;
    }

    public String getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(String unit_price) {
        this.unit_price = unit_price;
    }
}
