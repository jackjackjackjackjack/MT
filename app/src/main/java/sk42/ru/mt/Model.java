package sk42.ru.mt;


import android.util.Log;

import org.ksoap2.serialization.SoapObject;


public class Model {


    public Order currentOrder;
    public Product currentProduct;
    private int currentStore;
    private final static String login = "web_client";
    private final static String password = "webwebclient";

    private Orders orders;
    /*
    private int currentProduct;
    private int currentOder;
    */
    private boolean QuerySent;
    private boolean IOError;
    private static String deviceCode;


    private static Model instance;

    private Model(){

    }


    public static Model getInstance(){
        if (null == instance){
            instance = new Model();
            instance.orders = new Orders();
        }
        return instance;
    }

    public static String getLoginString(){
        return login + ":" + password;
    }



    public static Product findProductInCurrentOrder(String barcode){

        Product result = null;
        try {
            for (int i = 0; i < Model.getCurrentOrder().products.size(); i++) {

                Product p = Model.getCurrentOrder().products.get(i);
                if (p.barcode.equals(barcode)) {
                    result = p;
                    break;
                }
            }
        }
        catch (Throwable e){
            MyApp.showError("findProduct", e.toString());
            Log.e("Model", e.toString());
        }

        return result;
    }


//    static public void deleteCurrentProduct(){
//        String barcode = getCurrentProduct().barcode;
//        for(int i = 0; i < getCurrentOrder().products.size(); i++){
//
//            if(getCurrentOrder().products.get(i).barcode.equals(barcode)){
//                getCurrentOrder().products.remove(i);
//                return;
//            }
//
//        }
//        //getInstance().currentProduct = new Product();
//
//    }


    public static Orders getOrders(){
        return Model.getInstance().orders;
    }


    public static Order getCurrentOrder(){
        return getInstance().currentOrder;
    }


    public static Product getCurrentProduct(){
        return getInstance().currentProduct;
    }

    public static Order addNewOrder(){
        getInstance().currentOrder = new Order();
        return getCurrentOrder();
    }

    public static Product addNewProduct(){
        Product p = new Product();
        getInstance().currentProduct = p;
        return p;
    }

    public static void updateCurrentOrder(){
        Order order = Model.getCurrentOrder();
        order.total = 0f;
        for(int i = 0; i < order.products.size(); i++){
            if (order.products.get(i).totalQtyBuy == 0)
            {
                order.products.remove(i);
                i = 0;
            }
            else
                order.total += order.products.get(i).total;
        }
    }
    public static void updateCurrentProduct(){
        Product p = Model.getCurrentProduct();
        p.setTotal();
    }


    public static String getDeviceCode(){

        return deviceCode;

    }

    public static String serializeOrderProductsToString() {
        String ss = "";
        for(int p = 0; p < getCurrentOrder().products.size(); p++){
            Product cp = getCurrentOrder().products.get(p);
            for (int s = 0; s < cp.stores.size(); s++) {
                Store cs = cp.stores.get(s);
                if (cs.qtybuy > 0) {
                    ss+= cp.barcode + ";" + cs.name + ";" + cs.charact + ";" + cs.qtybuy.toString() + ";" + cs.price.toString() + ";#";
                }
            }
        }
        return ss;
    }

    public static boolean QuerySent(){
        Log.wtf("Model", "QuerySent? " + Model.getInstance().QuerySent);
        return Model.getInstance().QuerySent;
    }

    public static void setQueryAnswered()
    {

        Log.wtf("Model", "setQueryAnswered called");
        Model.getInstance().QuerySent = false;
        Model.setIOError(false);
    }

    public static void setQuerySent()
    {
        Log.wtf("Model", "setQuerySent called");
        Model.getInstance().QuerySent = true;
    }

    public static boolean isIOError() {
        return Model.getInstance().IOError;
    }

    public static void setIOError(boolean IOError) {
        Model.getInstance().IOError = IOError;
    }

    public static void clearErrors(){
        Model.setIOError(false);
    }

    public static void setCurrentOrder(int index) {

        //скопировать ордер из общего массива в отдельный объект currentorder
        Order newOrder = new Order();
        newOrder.copy(getOrders().getOrderByIndex(index));
        getInstance().currentOrder = newOrder;
    }

    public static void saveCurrentProduct(){
        Order r = getCurrentOrder();
        Product p = findProductInCurrentOrder(getCurrentProduct().barcode);

        if (p != null)
            p.copy(getCurrentProduct());
        else {
            Product newp = new Product();
            newp.copy(getCurrentProduct());
            r.products.add(r.products.size(), newp);
        }
        updateCurrentOrder();
    }


    public static void saveCurrentOrder() {
        String number = getCurrentOrder().number;
        if(number.isEmpty()) return;
        for(int i = 0; i < getOrders().size(); i++){
            Order existingOrderToSave = getOrders().getOrderByIndex(i);
            if (existingOrderToSave.number.equals(number))
            {
                existingOrderToSave.copy(getCurrentOrder());
                return;
            }
        }
        Order newOrderToSave = new Order();
        newOrderToSave.copy(getCurrentOrder());
        getOrders().add(newOrderToSave);
    }

    public static Store getCurrentStore() {
        int index = getInstance().currentStore;
        return getCurrentProduct().stores.get(index);
    }
    public static int getCurrentStoreIndex() {
        return getInstance().currentStore;
    }

    public static void setCurrentStore(int i) {
        getInstance().currentStore = i;
    }

    public static void setCurrentProduct(Product p) {
        getInstance().currentProduct = new Product();
        getInstance().currentProduct.copy(p);
    }
    public static void setCurrentProduct(int index) {
        Product p =  getCurrentOrder().getRow(index);
        getInstance().currentProduct.copy(p);
    }

    public static String readProductDataFromSOAP(SoapObject soap) {
        Model.setQueryAnswered();

        if (soap == null) {

            return "Ошибка поиска штрихкода в базе 1С, попробуйте еще раз!";
        }
        int r = Integer.parseInt(soap.getProperty("Result").toString());
        if (r > 0) {
            return soap.getProperty("ResultDescription").toString();

        }
        int nstores = Integer.parseInt(soap.getProperty("NumberOfStores").toString());
        if (nstores == 0) {
            return "Товара нет в наличии!";
        }

        Product p = Model.addNewProduct();
        p.barcode = soap.getProperty("Barcode").toString().trim();
        p.price = Float.parseFloat(soap.getProperty("Price").toString());
        p.code = soap.getProperty("Code").toString();
        p.name = soap.getProperty("Description").toString();
        p.image = soap.getProperty("Image").toString();
        p.stores.clear();
        if (nstores > 0) {
            for (int i = 0; i < nstores; i++) {
                Store s = new Store();
                SoapObject t = (SoapObject) soap.getProperty(i + 9);
                String name = t.getProperty("Store").toString();
                Float qty = Float.parseFloat(t.getProperty("Quantity").toString());

                s.price = Float.parseFloat(t.getProperty("Price").toString());
                s.charact = t.getProperty("Characteristic").toString();
                s.name = name;
                s.qty = qty;
                p.addStore(s);
            }
        }
        return "";
    }

    public static String readSaveResultFromSOAP(SoapObject soap){
        Model.setQueryAnswered();
        if(soap == null) {

            return "Ошибка (Null) при передаче документа в базу 1С, попробуйте еще раз!";
        }

        int res = Integer.parseInt(soap.getProperty("Result").toString());
        String resultDescription = soap.getProperty("ResultDescription").toString();
        String number = soap.getProperty("Number").toString();
        if (res  == 0) {
            Model.getCurrentOrder().number = number;
            return "";
        }
        else
        {
            return resultDescription;
        }
    }

    public static void setDeviceCode(String deviceCode) {
        Model.deviceCode = deviceCode;
    }
}



