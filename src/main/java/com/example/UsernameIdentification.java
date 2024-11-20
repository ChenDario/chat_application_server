package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class UsernameIdentification {
    private static String username;
    
    public static String username_status(BufferedReader in, ArrayList<ChatApplicationThread> clients) throws IOException{
        username = in.readLine();
        //Se l'username è vuoto
        if(username.isEmpty())
            return "ERROR_401";
        //Se contiene caratteri vietati
        if(invalid_character(username))
            return "ERROR_400";
        //Se l'username è già presente
        if(findUser(username, clients))
            return "ERROR_402";
        //Se la lunghezza è minore di 4 caratteri o maggiore di 30
        if(username.length() < 4)
            return "<";
        if(username.length() > 30)
            return ">";  
        return "SUCC_200";
    }

    public static boolean invalid_character(String name){
        //Caratteri speciali vietati    // I primi due \\ per intendere \ e il terzo \" per il "
        CharSequence caratteriVietati = "!\\\"#$%&'()*+,/:;<=>?@[]^{}|~`";
        //Controllo presenza dei caratteri vietati
        for (int i = 0; i < caratteriVietati.length(); i++) {
            if (name.indexOf(caratteriVietati.charAt(i)) >= 0)
                return true; 
        }
        //String contenente i comandi che non devono essere presenti nel nome
        String[] comands = {
            "@" , "/create_group", 
            "list", "/show_command", 
            "/request_key", "/users_group"
        };

        for(String comand: comands){
            if(name.contains(comand))
                return true;
        }

        return false;
    }

    public static boolean findUser(String name, ArrayList<ChatApplicationThread> clients){
        if(clients.isEmpty())
            return false;
        
        for(ChatApplicationThread client: clients){
            if(client.getUserName().equals(name))
                return true; 
        }
        
        return false; 
    }

    public static String getUsername() {
        return username;
    }

}
