package com.mycompany.com2007groupcoursework;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class ISATest {

    @Test
    public void testMagazineAttributes() {
        Member donor = new Member("Alice", "1 Road", "a@b.com", 1);
        Magazine mag = new Magazine("Nature", "English", donor, "Springer", "Vol.42");
        assertEquals("Nature", mag.getTitle());
        assertEquals("Springer", mag.getPublisher());
        assertEquals("Vol.42", mag.getIssueNumber());
        assertTrue(mag.isAvailable());
    }

    @Test
    public void testMagazineLoanReturn() {
        Member donor = new Member("Alice", "1 Road", "a@b.com", 2);
        Member borrow = new Member("Bob", "2 Road", "b@b.com", 1);
        Magazine mag = new Magazine("Nature", "English", donor, "Springer", "Vol.42");
        mag.loanTo(borrow);
        assertFalse(mag.isAvailable());
        mag.returnLoan();
        assertTrue(mag.isAvailable());
    }

    @Test
    public void testSearchFindsMatch() {
        Collection col = new Collection();
        col.addBook("The Shining", "Stephen King", null, "English", "123");
        col.addBook("Shindig Weekly", "Jane Doe", null, "English", "456");
        ArrayList<Item> results = col.searchItems("shin");
        assertEquals(2, results.size());
    }

    @Test
    public void testSearchCaseInsensitive() {
        Collection col = new Collection();
        col.addBook("The Shining", "Stephen King", null, "English", "123");
        ArrayList<Item> results = col.searchItems("SHINING");
        assertEquals(1, results.size());
    }

    @Test
    public void testSearchNoMatch() {
        Collection col = new Collection();
        col.addBook("The Shining", "Stephen King", null, "English", "123");
        assertTrue(col.searchItems("xyz").isEmpty());
    }

    @Test
    public void testBorrowingLimit() {
        // donatedQty=1 means maxBorrow()=1, so only one item can be borrowed
        Member m = new Member("Alice", "1 Road", "a@b.com", 1);
        Book b1 = new Book("Book1", "Auth", null, "English", "111");
        Book b2 = new Book("Book2", "Auth", null, "English", "222");
        m.lend(b1);
        assertEquals(1, m.borrowingQty());
        m.lend(b2); // should be blocked — limit already reached
        assertEquals(1, m.borrowingQty());
    }

    @Test
    public void testClearDonator() {
        Member donor = new Member("Alice", "1 Road", "a@b.com", 1);
        Book b = new Book("Book", "Auth", donor, "English", "123");
        b.clearDonator();
        assertNull(b.getDonator());
    }

    @Test
    public void testAddMagazineToCollection() {
        Collection col = new Collection();
        Member donor = new Member("Alice", "1 Road", "a@b.com", 1);
        col.addMagazine("Nature", "English", donor, "Springer", "Vol.42");
        assertNotNull(col.getItem("Nature"));
        assertEquals(1, col.getItems().size());
    }

    @Test
    public void testMagazineFoundInSearch() {
        Collection col = new Collection();
        col.addMagazine("Nature Weekly", "English", null, "Springer", "Vol.1");
        col.addBook("Nature of Code", "Shiffman", null, "English", "999");
        ArrayList<Item> results = col.searchItems("nature");
        assertEquals(2, results.size());
    }

    @Test
    public void testMagazineUpdateAttributes() {
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
    public void testMemberReturnUpdatesState() {
        Member m = new Member("Alice", "1 Road", "a@b.com", 2);
        Magazine mag = new Magazine("Nature", "English", null, "Springer", "Vol.1");
        m.lend(mag);
        assertEquals(1, m.borrowingQty());
        assertFalse(mag.isAvailable());
        m.returnItem(mag);
        assertEquals(0, m.borrowingQty());
        assertTrue(mag.isAvailable());
    }
}
