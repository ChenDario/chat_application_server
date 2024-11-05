package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatApplicationThread extends Thread {
    private Socket socketClient;
    private List<ChatApplicationThread> clients; // Lista dei client connessi
    private BufferedReader in;
    private DataOutputStream out;
    private String userName = "";

    public ChatApplicationThread(Socket s, ArrayList<ChatApplicationThread> clients){
        this.socketClient = s;
        this.clients = clients;
        
    }


    CharSequence caratteriVietati = "!\"#$%&'()*+,/:;<=>?@[]^{}|~`";

    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));;
            out = new DataOutputStream(socketClient.getOutputStream());
            


            try { 
                //controllo della validità dell'username 
                boolean check = false;
                boolean contieneVietati = false;
                do{
                    userName = in.readLine();
                    contieneVietati = false;
                    //controllo se l'username è già presente in lista
                    if(this.cercaUser(userName).equals("ERROR_402")){
                        out.writeBytes("ERROR_402" + "\n");
                        check = false;
                    }else{

                    
                    //controllo la presenza di caratteriVietati dell'username
                    for (int i = 0; i < caratteriVietati.length(); i++) {
                        if (userName.indexOf(caratteriVietati.charAt(i)) != -1) {
                            contieneVietati = true;
                            break;
                        }
                    }
                    if(contieneVietati == true){
                        out.writeBytes("ERROR_402" + "\n");
                        check = false;
                    }else{
                        //controllo se l'username è uguale ad un'elemento della lista comandi
                        if(userName.equals("username") || userName.equals("Everyone") || userName.equals("group_name") || userName.equals("active_user") || userName.equals("create_group") 
                        || userName.equals("stop") || userName.equals("add_user") || userName.equals("accept") || userName.equals("reject") || userName.equals("accept_all") 
                        || userName.equals("reject_all")){
                            out.writeBytes("ERROR_402" + "\n");
                            check = false;
                        }else{
                            check = true;
                        }
                    }
                }

                }while (check == false);

                out.writeBytes("Welcome" + "\n");
                //switch

                
                
                
                
                
                
                
                        
                
               
                //invio del messaggio a tutti gli utenti
                String message = "";
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) {
                        System.out.println("Il client si è disconnesso.");
                        break;
                    } else {
                        System.out.println("Messaggio ricevuto: " + message);
                        // Invia il messaggio agli altri client
                        for (ChatApplicationThread client : clients) {
                            if (client != this) { // Invia a tutti gli altri tranne che a sé stesso
                                client.sendMessage(message);
                            }
                        }
                    }
                }

            } catch (IOException e) {
                // TODO: handle exception
                e.printStackTrace();
            } finally {
                try {
                    socketClient.close();
                    clients.remove(this); // Rimuovi il client dalla lista quando si disconnette
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
        } catch (IOException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    // Metodo per inviare un messaggio a questo client
    public void sendMessage(String message) throws IOException {
        out.writeBytes(message + "\n");
    }

    //metodo getUsername
    public String getUserName(){
        return this.userName;
    }

    //metodo per aggiungere gli username all'arraylist
    public String cercaUser(String x){
        for(int i = 0;i < clients.size();i++){
            if(clients.get(i).getUserName().equals(x)){
                return "ERROR_402" + "\n";
            }
        }
            return "Welcome" + "\n";
    }
}
