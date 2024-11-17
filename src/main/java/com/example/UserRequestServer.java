package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UserRequestServer {

    public static void receive_user_requests(BufferedReader in, DataOutputStream out, ArrayList<Group> groups, ArrayList<ChatApplicationThread> clients, String from_user, ArrayList<String> generated_group_codes, HashMap<String, String> users_key) throws IOException{
        String user_request = "";
        do {
            user_request = in.readLine();
            if(!user_request.equalsIgnoreCase("Exit")){
                System.out.println("Stringa ricevuta dal client: " + user_request);
                identify_request(user_request, groups, clients, in, out, from_user, generated_group_codes, users_key);
            } 
        } while (!user_request.equalsIgnoreCase("Exit"));
    }

    //Divisione della stringa
    public static void identify_request(String string_from_user, ArrayList<Group> groups, ArrayList<ChatApplicationThread> clients, BufferedReader in, DataOutputStream out, String from_user,ArrayList<String> generated_group_codes, HashMap<String, String> users_key) throws IOException{
            String[] string_request;
        
            try {
                //Divide la stringa in due parti appena incontra uno spazio vuoto, limitando il risultato in 2 sezioni
                //Nel caso fosse una sola parola allora ritorna una sola stringa 
                string_request = string_from_user.split(" ", 2); 
                //Definire la richiesta
                String request = string_request[0]; //Richiesta dell'utente
                String message = string_request.length > 1 ? string_request[1] : ""; // Se esiste, prendi la seconda parte, altrimenti una stringa vuota
                
                System.out.println("Richiesta(Thread): " + request);
                System.out.println("Messaggio(Thread): " + message);


                if(isRequest(request)){
                    //Handle all the request and return the code of the operations
                    request_type(request, message, groups, clients, in, out, from_user, generated_group_codes, users_key);
                } else {
                    //Richiesta non trovata
                    out.writeBytes("ERROR_405" + "\n");
                }
            } catch (Exception e) {
                // TODO: handle exception
                out.writeBytes("ERROR_500" + "\n");
            }
    }

    //Controlla se è effetivamente una richiesta
    public static boolean isRequest(String request){
        String[] requests = {
            "@" , "/create_group", 
            "list", "/show_command", 
            "/request_key"
        };

        for(String type: requests){
            if(request.contains(type))
                return true;
        }

        return false;
    }

    public static void request_type(String request, String messaggio ,ArrayList<Group> groups, ArrayList<ChatApplicationThread> clients, BufferedReader in, DataOutputStream out, String from_user,ArrayList<String> generated_group_codes,HashMap<String, String> users_key) throws IOException{
        char comando = request.charAt(0);

        try {
            switch(comando){
                //Se si tratta di un gruppo
                case 'G': 
                    GroupRequest gr = new GroupRequest(out, clients, groups);
                    gr.group_requests(request, messaggio, from_user);
                    break;

                //Se si tratta di una chat privata
                case '@': 
                    PrivateRequest pr = new PrivateRequest(out, clients);
                    pr.private_chat_requests(request, messaggio, from_user);
                    break;

                //Se si tratta di un altro comando presente che inizia per /
                case '/': 
                    OtherRequests or = new OtherRequests(in, out, clients, groups, generated_group_codes, users_key);
                    or.handleRequest(request, messaggio, from_user);
                    break;
    
                default: 
                    out.writeBytes("ERROR_405" + "\n");
                    break;
            }   
        } catch (IOException e) {
            // TODO: handle exception
            out.writeBytes("ERROR_500" + "\n");
        }

    }

}


