package com.mycompany.com2007groupcoursework;

import java.util.ArrayList;
import java.util.Scanner;

public class UI {
    private Collection collection;
    private MemberList memberList;
    private Scanner scanner;

    public UI(Collection collection, MemberList memberList) {
        this.collection = collection;
        this.memberList = memberList;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        int choice = 0;
        while (choice != 9) {
            System.out.println("\n=== ISA Library System ===");
            System.out.println("1. Search items");
            System.out.println("2. Add item");
            System.out.println("3. Add member");
            System.out.println("4. Search members");
            System.out.println("5. Lend item");
            System.out.println("6. Return item");
            System.out.println("7. Remove item");
            System.out.println("8. Save to file");
            System.out.println("9. Exit");
            System.out.print("Enter choice: ");
            choice = Integer.parseInt(scanner.nextLine());
            switch (choice) {
                case 1 -> searchItems();
                case 2 -> addItem();
                case 3 -> addMember();
                case 4 -> searchMembers();
                case 5 -> lendItem();
                case 6 -> returnItem();
                case 7 -> removeItem();
                case 8 -> saveToFile();
                case 9 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void searchItems() {
        System.out.print("Enter search term: ");
        String term = scanner.nextLine();
        ArrayList<Item> results = collection.searchItems(term);
        if (results.isEmpty()) {
            System.out.println("No items found!");
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i));
        }
        System.out.print("Select item number (0 to cancel): ");
        int sel = Integer.parseInt(scanner.nextLine());
        if (sel == 0) return;
        Item selected = results.get(sel - 1);
        System.out.println("\n" + selected);
        if (!selected.isAvailable()) {
            System.out.println("On loan to: " + selected.getOnLoanTo().getName());
        }
    }

    private void addItem() {
        System.out.println("1. Book  2. DVD  3. Magazine");
        System.out.print("Choose type: ");
        int type = Integer.parseInt(scanner.nextLine());
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Language: ");
        String language = scanner.nextLine();
        System.out.print("Donor email: ");
        String email = scanner.nextLine();
        Member donor = memberList.getMemberByEmail(email);
        switch (type) {
            case 1 -> {
                System.out.print("Author: ");
                String author = scanner.nextLine();
                System.out.print("ISBN: ");
                String isbn = scanner.nextLine();
                collection.addBook(title, author, donor, language, isbn);
                if (donor != null) donor.addDonation(collection.getItem(title));
                System.out.println("Book added!");
            }
            case 2 -> {
                System.out.print("Director: ");
                String director = scanner.nextLine();
                System.out.print("Audio languages (comma separated): ");
                String[] audio = scanner.nextLine().split(",");
                collection.addDVD(title, director, donor, language, audio);
                if (donor != null) donor.addDonation(collection.getItem(title));
                System.out.println("DVD added!");
            }
            case 3 -> {
                System.out.print("Publisher: ");
                String publisher = scanner.nextLine();
                System.out.print("Issue number: ");
                String issue = scanner.nextLine();
                collection.addMagazine(title, language, donor, publisher, issue);
                if (donor != null) donor.addDonation(collection.getItem(title));
                System.out.println("Magazine added!");
            }
        }
    }

    private void addMember() {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        memberList.addMember(new Member(name, address, email, 0));
        System.out.println("Member added!");
    }

    private void searchMembers() {
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine();
        ArrayList<Member> results = memberList.searchMembers(name);
        if (results.isEmpty()) {
            System.out.println("No members found!");
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            System.out.println((i + 1) + ". " + results.get(i).getName() +
                " | Email: " + results.get(i).getEmail());
        }
        System.out.print("Select member number (0 to cancel): ");
        int sel = Integer.parseInt(scanner.nextLine());
        if (sel == 0) return;
        Member m = results.get(sel - 1);
        System.out.println("\nName: " + m.getName());
        System.out.println("Address: " + m.getAddress());
        System.out.println("Email: " + m.getEmail());
        System.out.println("Donated: " + m.getDonatedQty());
        System.out.println("Borrowing " + m.borrowingQty() + " items:");
        for (Item item : m.getLoanItems()) {
            System.out.println("  - " + item.getTitle());
        }
    }

    private void lendItem() {
        System.out.print("Enter item title: ");
        String title = scanner.nextLine();
        Item item = collection.getItem(title);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        if (!item.isAvailable()) {
            System.out.println("Item already on loan!");
            return;
        }
        System.out.print("Enter member email: ");
        String email = scanner.nextLine();
        Member member = memberList.getMemberByEmail(email);
        if (member == null) {
            System.out.println("Member not found!");
            return;
        }
        if (member.borrowingQty() >= member.getDonatedQty()) {
            System.out.println("Member has reached borrow limit!");
            return;
        }
        member.lend(item);
        System.out.println("Item lent successfully!");
    }

    private void returnItem() {
        System.out.print("Enter item title: ");
        String title = scanner.nextLine();
        Item item = collection.getItem(title);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        if (item.isAvailable()) {
            System.out.println("Item is not on loan!");
            return;
        }
        Member borrower = item.getOnLoanTo();
        borrower.returnItem(item);
        System.out.println("Item returned successfully!");
    }

    private void removeItem() {
        System.out.print("Enter item title to remove: ");
        String title = scanner.nextLine();
        Item item = collection.getItem(title);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        collection.removeItem(item);
        System.out.println("Item removed!");
    }

    private void saveToFile() {
        System.out.print("Enter filename to save: ");
        String filename = scanner.nextLine();
        FileHandler.saveToFile(filename, collection, memberList);
        System.out.println("Saved successfully!");
    }
}