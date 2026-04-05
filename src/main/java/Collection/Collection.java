/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
    
    public void addBook(String title, String author, Member donatedBy, String language, String isbn){
        items.add(new Book(title, author, donatedBy, language, isbn));
    
    }
    public void addDVD(String title, String director, Member donatedBy, String language, String[] audioLanguages ){
        items.add(new DVD(title, director, donatedBy, language, audioLanguages));
        
    }
    public void addMagazine(String title, String language, Member donatedBy, String publisher, String issueNumber){
        items.add(new Magazine(title, language, donatedBy, publisher, issueNumber));
    }
    
    public ArrayList<Item> searchItems(String searchTerm){
        ArrayList<Item> results = new ArrayList<>();
        for(Item item : items){
            if(item.getTitle().toLowerCase().contains(searchTerm.toLowerCase())){
                results.add(item);
            }
        }
        return results;
    }
    
    public Item getItem(String title){
        for(Item item : items){
            if(item.getTitle().equals(title)){
                return item;
            }
        }
        return null;
    }
    
    public void removeItem(Item item){
        items.remove(item);
    }
    
    public ArrayList<Item> getItems(){
        return items;
    }
}
