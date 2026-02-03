package com.example.shopping;
//
public class Item {
    String name;
    int price;



    public Item() {

    }
    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }



    public void add(String name) {
        this.name = name;
    }

    public String getName(String name) {
        return name;
    }
    public int price(){
        return price;
    }


}
