package com.mycompany.com2007groupcoursework;

public class DVD extends Item {
private String director;
private String[] audioLanguages;

public DVD(String title, String director, Member donatedBy,
String language, String[] audioLanguages) {
super(title, language, donatedBy);
this.director = director;
this.audioLanguages = audioLanguages;
}

public String getDirector() { return director; }
public void setDirector(String director) { this.director = director; }

public String[] getAudioLanguages() { return audioLanguages; }
public void setAudioLanguages(String[] languages) { this.audioLanguages = languages; }

@Override
public String toString() {
return "DVD: " + title + " | Director: " + director +
" | Lang: " + language +
" | Audio: " + String.join(", ", audioLanguages) +
" | Available: " + isAvailable();
}
}