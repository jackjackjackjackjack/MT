package sk42.ru.mt;

import java.util.ArrayList;


public class Orders {
    private ArrayList<Order> orders;
    
    Orders(){
        orders = new ArrayList<Order>();
    }



    public Order getOrderByIndex(int index){

        if (index >= orders.size()) return new Order();

        return orders.get(index);

    }

    public void add(Order order){
        orders.add(orders.size(), order);
    }


    public int size() {
        return orders.size();
    }
}
