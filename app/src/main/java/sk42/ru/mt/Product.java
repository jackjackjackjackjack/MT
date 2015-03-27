package sk42.ru.mt;

import java.util.ArrayList;


public class Product {
    String code, barcode, name, image;
    Float price, total, totalQtyBuy;

    ArrayList<Store> stores;

    Product(){
        code = "";
        barcode = "";
        name = "";
        image = "";
        totalQtyBuy = 0f;
        total = 0f;
        price = 0f;
        stores = new ArrayList<Store>();
    }

    public void copy(Product p){
        code = p.code;
        name = p.name;
        barcode = p.barcode;
        image = p.image;

        price = p.price;
        totalQtyBuy = p.totalQtyBuy;
        total = p.total;

        stores.clear();

        for (int i = 0; i < p.stores.size(); i++) {
            Store store = new Store();
            store.copy(p.stores.get(i));
            stores.add(store);
        }


    }

    public void addStore(Store s){
        int index = stores.size();
        Store newstore = new Store(index);
        newstore.copy(s);
        stores.add(index, newstore);
    }

    public void setTotal(){
        total = 0f;
        totalQtyBuy = 0f;

        if ( stores.size() != 0) {

            for (int i = 0; i < stores.size(); i++) {
                Store store =  stores.get(i);
                total += store.price * store.qtybuy;
                totalQtyBuy += store.qtybuy;
            }
        }

    }

}

class Store {

    String name;
    String charact;
    Float qty;
    Float qtybuy;
    Float price;

    int index;
    Store(int ind){
        index = ind;
        charact = "";
        name="";
        qtybuy=0f;
        qty=0f;
        price = 0f;
    }
    Store(){
        index = -1;
        charact = "";
        name="";
        qtybuy=0f;
        qty=0f;
        price = 0f;
    }
    void copy(Store s){
        charact = s.charact;
        name = s.name;
        qty = s.qty;
        qtybuy = s.qtybuy;
        index = s.index;
        price = s.price;
    }

}