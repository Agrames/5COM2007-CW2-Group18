package com.mycompany.com2007groupcoursework;

public class Magazine extends Item {
private String publisher;
private String issueNumber;

public Magazine(String title, String language, Member donatedBy,
String publisher, String issueNumber) {
super(title, language, donatedBy);
this.publisher = publisher;
this.issueNumber = issueNumber;
}

public String getPublisher() { return publisher; }
public void setPublisher(String publisher) { this.publisher = publisher; }

public String getIssueNumber() { return issueNumber; }
public void setIssueNumber(String issueNumber) { this.issueNumber = issueNumber; }

@Override
public String toString() {
return "Magazine: " + title + " | Publisher: " + publisher +
" | Issue: " + issueNumber + " | Lang: " + language +
" | Available: " + isAvailable();
}
}