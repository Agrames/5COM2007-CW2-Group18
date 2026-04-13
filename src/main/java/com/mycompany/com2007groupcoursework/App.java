package com.mycompany.com2007groupcoursework;

public class App {
    public static void main(String[] args) {
        Collection collection = new Collection();
        MemberList memberList = new MemberList();
        FileHandler.loadFromFile("input-1.dat", collection, memberList);
        UI ui = new UI(collection, memberList);
        ui.run();
    }
}