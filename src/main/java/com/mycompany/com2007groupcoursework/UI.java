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
 * @author zs25adm
 */
public class UI {

    private Collection collection;
    private MemberList memberList;
    private ObservableList<Item> itemObsList;
    private ObservableList<Member> memberObsList;
    private TableView<Item> itemTable;
    private TableView<Member> memberTable;

    public UI(Collection collection, MemberList memberList){
        this.collection = collection;
        this.memberList = memberList;
        itemObsList = FXCollections.observableArrayList(collection.getItems());
        memberObsList = FXCollections.observableArrayList(memberList.getMembers());
    }
    //build the main layout - tabs on top, file buttons at the bottom
    public Parent buildUI(){

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(new Tab("Items", buildItemsTab()), new Tab("Members", buildMembersTab()));

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
    //set up the items tab with the table and all the action buttons below
    private VBox buildItemsTab(){
        itemTable = new TableView<>();
        itemTable.setItems(itemObsList);
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // need to check what type of item it is so we can display it correctly
        TableColumn<Item, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            Item currentItem = data.getValue();
            String itemType = "Magazine";
            if(currentItem instanceof Book) itemType = "Book";
            else if(currentItem instanceof DVD) itemType = "DVD";
            return new SimpleStringProperty(itemType);
        });

        TableColumn<Item, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Item, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLanguage()));

        // show who has the item on loan if its not available
        TableColumn<Item, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> {
            Item currentItem = data.getValue();
            String statusText = currentItem.isAvailable() ? "Available" : "On loan to: " + currentItem.getOnLoanTo().getName();
            return new SimpleStringProperty(statusText);
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
    //members tab with name email address and how much they donated/ borrowed
    private VBox buildMembersTab(){
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
    // refresh items table to show latest data
    private void refreshItems(){
        itemObsList.setAll(collection.getItems());
    }

    private void refreshMembers(){
        memberObsList.setAll(memberList.getMembers());
    }
    //search items by title - case insensitive partial match
    private void searchItems(){
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("Search Items");
        searchDialog.setHeaderText(null);
        searchDialog.setContentText("Enter search term:");
        Optional<String> result = searchDialog.showAndWait();
        if(result.isPresent()){
            ArrayList<Item> found = collection.searchItems(result.get());
            if(found.isEmpty()){
                showAlert("No Results", "No items found matching: " + result.get());
            } else {
                itemObsList.setAll(found);
            }
        }
    }
    //add a new book - get donor from member list if email was provided
    private void addBook(){
        Dialog<String[]> dialog = buildDialog("Add Book",
                new String[]{"Title", "Author", "Language", "ISBN", "Donor Email (optional)"});
        Optional<String[]> result = dialog.showAndWait();
        if(result.isPresent()){
            String[] inputs = result.get();
            Member donor = memberList.getMemberByEmail(inputs[4]);
            collection.addBook(inputs[0], inputs[1], donor, inputs[2], inputs[3]);
            if(donor != null) donor.addDonation(collection.getItem(inputs[0]));
            refreshItems();
            showAlert("Success", "Book added!");
        }
    }
    //add a new dvd - split audio languages by comma
    private void addDVD(){
        Dialog<String[]> dialog = buildDialog("Add DVD",
                new String[]{"Title", "Director", "Language", "Audio Languages (comma-separated)", "Donor Email (optional)"});
        Optional<String[]> result = dialog.showAndWait();
        if(result.isPresent()){
            String[] inputs = result.get();
            String[] audioLangs = inputs[3].split(",");
            Member donor = memberList.getMemberByEmail(inputs[4]);
            collection.addDVD(inputs[0], inputs[1], donor, inputs[2], audioLangs);
            if(donor != null) donor.addDonation(collection.getItem(inputs[0]));
            refreshItems();
            showAlert("Success", "DVD added!");
        }
    }
    // add a magazine to the collection
    private void addMagazine(){
        Dialog<String[]> dialog = buildDialog("Add Magazine",
                new String[]{"Title", "Language", "Publisher", "Issue Number", "Donor Email (optional)"});
        Optional<String[]> result = dialog.showAndWait();
        if(result.isPresent()){
            String[] inputs = result.get();
            Member donor = memberList.getMemberByEmail(inputs[4]);
            collection.addMagazine(inputs[0], inputs[1], donor, inputs[2], inputs[3]);
            if(donor != null) donor.addDonation(collection.getItem(inputs[0]));
            refreshItems();
            showAlert("Success", "Magazine added!");
        }
    }
    //lend selected item to a member - check borrowing qty before and after so we know if it worked
    private void lendItem(){
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if(selected == null){ showAlert("No Selection", "Please select an item first."); return; }
        if(!selected.isAvailable()){ showAlert("Not Available", "Already on loan to: " + selected.getOnLoanTo().getName()); return; }

        TextInputDialog lendDialog = new TextInputDialog();
        lendDialog.setTitle("Lend Item");
        lendDialog.setHeaderText("Lending: " + selected.getTitle());
        lendDialog.setContentText("Member email:");
        Optional<String> result = lendDialog.showAndWait();
        if(result.isPresent()){
            Member member = memberList.getMemberByEmail(result.get());
            if(member == null){ showAlert("Not Found", "No member with email: " + result.get()); return; }
            int before = member.borrowingQty();
            member.lend(selected);
            if(member.borrowingQty() > before){
                refreshItems();
                showAlert("Success", "Lent to " + member.getName());
            } else {
                showAlert("Limit Reached", member.getName() + " has reached their borrow limit.");
            }
        }
    }
    //return selected item - find the borrower and update both sides
    private void returnItem(){
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if(selected == null){ showAlert("No Selection", "Please select an item first."); return; }
        if(selected.isAvailable()){ showAlert("Not On Loan", "This item is not currently on loan."); return; }
        Member borrower = selected.getOnLoanTo();
        borrower.returnItem(selected);
        refreshItems();
        showAlert("Success", "Returned by " + borrower.getName());

    }
    // remove item from the collection
    private void removeItem(){
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if(selected == null){ showAlert("No Selection", "Please select an item first."); return; }
        collection.removeItem(selected);
        refreshItems();
        showAlert("Success", "Item removed.");
    }
    //edit selected item - show different dialog depending on what type it is
    private void editItem(){
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if(selected == null){ showAlert("No Selection", "Please select an item first."); return; }

        Dialog<String[]> dialog = new Dialog<>();
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        if(selected instanceof Magazine mag){
            dialog.setTitle("Edit Magazine");
            TextField titleField = new TextField(mag.getTitle());
            TextField langField = new TextField(mag.getLanguage());
            TextField pubField = new TextField(mag.getPublisher());
            TextField issueField = new TextField(mag.getIssueNumber());
            grid.add(new Label("Title:"), 0, 0); grid.add(titleField, 1, 0);
            grid.add(new Label("Language:"), 0, 1); grid.add(langField, 1, 1);
            grid.add(new Label("Publisher:"), 0, 2); grid.add(pubField, 1, 2);
            grid.add(new Label("Issue Number:"), 0, 3); grid.add(issueField, 1, 3);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> btn == ButtonType.OK ?
                    new String[]{titleField.getText().trim(), langField.getText().trim(),
                        pubField.getText().trim(), issueField.getText().trim()} : null);
            dialog.showAndWait().ifPresent(inputs -> {
                mag.setTitle(inputs[0]); mag.setLanguage(inputs[1]);
                mag.setPublisher(inputs[2]); mag.setIssueNumber(inputs[3]);
                refreshItems(); showAlert("Success", "Magazine updated!");
            });

        } else if(selected instanceof Book book){
            dialog.setTitle("Edit Book");
            TextField titleField = new TextField(book.getTitle());
            TextField langField = new TextField(book.getLanguage());
            TextField authorField = new TextField(book.getAuthor());
            TextField isbnField = new TextField(book.getIsbn());
            grid.add(new Label("Title:"), 0, 0); grid.add(titleField, 1, 0);
            grid.add(new Label("Language:"), 0, 1); grid.add(langField, 1, 1);
            grid.add(new Label("Author:"), 0, 2); grid.add(authorField, 1, 2);
            grid.add(new Label("ISBN:"), 0, 3); grid.add(isbnField, 1, 3);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> btn == ButtonType.OK ?
                    new String[]{titleField.getText().trim(), langField.getText().trim(),
                        authorField.getText().trim(), isbnField.getText().trim()} : null);
            dialog.showAndWait().ifPresent(inputs -> {
                book.setTitle(inputs[0]); book.setLanguage(inputs[1]);
                book.setAuthor(inputs[2]); book.setIsbn(inputs[3]);
                refreshItems(); showAlert("Success", "Book updated!");
            });

        } else if(selected instanceof DVD dvd){
            dialog.setTitle("Edit DVD");
            TextField titleField = new TextField(dvd.getTitle());
            TextField langField = new TextField(dvd.getLanguage());
            TextField directorField = new TextField(dvd.getDirector());
            TextField audioField = new TextField(String.join(",", dvd.getAudioLanguages()));
            grid.add(new Label("Title:"), 0, 0); grid.add(titleField, 1, 0);
            grid.add(new Label("Language:"), 0, 1); grid.add(langField, 1, 1);
            grid.add(new Label("Director:"), 0, 2); grid.add(directorField, 1, 2);
            grid.add(new Label("Audio Languages (comma-separated):"), 0, 3); grid.add(audioField, 1, 3);
            dialog.getDialogPane().setContent(grid);
            dialog.setResultConverter(btn -> btn == ButtonType.OK ?
                    new String[]{titleField.getText().trim(), langField.getText().trim(),
                        directorField.getText().trim(), audioField.getText().trim()} : null);
            dialog.showAndWait().ifPresent(inputs -> {
                dvd.setTitle(inputs[0]); dvd.setLanguage(inputs[1]);
                dvd.setDirector(inputs[2]); dvd.setAudioLanguages(inputs[3].split(","));
                refreshItems(); showAlert("Success", "DVD updated!");
            });
        }
    }
    // add a new member with 0 donated qty to start
    private void addMember(){
        Dialog<String[]> dialog = buildDialog("Add Member", new String[]{"Name", "Address", "Email"});
        Optional<String[]> result = dialog.showAndWait();
        if(result.isPresent()){
            String[] inputs = result.get();
            memberList.addMember(new Member(inputs[0], inputs[1], inputs[2], 0));
            refreshMembers();
            showAlert("Success", "Member added!");
        }
    }
    //search members by name
    private void searchMembers(){
        TextInputDialog searchDialog = new TextInputDialog();
        searchDialog.setTitle("Search Members");
        searchDialog.setHeaderText(null);
        searchDialog.setContentText("Enter name:");
        Optional<String> result = searchDialog.showAndWait();
        if(result.isPresent()){
            ArrayList<Member> found = memberList.searchMembers(result.get());
            if(found.isEmpty()){
                showAlert("No Results", "No members found matching: " + result.get());
            } else {
                memberObsList.setAll(found);
            }
        }
    }
    //save collection and members to file
    private void saveToFile(){
        TextInputDialog saveDialog = new TextInputDialog("library.dat");
        saveDialog.setTitle("Save"); saveDialog.setHeaderText(null); saveDialog.setContentText("Filename:");
        saveDialog.showAndWait().ifPresent(filename -> {
            FileHandler.saveToFile(filename, collection, memberList);
            showAlert("Saved", "Data saved to " + filename);
        });
    }
    //load from file and refresh both tables so changes show up
    private void loadFromFile(){
        TextInputDialog loadDialog = new TextInputDialog("input-1.dat");
        loadDialog.setTitle("Load"); loadDialog.setHeaderText(null); loadDialog.setContentText("Filename:");
        loadDialog.showAndWait().ifPresent(filename -> {
            FileHandler.loadFromFile(filename, collection, memberList);
            refreshItems(); refreshMembers();
            showAlert("Loaded", "Data loaded from " + filename);
        });
    }
    //helper to build a dialog with labeled text fields - reused for add book, dvd, magazine, member
    private Dialog<String[]> buildDialog(String title, String[] labels){
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField[] fields = new TextField[labels.length];
        for(int index = 0; index < labels.length; index++){
            fields[index] = new TextField();
            grid.add(new Label(labels[index] + ":"), 0, index);
            grid.add(fields[index], 1, index);
        }
        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if(btn == ButtonType.OK){
                String[] values = new String[fields.length];
                for(int index = 0; index < fields.length; index++) values[index] = fields[index].getText().trim();
                return values;
            }
            return null;
        });
        return dialog;
    }
    // show an information alert to the user
    private void showAlert(String title, String msg){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
