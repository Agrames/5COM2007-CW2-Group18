package com.mycompany.com2007groupcoursework;

public class Book extends Item {
private String author;
private String isbn;

public Book(String title, String author, Member donatedBy,
String language, String isbn) {
super(title, language, donatedBy);
this.author = author;
this.isbn = isbn;
}

public String getAuthor() { return author; }
public void setAuthor(String author) { this.author = author; }

public String getIsbn() { return isbn; }
public void setIsbn(String isbn) { this.isbn = isbn; }

@Override
public String toString() {
return "Book: " + title + " by " + author +
" | ISBN: " + isbn + " | Lang: " + language +
" | Available: " + isAvailable();
}
}