package com.mycompany.com2007groupcoursework;

public abstract class Item {
protected String title;
protected String language;
protected Member donatedBy;
protected Member onLoanTo;

public Item(String title, String language, Member donatedBy) {
this.title = title;
this.language = language;
this.donatedBy = donatedBy;
this.onLoanTo = null;
}

public String getTitle() { return title; }
public void setTitle(String title) { this.title = title; }

public String getLanguage() { return language; }
public void setLanguage(String language) { this.language = language; }

public Member getDonator() { return donatedBy; }
public void clearDonator() { donatedBy = null; }

public boolean isAvailable() { return onLoanTo == null; }
public void loanTo(Member borrower) { onLoanTo = borrower; }
public void returnLoan() { onLoanTo = null; }
public Member getOnLoanTo() { return onLoanTo; }

public abstract String toString();
}
