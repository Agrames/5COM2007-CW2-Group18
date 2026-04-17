/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.com2007groupcoursework;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 *
 * @author Agrames
 */
public class UI {

    private Collection collection;
    private MemberList memberList;
    private ObservableList<Item> itemObsList;
    private ObservableList<Member> memberObsList;
    private TableView<Item> itemTable;
    private TableView<Member> memberTable;

    public UI(Collection collection, MemberList memberList) {
        this.collection = collection;
        this.memberList = memberList;
        itemObsList = FXCollections.observableArrayList(collection.getItems());
        memberObsList = FXCollections.observableArrayList(memberList.getMembers());
    }

    public Parent buildUI() {

        Tab itemsTab = new Tab("Items", buildItemsTab());
        Tab membersTab = new Tab("Members", buildMembersTab());

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(itemsTab, membersTab);

        Button saveBtn = new Button("Save to File");
        Button loadBtn = new Button("Load from File");
        saveBtn.setOnAction(e -> saveToFile());
        loadBtn.setOnAction(e -> loadFromFile());

        HBox bottom = new HBox(10);
        bottom.setPadding(new Insets(10));
        bottom.setAlignment(Pos.CENTER_RIGHT);
        bottom.getChildren().addAll(loadBtn, saveBtn);

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(bottom);
        return root;
    }

    private VBox buildItemsTab() {

        itemTable = new TableView<>();
        itemTable.setItems(itemObsList);
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Item, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            Item i = data.getValue();
            String t = "Magazine";
            if (i instanceof Book) t = "Book";
            else if (i instanceof DVD) t = "DVD";
            return new SimpleStringProperty(t);
        });

        TableColumn<Item, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Item, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLanguage()));

        TableColumn<Item, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            Item i = data.getValue();
            String s = i.isAvailable() ? "Available" : "On loan to: " + i.getOnLoanTo().getName();
            return new SimpleStringProperty(s);
        });

        itemTable.getColumns().addAll(typeCol, titleCol, langCol, statusCol);

        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");
        Button addBookBtn = new Button("Add Book");
        Button addDVDBtn = new Button("Add DVD");
        Button addMagBtn = new Button("Add Magazine");
        Button editBtn = new Button("Edit Item");
        Button lendBtn = new Button("Lend");
        Button returnBtn = new Button("Return");
        Button removeBtn = new Button("Remove");

        searchBtn.setOnAction(e -> searchItems());
        showAllBtn.setOnAction(e -> refreshItems());
        addBookBtn.setOnAction(e -> addBook());
        addDVDBtn.setOnAction(e -> addDVD());
        addMagBtn.setOnAction(e -> addMagazine());
        editBtn.setOnAction(e -> editItem());
        lendBtn.setOnAction(e -> lendItem());
        returnBtn.setOnAction(e -> returnItem());
        removeBtn.setOnAction(e -> removeItem());

        HBox buttons = new HBox(8);
        buttons.setPadding(new Insets(8));
        buttons.getChildren().addAll(searchBtn, showAllBtn, new Separator(),
                addBookBtn, addDVDBtn, addMagBtn, new Separator(),
                editBtn, new Separator(), lendBtn, returnBtn, removeBtn);

        VBox layout = new VBox(8, itemTable, buttons);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(itemTable, Priority.ALWAYS);
        return layout;
    }

    private VBox buildMembersTab() {

        memberTable = new TableView<>();
        memberTable.setItems(memberObsList);
        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Member, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Member, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        TableColumn<Member, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAddress()));

        TableColumn<Member, String> donatedCol = new TableColumn<>("Donated");
        donatedCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDonatedQty())));

        TableColumn<Member, String> borrowingCol = new TableColumn<>("Borrowing");
        borrowingCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().borrowingQty())));

        memberTable.getColumns().addAll(nameCol, emailCol, addressCol, donatedCol, borrowingCol);

        Button addBtn = new Button("Add Member");
        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");

        addBtn.setOnAction(e -> addMember());
        searchBtn.setOnAction(e -> searchMembers());
        showAllBtn.setOnAction(e -> refreshMembers());

        HBox buttons = new HBox(8);
        buttons.setPadding(new Insets(8));
        buttons.getChildren().addAll(addBtn, searchBtn, showAllBtn);

        VBox layout = new VBox(8, memberTable, buttons);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(memberTable, Priority.ALWAYS);
        return layout;
    }

    private void refreshItems() {
        itemObsList.setAll(collection.getItems());
    }

    private void refreshMembers() {
        memberObsList.setAll(memberList.getMembers());
    }

    private void searchItems() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Search Items");
        d.setHeaderText(null);
        d.setContentText("Enter search term:");
        Optional<String> result = d.showAndWait();
        if (result.isPresent()) {
            String term = result.get();
            ArrayList<Item> found = collection.searchItems(term);
            if (found.isEmpty()) {
                showAlert("No Results", "No items found matching: " + term);
            } else {
                itemObsList.setAll(found);
            }
        }
    }

    private void addBook() {
        Dialog<String[]> dialog = buildDialog("Add Book",
                new String[]{"Title", "Author", "Language", "ISBN", "Donor Email (optional)"});
        Optional<String[]> res = dialog.showAndWait();
        if (res.isPresent()) {
            String[] f = res.get();
            Member donor = memberList.getMemberByEmail(f[4]);
            collection.addBook(f[0], f[1], donor, f[2], f[3]);
            if (donor != null) {
                donor.addDonation(collection.getItem(f[0]));
            }
            refreshItems();
            showAlert("Success", "Book added!");
        }
    }

    private void addDVD() {
        Dialog<String[]> dialog = buildDialog("Add DVD",
                new String[]{"Title", "Director", "Language", "Audio Languages (comma-separated)", "Donor Email (optional)"});
        Optional<String[]> res = dialog.showAndWait();
        if (res.isPresent()) {
            String[] f = res.get();
            String[] audio = f[3].split(",");
            Member donor = memberList.getMemberByEmail(f[4]);
            collection.addDVD(f[0], f[1], donor, f[2], audio);
            if (donor != null) {
                donor.addDonation(collection.getItem(f[0]));
            }
            refreshItems();
            showAlert("Success", "DVD added!");
        }
    }

    private void addMagazine() {
        Dialog<String[]> dialog = buildDialog("Add Magazine",
                new String[]{"Title", "Language", "Publisher", "Issue Number", "Donor Email (optional)"});
        Optional<String[]> res = dialog.showAndWait();
        if (res.isPresent()) {
            String[] f = res.get();
            Member donor = memberList.getMemberByEmail(f[4]);
            collection.addMagazine(f[0], f[1], donor, f[2], f[3]);
            if (donor != null) {
                donor.addDonation(collection.getItem(f[0]));
            }
            refreshItems();
            showAlert("Success", "Magazine added!");
        }
    }

    private void lendItem() {
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an item first.");
            return;
        }
        if (!selected.isAvailable()) {
            showAlert("Not Available", "Already on loan to: " + selected.getOnLoanTo().getName());
            return;
        }
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Lend Item");
        d.setHeaderText("Lending: " + selected.getTitle());
        d.setContentText("Member email:");
        Optional<String> res = d.showAndWait();
        if (res.isPresent()) {
            String email = res.get();
            Member m = memberList.getMemberByEmail(email);
            if (m == null) {
                showAlert("Not Found", "No member with email: " + email);
                return;
            }
            int before = m.borrowingQty();
            m.lend(selected);
            if (m.borrowingQty() > before) {
                refreshItems();
                showAlert("Success", "Lent to " + m.getName());
            } else {
                showAlert("Limit Reached", m.getName() + " has reached their borrow limit.");
            }
        }
    }

    private void returnItem() {
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an item first.");
            return;
        }
        if (selected.isAvailable()) {
            showAlert("Not On Loan", "This item is not currently on loan.");
            return;
        }
        Member borrower = selected.getOnLoanTo();
        borrower.returnItem(selected);
        refreshItems();
        showAlert("Success", "Returned by " + borrower.getName());
    }

    private void removeItem() {
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an item first.");
            return;
        }
        collection.removeItem(selected);
        refreshItems();
        showAlert("Success", "Item removed.");
    }

    private void editItem() {
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select an item first.");
            return;
        }

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        if (selected instanceof Magazine mag) {
            dialog.setTitle("Edit Magazine");
            TextField title = new TextField(mag.getTitle());
            TextField lang = new TextField(mag.getLanguage());
            TextField pub = new TextField(mag.getPublisher());
            TextField issue = new TextField(mag.getIssueNumber());
            grid.add(new Label("Title:"), 0, 0);
            grid.add(title, 1, 0);
            grid.add(new Label("Language:"), 0, 1);
            grid.add(lang, 1, 1);
            grid.add(new Label("Publisher:"), 0, 2);
            grid.add(pub, 1, 2);
            grid.add(new Label("Issue Number:"), 0, 3);
            grid.add(issue, 1, 3);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    return new String[]{title.getText().trim(), lang.getText().trim(),
                        pub.getText().trim(), issue.getText().trim()};
                }
                return null;
            });
            Optional<String[]> r = dialog.showAndWait();
            if (r.isPresent()) {
                String[] f = r.get();
                mag.setTitle(f[0]);
                mag.setLanguage(f[1]);
                mag.setPublisher(f[2]);
                mag.setIssueNumber(f[3]);
                refreshItems();
                showAlert("Success", "Magazine updated!");
            }

        } else if (selected instanceof Book b) {
            dialog.setTitle("Edit Book");
            TextField title = new TextField(b.getTitle());
            TextField lang = new TextField(b.getLanguage());
            TextField author = new TextField(b.getAuthor());
            TextField isbn = new TextField(b.getIsbn());
            grid.add(new Label("Title:"), 0, 0);
            grid.add(title, 1, 0);
            grid.add(new Label("Language:"), 0, 1);
            grid.add(lang, 1, 1);
            grid.add(new Label("Author:"), 0, 2);
            grid.add(author, 1, 2);
            grid.add(new Label("ISBN:"), 0, 3);
            grid.add(isbn, 1, 3);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    return new String[]{title.getText().trim(), lang.getText().trim(),
                        author.getText().trim(), isbn.getText().trim()};
                }
                return null;
            });
            Optional<String[]> r = dialog.showAndWait();
            if (r.isPresent()) {
                String[] f = r.get();
                b.setTitle(f[0]);
                b.setLanguage(f[1]);
                b.setAuthor(f[2]);
                b.setIsbn(f[3]);
                refreshItems();
                showAlert("Success", "Book updated!");
            }

        } else if (selected instanceof DVD d) {
            dialog.setTitle("Edit DVD");
            TextField title = new TextField(d.getTitle());
            TextField lang = new TextField(d.getLanguage());
            TextField director = new TextField(d.getDirector());
            TextField audio = new TextField(String.join(",", d.getAudioLanguages()));
            grid.add(new Label("Title:"), 0, 0);
            grid.add(title, 1, 0);
            grid.add(new Label("Language:"), 0, 1);
            grid.add(lang, 1, 1);
            grid.add(new Label("Director:"), 0, 2);
            grid.add(director, 1, 2);
            grid.add(new Label("Audio Languages (comma-separated):"), 0, 3);
            grid.add(audio, 1, 3);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> {
                if (btn == ButtonType.OK) {
                    return new String[]{title.getText().trim(), lang.getText().trim(),
                        director.getText().trim(), audio.getText().trim()};
                }
                return null;
            });
            Optional<String[]> r = dialog.showAndWait();
            if (r.isPresent()) {
                String[] f = r.get();
                d.setTitle(f[0]);
                d.setLanguage(f[1]);
                d.setDirector(f[2]);
                d.setAudioLanguages(f[3].split(","));
                refreshItems();
                showAlert("Success", "DVD updated!");
            }
        }
    }

    private void addMember() {
        Dialog<String[]> dialog = buildDialog("Add Member", new String[]{"Name", "Address", "Email"});
        Optional<String[]> res = dialog.showAndWait();
        if (res.isPresent()) {
            String[] f = res.get();
            memberList.addMember(new Member(f[0], f[1], f[2], 0));
            refreshMembers();
            showAlert("Success", "Member added!");
        }
    }

    private void searchMembers() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Search Members");
        d.setHeaderText(null);
        d.setContentText("Enter name:");
        Optional<String> res = d.showAndWait();
        if (res.isPresent()) {
            String name = res.get();
            ArrayList<Member> found = memberList.searchMembers(name);
            if (found.isEmpty()) {
                showAlert("No Results", "No members found matching: " + name);
            } else {
                memberObsList.setAll(found);
            }
        }
    }

    private void saveToFile() {
        TextInputDialog d = new TextInputDialog("library.dat");
        d.setTitle("Save");
        d.setHeaderText(null);
        d.setContentText("Filename:");
        Optional<String> res = d.showAndWait();
        if (res.isPresent()) {
            FileHandler.saveToFile(res.get(), collection, memberList);
            showAlert("Saved", "Data saved to " + res.get());
        }
    }

    private void loadFromFile() {
        TextInputDialog d = new TextInputDialog("input-1.dat");
        d.setTitle("Load");
        d.setHeaderText(null);
        d.setContentText("Filename:");
        Optional<String> res = d.showAndWait();
        if (res.isPresent()) {
            FileHandler.loadFromFile(res.get(), collection, memberList);
            refreshItems();
            refreshMembers();
            showAlert("Loaded", "Data loaded from " + res.get());
        }
    }

    private Dialog<String[]> buildDialog(String title, String[] labels) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField[] fields = new TextField[labels.length];
        for (int i = 0; i < labels.length; i++) {
            fields[i] = new TextField();
            grid.add(new Label(labels[i] + ":"), 0, i);
            grid.add(fields[i], 1, i);
        }
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                String[] vals = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    vals[i] = fields[i].getText().trim();
                }
                return vals;
            }
            return null;
        });
        return dialog;
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
