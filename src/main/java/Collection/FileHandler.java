/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 *
 * @author HP
 */
public class FileHandler {
    //load all members and items from file
    public static void loadFromFile(String filename, Collection col, MemberList ml){
        List<String[]> lines = new ArrayList<>();
        
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename),
                        StandardCharsets.UTF_8
        ))){
        String line;
        while((line = br.readLine()) != null){
            if(!line.isBlank()) lines.add(line.split("\\|",-1));
            
        }
        }catch(IOException e){
            System.out.println("Error reading file :"+ e.getMessage());
            return;
        }
        //create all members first so we can search by email
        for(String[] parts : lines){
            if(parts[0].equals("Member")){
                ml.addMember(new Member(parts[1], parts[2], parts[3],
                Integer.parseInt(parts[4])));
            }
        }
        // create items and link donors/ borrowers
        Member currentDonor = null;
        for(String[] parts : lines){
            if(parts[0].equals("Member")){
                currentDonor = ml.getMemberByEmail(parts[3]);
                
                
            }else if(parts[0].equals("Book")){
                col.addBook(parts[1],parts[2], currentDonor,parts[4], parts[3]);
                linkBorrowerAndDonor(parts[1], parts[5], currentDonor, col, ml);
                
            }else if(parts[0].equals("DVD")){
                String[] audio = parts[4].split(",");
                col.addDVD(parts[1],parts[3],currentDonor,parts[2], audio);
                linkBorrowerAndDonor(parts[1],parts[5],currentDonor,col,ml);
            }else if (parts[0].equals("Magazine")){
                col.addMagazine(parts[1],parts[2], currentDonor, parts[3], parts[4]);
                linkBorrowerAndDonor(parts[1], parts[5], currentDonor, col, ml);
            }
        }
        
    }
    //links borrower to item and adds item to donor's donated list
    private static void linkBorrowerAndDonor(String title, String borrowerEmail, 
            Member donor, Collection col, MemberList ml){
        Item item = col.getItem(title);
        if(item == null) return;
        
        if(borrowerEmail != null && !borrowerEmail.isEmpty()){
            Member borrower = ml.getMemberByEmail(borrowerEmail);
            if(borrower !=null){
                item.loanTo(borrower);
                borrower.getLoanItems().add(item);
            }
        }
        // add item to donors donated list
        if (donor != null) donor.getDonatedItems().add(item);
    }
    //save all members and items to file
    public static void saveToFile(String filename, Collection col, MemberList ml){
        try(PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filename),
                        StandardCharsets.UTF_8
                )
        )){
            for(Item item : col.getItems()){
                if(item.getDonator()==null) writeItem(pw,item);
            }
            for(Member m: ml.getMembers()){
                pw.println("Member|"+m.getName()+"|"+m.getAddress() + 
                        "|"+ m.getEmail()+"|"+m.getDonatedQty());
                for(Item item : m.getDonatedItems()){
                    writeItem(pw,item);
                }
            }
        } catch(IOException e){
            System.out.println("Error saving this file:"+ e.getMessage());
        }
    }
    // write a single item line in the correct format
    private static void writeItem(PrintWriter pw, Item item){
        // get borrower email or empty string if not on a loan
        String borrowerEmail = item.isAvailable() ? "" : item.getOnLoanTo().getEmail();
        
        if(item instanceof Book b){
            pw.println("Book|"+b.getTitle()+"|"+b.getAuthor()+
                    "|" + b.getIsbn()+ "|"+b.getLanguage()+ "|" + borrowerEmail);
        } else if(item instanceof DVD d){
        pw.println("DVD|"+d.getTitle()+"|"+d.getLanguage()+"|"+d.getDirector()+"|"+
                String.join(",",d.getAudioLanguages()+"|" + borrowerEmail));
        }else if(item instanceof Magazine mag){
            pw.println("Magazine|" + mag.getTitle()+"|"+mag.getLnaguage()+"|"+ mag.getPublisher()+"|"+mag.getIssueNumber()+
                    "|"+ borrowerEmail);
        }
    
    
        
        
    }
}
