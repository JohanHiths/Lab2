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
    private int discountedTotal(int percent) {
        return total() * percent / 100;
    }

    public int smallSale() {
        return discountedTotal(80);
    }
    public int mediumSale() {
        return discountedTotal(70);
    }
    public int largeSale() {
        return discountedTotal(50);
    }
}


