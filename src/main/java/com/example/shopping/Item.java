package com.example.shopping;

import java.util.Objects;

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

    public String getName() {
        return this.name;
    }
    public int price(){
        return price;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return price == item.price && Objects.equals(name, item.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }


}
