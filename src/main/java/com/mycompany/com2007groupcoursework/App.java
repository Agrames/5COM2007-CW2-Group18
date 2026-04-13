package com.mycompany.com2007groupcoursework;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private Collection collection = new Collection();
    private MemberList memberList = new MemberList();

    @Override
    public void start(Stage primaryStage) {
        FileHandler.loadFromFile("input-1.dat", collection, memberList);

        UI ui = new UI(collection, memberList);
        Scene scene = new Scene(ui.buildUI(), 950, 650);
        primaryStage.setTitle("ISA Library System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
