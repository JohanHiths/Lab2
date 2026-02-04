package com.example;

import com.example.shopping.Item;
import com.example.shopping.ShoppingCart;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//
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
    @Test
    @DisplayName("Beräknar totala priset")
    void calculateTotalPrice() {
        ShoppingCart cart = new ShoppingCart();

        assertThat(cart.total()).isEqualTo(0.0);
    }
    @Test
    void cartTotalIsSumOfItemPrices_singleItem() {
        ShoppingCart cart = new ShoppingCart();
        Item item = new Item("Choklad", 10);

        cart.add(item);

        assertThat(cart.total()).isEqualTo(10);
    }
    @Test
    void cartTotalIsSumOfItemPrices_multipleItems() {
        ShoppingCart cart = new ShoppingCart();
        Item item1 = new Item("Choklad", 10);
        Item item2 = new Item("Kaffe", 5);
        Item item3 = new Item("Te", 3);



        cart.add(item1);
        cart.add(item2);
        cart.add(item3);

        assertThat(cart.total()).isEqualTo(18);

    }

    @Test
    void smallSale_onEmptyCart_isZero() {
        ShoppingCart cart = new ShoppingCart();

        assertThat(cart.smallSale()).isEqualTo(0);
    }
    @Test
    void smallSale_applies20PercentDiscount(){
        ShoppingCart cart = new ShoppingCart();
        Item item1 = new Item("Choklad", 10);
        Item item2 = new Item("Kaffe", 5);

        cart.add(item1);
        cart.add(item2);

        assertThat(cart.smallSale()).isEqualTo(12);

    }
    @Test
    void mediumSale_applies30PercentDiscount(){
        ShoppingCart cart = new ShoppingCart();
        Item item1 = new Item("Pizza", 100);
        Item item2 = new Item("Cola", 20);

        cart.add(item1);
        cart.add(item2);

        assertThat(cart.mediumSale()).isEqualTo(84);

    }
    @Test
    void largeSale_applies50PercentDiscount(){
        ShoppingCart cart = new ShoppingCart();
        Item item1 = new Item("Car", 50000);
        Item item2 = new Item("Bike", 10000);

        cart.add(item1);
        cart.add(item2);

        assertThat(cart.largeSale()).isEqualTo(30000);
    }



    }


