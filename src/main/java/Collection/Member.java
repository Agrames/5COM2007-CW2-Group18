/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.member;
import java.util.ArrayList;
/**
 *
 * @author to23abg
 */


public class Member {

    private String name;
    private String address;
    private String email;
    private int donatedQty;

    private ArrayList<Item> borrowing;
    private ArrayList<Item> donatedItems;

    public Member(String name, String address, String email, int donatedQty) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.donatedQty = donatedQty;

        borrowing = new ArrayList<>();
        donatedItems = new ArrayList<>();
    }

    public int borrowingQty() {
        return borrowing.size();
    }

    private int maxBorrow() {
        return Math.min(5, donatedQty);
    }

    public void lend(Item item) {
        if (item != null && item.isAvailable()) {
            if (borrowingQty() < maxBorrow()) {
                borrowing.add(item);
                item.loanTo(this);
            } else {
                System.out.println("Borrow limit reached!");
            }
        }
    }

    public void returnItem(Item item) {
       if (borrowing.contains(item)) {
           borrowing.remove(item);
           item.returnLoan();
       }
    }

    public void addDonation(Item item) {
        if (item != null) {
             donatedItems.add(item);
             donatedQty++;
        }
    }

    public ArrayList<Item> getLoanItems() {
         return borrowing;
    }

    public ArrayList<Item> getDonatedItems() {
         return donatedItems;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public int getDonatedQty() { return donatedQty; }

    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
}
