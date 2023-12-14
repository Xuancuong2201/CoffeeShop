package com.example.coffeeshop.Model;

public class WareHouse {
    int ID,Depot,Used;
    String Name,key,type,Date;

    public WareHouse() {
    }
    public WareHouse(String key, String Date, int depot,int used, int ID, String name, String type) {
        this.key = key;
        this.Date = Date;
        this.Depot = depot;
        this.ID = ID;
        this.Name = name;
        this.type = type;
        this.Used =used;
    }

    public int getUsed() {
        return Used;
    }

    public void setUsed(int used) {
        Used = used;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String Date) {
        Date = Date;
    }

    public int getDepot() {
        return Depot;
    }

    public void setDepot(int depot) {
        Depot = depot;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }




}
