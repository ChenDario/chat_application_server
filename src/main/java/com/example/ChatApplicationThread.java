package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

public class ChatApplicationThread extends Thread {
    private Socket socketClient;
    private ArrayList<ChatApplicationThread> clients; // Lista dei client connessi
    private BufferedReader in;
    private DataOutputStream out;
    private String userName = "";

    public ChatApplicationThread(Socket s, ArrayList<ChatApplicationThread> clients){
        this.socketClient = s;
        this.clients = clients;
        
    }

    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            out = new DataOutputStream(socketClient.getOutputStream());
            

            try {
                String ans;
                //Ricevo l'username per il controllo
                do {
                    ans = UsernameIdentification.username_status(in, clients);
                    out.writeBytes(ans + "\n");
                    System.out.println("Status dell'username: " + ans);
                } while (ans.contains("ERROR") || ans.contains("<") || ans.contains(">"));
                userName = UsernameIdentification.getUsername();

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



}
