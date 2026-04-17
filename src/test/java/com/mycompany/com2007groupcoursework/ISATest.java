/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package com.mycompany.com2007groupcoursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

/**
 *
 * @author zs25adm
 */
public class ISATest {

    @Test
    public void testMagazine(){
        // check magazine is created with the right attributes
        Member donor = new Member("Alice", "1 Road", "a@b.com", 1);
        Magazine mag = new Magazine("Nature", "English", donor, "Springer", "Vol.42");
        assertEquals("Nature", mag.getTitle());
        assertEquals("Springer", mag.getPublisher());
        assertEquals("Vol.42", mag.getIssueNumber());
        assertTrue(mag.isAvailable());
    }

    @Test
    public void testMagazineLoanReturn(){
        // loan a magazine and check its not available, then return it
        Member donor = new Member("Alice", "1 Road", "a@b.com", 2);
        Member borrower = new Member("Bob", "2 Road", "b@b.com", 1);
        Magazine mag = new Magazine("Nature", "English", donor, "Springer", "Vol.42");
        mag.loanTo(borrower);
        assertFalse(mag.isAvailable());
        mag.returnLoan();
        assertTrue(mag.isAvailable());
    }

    @Test
    public void testSearchFindsMatch(){
        // search should find both books with shin in the title
        Collection col = new Collection();
        col.addBook("The Shining", "Stephen King", null, "English", "123");
        col.addBook("Shindig Weekly", "Jane Doe", null, "English", "456");
        ArrayList<Item> results = col.searchItems("shin");
        assertEquals(2, results.size());
    }

    @Test
    public void testSearchCaseInsensitive(){
        //search should work regardless of case
        Collection col = new Collection();
        col.addBook("The Shining", "Stephen King", null, "English", "123");
        ArrayList<Item> results = col.searchItems("SHINING");
        assertEquals(1, results.size());
    }

    @Test
    public void testSearchNoMatch(){
        Collection col = new Collection();
        col.addBook("The Shining", "Stephen King", null, "English", "123");
        // should return empty list if nothing matches
        ArrayList<Item> results = col.searchItems("xyz");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testBorrowingLimit(){
        // donatedQty = 1 means max borrow is 1, second book should be blocked
        Member member = new Member("Alice", "1 Road", "a@b.com", 1);
        Book book1 = new Book("Book1", "Auth", null, "English", "111");
        Book book2 = new Book("Book2", "Auth", null, "English", "222");
        member.lend(book1);
        assertEquals(1, member.borrowingQty());
        member.lend(book2);
        assertEquals(1, member.borrowingQty());
    }

    @Test
    public void testClearDonator(){
        Member donor = new Member("Alice", "1 Road", "a@b.com", 1);
        Book book = new Book("Book", "Auth", donor, "English", "123");
        book.clearDonator();
        assertNull(book.getDonator());
    }

    @Test
    public void testAddMagazineToCollection(){
        Collection col = new Collection();
        Member donor = new Member("Alice", "1 Road", "a@b.com", 1);
        col.addMagazine("Nature", "English", donor, "Springer", "Vol.42");
        // check the magazine is actually in the collection
        assertNotNull(col.getItem("Nature"));
        assertEquals(1, col.getItems().size());
    }

    @Test
    public void testMagazineInSearch(){
        // magazines should show up in search results just like books
        Collection col = new Collection();
        col.addMagazine("Nature Weekly", "English", null, "Springer", "Vol.1");
        col.addBook("Nature of Code", "Shiffman", null, "English", "999");
        ArrayList<Item> results = col.searchItems("nature");
        assertEquals(2, results.size());
    }

    @Test
    public void testMagazineSetters(){
        Member donor = new Member("Alice", "1 Road", "a@b.com", 1);
        Magazine mag = new Magazine("Old Title", "English", donor, "OldPub", "Issue1");
        mag.setTitle("New Title");
        mag.setPublisher("NewPub");
        mag.setIssueNumber("Issue2");
        mag.setLanguage("French");
        assertEquals("New Title", mag.getTitle());
        assertEquals("NewPub", mag.getPublisher());
        assertEquals("Issue2", mag.getIssueNumber());
        assertEquals("French", mag.getLanguage());
    }

    @Test
    public void testReturnItem(){
        Member member = new Member("Alice", "1 Road", "a@b.com", 2);
        Magazine mag = new Magazine("Nature", "English", null, "Springer", "Vol.1");
        member.lend(mag);
        assertEquals(1, member.borrowingQty());
        assertFalse(mag.isAvailable());
        // return should update both the member borrowing count and the item status
        member.returnItem(mag);
        assertEquals(0, member.borrowingQty());
        assertTrue(mag.isAvailable());
    }
}
