/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.com2007groupcoursework;

import java.util.ArrayList;

/**
 *
 * @author HP
 */
public class Collection {
    private ArrayList<Item> items;

    public Collection(){
        items = new ArrayList<>();
    }
    // add a book to the collection
    public void addBook(String title, String author, Member donatedBy, String language, String isbn){
        items.add(new Book(title, author, donatedBy, language, isbn));

    }
    // add a dvd to the collection
    public void addDVD(String title, String director, Member donatedBy, String language, String[] audioLanguages ){
        items.add(new DVD(title, director, donatedBy, language, audioLanguages));

    }
    // add a magazine to the collection
    public void addMagazine(String title, String language, Member donatedBy, String publisher, String issueNumber){
        items.add(new Magazine(title, language, donatedBy, publisher, issueNumber));
    }
    // allowing search to be case insensitive, search by partial
    public ArrayList<Item> searchItems(String searchTerm){
        ArrayList<Item> results = new ArrayList<>();
        for(Item item : items){
            if(item.getTitle().toLowerCase().contains(searchTerm.toLowerCase())){
                results.add(item);
            }
        }
        return results;
    }
    // get the first item with exact title match
    public Item getItem(String title){
        for(Item item : items){
            if(item.getTitle().equals(title)){
                return item;
            }
        }
        return null;
    }
    // remove an item from the collection
    public void removeItem(Item item){
        items.remove(item);
    }
    // return all items
    public ArrayList<Item> getItems(){
        return items;
    }
}
