package in.neer24.neer24.CustomObjects;

import java.util.HashMap;

/**
 * Created by abhmishr on 12/26/17.
 */

public class RecurrenceCart {

    public static HashMap<Can,Integer> cartList=new HashMap<Can,Integer>();

    RecurrenceCart(){

    }

    public static HashMap<Can,Integer> getCartList(){
        return cartList;
    }

    public static void addItemsToCarts(Can can){

        for (Can c:cartList.keySet()) {
            if(c.getCanID() == can.getCanID()){
                int quantity=cartList.get(c);
                cartList.put(c,quantity+1);
                return;
            }
        }
        cartList.put(can,1);
    }

    public static boolean deleteItemsFromCarts(Can can){

        for (Can c:cartList.keySet()) {
            if(c.getCanID() == can.getCanID()){
                int quantity=cartList.get(c);
                if(quantity==1) {
                    cartList.remove(c);
                    return false;
                }else {
                    cartList.put(c,quantity-1);
                    return  true;
                }
            }
        }
        return false;
    }

    public static int getQuantityForSelectedItem(Can can){

        for (Can c:cartList.keySet()) {
            if(c.getCanID() == can.getCanID())
                return cartList.get(c);
        }
        return 0;
    }

    public static String getCanNameByPosition(int position){
        int count=0;
        for (Can c:cartList.keySet()) {
            if(count==position){
                return c.getName();
            }
            count=count+1;
        }
        return "hello";
    }

    public static double getCanPriceByPosition(int position){
        int count=0;
        for (Can c:cartList.keySet()) {
            if(count==position){
                return c.getPrice();
            }
            count=count+1;
        }
        return 0;
    }

    public static Can getCanByPosition(int position){
        int count=0;
        for (Can c:cartList.keySet()) {
            if(count==position){
                return c;
            }
            count=count+1;
        }
        return null;
    }

    public static double getTotalCanPriceByPosition(Can can){
        double price=0;
        for (Can c:cartList.keySet()) {
            if(c.getCanID() == can.getCanID()){
                price=c.getPrice();
                int quantity=cartList.get(c);
                double totalPrice =  quantity*price;
                if(c.getUserWantsNewCan() == 1){
                    totalPrice = totalPrice + (c.getNewCanPrice() * quantity);
                }
                return totalPrice;
            }
        }
        return 0;
    }

    public static void addItemsToCartsFromCheckoutPage(Can can){
        for (Can c:cartList.keySet()) {
            if(c.getCanID() == can.getCanID()){
                int quantity=cartList.get(c);
                cartList.put(c,quantity+1);
                return;
            }
        }
    }

    public static void deleteItemsToCartsFromCheckoutPage(Can can){
        for (Can c:cartList.keySet()) {
            if(c.getCanID() == can.getCanID()){
                int quantity=cartList.get(c);
                if(quantity==1){
                    cartList.remove(c);
                }else {
                    cartList.put(c, quantity - 1);
                }
                return;
            }
        }
    }


}
