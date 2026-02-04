package com.example.shopping;

import java.util.ArrayList;
import java.util.List;
//
public class ShoppingCart {
    private final List<Item> items = new ArrayList<>();

    public void add(Item item) {
        items.add(item);
    }

    public void remove(Item item) {
        items.remove(item);
    }

    public int size() {
        return items.size();
    }


    public int total() {
        return items.stream()
                .mapToInt(Item::price)
                .sum();
    }

    public int smallSale() {
        return total() * 80 / 100;
    }
    public int mediumSale() {
        return total() * 70 / 100;
    }
    public int largeSale() {
        return total() * 50 / 100;
    }
}


