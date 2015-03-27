package sk42.ru.mt;

import java.util.ArrayList;

public class Order {
    Float total;
    String number;
    int index;
    public ArrayList<Product> products;


    Order(){
        number = "";
        products = new ArrayList<Product>();
        total = 0f;
    }


    public void copy(Order source){

        number = source.number;
        total = source.total;
        products.clear();
        for (int i = 0; i < source.products.size(); i++){
            Product p = new Product();
            p.copy(source.products.get(i));
            products.add(p);
        }
        setTotal();

    }
    public void setTotal() {
        total = (float) 0;
        for(int i = 0; i < products.size(); i++){
            products.get(i).setTotal();
            total += products.get(i).total;
        }
    }

    public Product getRow(int rownum){
        if (products.size() > rownum) {
            return products.get(rownum);
        }
        else return new Product();
    }

}



