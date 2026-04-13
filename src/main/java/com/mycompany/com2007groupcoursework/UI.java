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
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getTabs().addAll(new Tab("Items", buildItemsTab()), new Tab("Members", buildMembersTab()));

        HBox bottomBar = new HBox(10);
        bottomBar.setPadding(new Insets(10));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        Button saveBtn = new Button("Save to File");
        Button loadBtn = new Button("Load from File");
        saveBtn.setOnAction(e -> saveToFile());
        loadBtn.setOnAction(e -> loadFromFile());
        bottomBar.getChildren().addAll(loadBtn, saveBtn);

        BorderPane root = new BorderPane();
        root.setCenter(tabPane);
        root.setBottom(bottomBar);
        return root;
    }

    // ===== ITEMS TAB =====
    private VBox buildItemsTab() {
        itemTable = new TableView<>();
        itemTable.setItems(itemObsList);
        itemTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Item, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> {
            Item item = data.getValue();
            String type = item instanceof Book ? "Book" : item instanceof DVD ? "DVD" : "Magazine";
            return new SimpleStringProperty(type);
        });

        TableColumn<Item, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));

        TableColumn<Item, String> langCol = new TableColumn<>("Language");
        langCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLanguage()));

        TableColumn<Item, String> availCol = new TableColumn<>("Status");
        availCol.setCellValueFactory(data -> {
            Item item = data.getValue();
            return new SimpleStringProperty(item.isAvailable() ? "Available"
                    : "On loan to: " + item.getOnLoanTo().getName());
        });

        itemTable.getColumns().addAll(typeCol, titleCol, langCol, availCol);

        // Buttons
        HBox buttons = new HBox(8);
        buttons.setPadding(new Insets(8));
        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        Button addBookBtn = new Button("Add Book");
        Button addDVDBtn  = new Button("Add DVD");
        Button addMagBtn  = new Button("Add Magazine");
        Button lendBtn    = new Button("Lend");
        Button returnBtn  = new Button("Return");
        Button removeBtn  = new Button("Remove");

        searchBtn.setOnAction(e -> searchItems());
        showAllBtn.setOnAction(e -> refreshItems());
        addBookBtn.setOnAction(e -> addBook());
        addDVDBtn.setOnAction(e -> addDVD());
        addMagBtn.setOnAction(e -> addMagazine());
        lendBtn.setOnAction(e -> lendItem());
        returnBtn.setOnAction(e -> returnItem());
        removeBtn.setOnAction(e -> removeItem());

        buttons.getChildren().addAll(searchBtn, showAllBtn, new Separator(),
                addBookBtn, addDVDBtn, addMagBtn, new Separator(), lendBtn, returnBtn, removeBtn);

        VBox layout = new VBox(8, itemTable, buttons);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(itemTable, Priority.ALWAYS);
        return layout;
    }

    // ===== MEMBERS TAB =====
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
        donatedCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().getDonatedQty())));

        TableColumn<Member, String> borrowingCol = new TableColumn<>("Borrowing");
        borrowingCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.valueOf(data.getValue().borrowingQty())));

        memberTable.getColumns().addAll(nameCol, emailCol, addressCol, donatedCol, borrowingCol);

        HBox buttons = new HBox(8);
        buttons.setPadding(new Insets(8));
        Button addBtn     = new Button("Add Member");
        Button searchBtn  = new Button("Search");
        Button showAllBtn = new Button("Show All");
        addBtn.setOnAction(e -> addMember());
        searchBtn.setOnAction(e -> searchMembers());
        showAllBtn.setOnAction(e -> refreshMembers());
        buttons.getChildren().addAll(addBtn, searchBtn, showAllBtn);

        VBox layout = new VBox(8, memberTable, buttons);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(memberTable, Priority.ALWAYS);
        return layout;
    }

    // ===== ITEM OPERATIONS =====

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
        d.showAndWait().ifPresent(term -> {
            ArrayList<Item> results = collection.searchItems(term);
            if (results.isEmpty()) showAlert("No Results", "No items found matching: " + term);
            else itemObsList.setAll(results);
        });
    }

    private void addBook() {
        Dialog<String[]> dialog = buildDialog("Add Book",
                new String[]{"Title", "Author", "Language", "ISBN", "Donor Email (optional)"});
        dialog.showAndWait().ifPresent(fields -> {
            Member donor = memberList.getMemberByEmail(fields[4]);
            collection.addBook(fields[0], fields[1], donor, fields[2], fields[3]);
            if (donor != null) donor.addDonation(collection.getItem(fields[0]));
            refreshItems();
            showAlert("Success", "Book added!");
        });
    }

    private void addDVD() {
        Dialog<String[]> dialog = buildDialog("Add DVD",
                new String[]{"Title", "Director", "Language", "Audio Languages (comma-separated)", "Donor Email (optional)"});
        dialog.showAndWait().ifPresent(fields -> {
            String[] audio = fields[3].split(",");
            Member donor = memberList.getMemberByEmail(fields[4]);
            collection.addDVD(fields[0], fields[1], donor, fields[2], audio);
            if (donor != null) donor.addDonation(collection.getItem(fields[0]));
            refreshItems();
            showAlert("Success", "DVD added!");
        });
    }

    private void addMagazine() {
        Dialog<String[]> dialog = buildDialog("Add Magazine",
                new String[]{"Title", "Language", "Publisher", "Issue Number", "Donor Email (optional)"});
        dialog.showAndWait().ifPresent(fields -> {
            Member donor = memberList.getMemberByEmail(fields[4]);
            collection.addMagazine(fields[0], fields[1], donor, fields[2], fields[3]);
            if (donor != null) donor.addDonation(collection.getItem(fields[0]));
            refreshItems();
            showAlert("Success", "Magazine added!");
        });
    }

    private void lendItem() {
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select an item first."); return; }
        if (!selected.isAvailable()) { showAlert("Not Available", "Already on loan to: " + selected.getOnLoanTo().getName()); return; }

        TextInputDialog d = new TextInputDialog();
        d.setTitle("Lend Item");
        d.setHeaderText("Lending: " + selected.getTitle());
        d.setContentText("Member email:");
        d.showAndWait().ifPresent(email -> {
            Member member = memberList.getMemberByEmail(email);
            if (member == null) { showAlert("Not Found", "No member with email: " + email); return; }
            int before = member.borrowingQty();
            member.lend(selected);
            if (member.borrowingQty() > before) { refreshItems(); showAlert("Success", "Lent to " + member.getName()); }
            else showAlert("Limit Reached", member.getName() + " has reached their borrow limit.");
        });
    }

    private void returnItem() {
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select an item first."); return; }
        if (selected.isAvailable()) { showAlert("Not On Loan", "This item is not currently on loan."); return; }
        Member borrower = selected.getOnLoanTo();
        borrower.returnItem(selected);
        refreshItems();
        showAlert("Success", "Returned by " + borrower.getName());
    }

    private void removeItem() {
        Item selected = itemTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("No Selection", "Please select an item first."); return; }
        collection.removeItem(selected);
        refreshItems();
        showAlert("Success", "Item removed.");
    }

    // ===== MEMBER OPERATIONS =====

    private void addMember() {
        Dialog<String[]> dialog = buildDialog("Add Member", new String[]{"Name", "Address", "Email"});
        dialog.showAndWait().ifPresent(fields -> {
            memberList.addMember(new Member(fields[0], fields[1], fields[2], 0));
            refreshMembers();
            showAlert("Success", "Member added!");
        });
    }

    private void searchMembers() {
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Search Members");
        d.setHeaderText(null);
        d.setContentText("Enter name:");
        d.showAndWait().ifPresent(name -> {
            ArrayList<Member> results = memberList.searchMembers(name);
            if (results.isEmpty()) showAlert("No Results", "No members found matching: " + name);
            else memberObsList.setAll(results);
        });
    }

    // ===== FILE OPERATIONS =====

    private void saveToFile() {
        TextInputDialog d = new TextInputDialog("library.dat");
        d.setTitle("Save"); d.setHeaderText(null); d.setContentText("Filename:");
        d.showAndWait().ifPresent(f -> { FileHandler.saveToFile(f, collection, memberList); showAlert("Saved", "Data saved to " + f); });
    }

    private void loadFromFile() {
        TextInputDialog d = new TextInputDialog("input-1.dat");
        d.setTitle("Load"); d.setHeaderText(null); d.setContentText("Filename:");
        d.showAndWait().ifPresent(f -> {
            FileHandler.loadFromFile(f, collection, memberList);
            refreshItems(); refreshMembers();
            showAlert("Loaded", "Data loaded from " + f);
        });
    }

    // ===== HELPERS =====

    private Dialog<String[]> buildDialog(String title, String[] labels) {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
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
                String[] values = new String[fields.length];
                for (int i = 0; i < fields.length; i++) values[i] = fields[i].getText().trim();
                return values;
            }
            return null;
        });
        return dialog;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
