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

    public ChatApplicationThread(Socket s, ArrayList<ChatApplicationThread> clients){
        this.socketClient = s;
        this.clients = clients;
        
    }

    @Override
    public void run(){
        try {
            in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));;
            out = new DataOutputStream(socketClient.getOutputStream());
            
            try {
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
}
