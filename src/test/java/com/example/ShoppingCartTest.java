package com.example;

import com.example.shopping.Item;
import com.example.shopping.ShoppingCart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class ShoppingCartTest {


    @Test
    @DisplayName("Ska kunna lägga till ett item i vagnen")
    void addItem() {
        Item item = new Item();

        item.add("Choklad");


        assertThat(item.getName("Choklad")).isEqualTo("Choklad");

    }
    @Test
    @DisplayName("Ska kunna ta bort varor från kundvagnen")
    void removeItem() {
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item();

        cart.add(item);
        cart.remove(item);

        assertThat(cart.size()).isEqualTo(0);

    }

}
