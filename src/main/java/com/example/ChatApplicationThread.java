package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatApplicationThread extends Thread {
    private Socket socketClient;
    private ArrayList<ChatApplicationThread> clients; // Lista dei client connessi
    private BufferedReader in;
    private DataOutputStream out;
    private String userName = "";
    private ArrayList<Group> groups;    
    private ArrayList<String> generated_group_codes;
    private HashMap<String, String> publicKeys;

    public ChatApplicationThread(Socket s, ArrayList<ChatApplicationThread> clients, ArrayList<Group> groups, ArrayList<String> generated_group_codes, HashMap<String, String> publicKeys) throws IOException {
        this.socketClient = s;
        this.clients = clients;
        this.groups = groups; 
        this.in = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));
        this.out = new DataOutputStream(this.socketClient.getOutputStream());
        this.generated_group_codes = generated_group_codes;
        this.publicKeys = publicKeys;
    }

    @Override
    public void run() {
        try {
            // Validazione dell'username
            username_validation(in, out);

            //Save the user's public key
            savePublicKey(in);
            System.out.println("Public Key saved");

            // Ricevi richieste dal client finché non si disconnette
            UserRequest.receive_user_requests(in, out, groups, clients, userName, generated_group_codes, publicKeys);

        } catch (IOException e) {
            System.err.println("Errore nella comunicazione con il client: " + e.getMessage());
        } finally {
            try {
                // Chiusura delle risorse
                if (socketClient != null && !socketClient.isClosed()) {
                    socketClient.close();
                }
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
            } finally {
                // Rimuovi il client dalla lista dopo la chiusura
                clients.remove(this);
                System.out.println("Client disconnesso e rimosso dalla lista.");
            }
        }
    }

    public void savePublicKey(BufferedReader in) throws IOException{
        String message = in.readLine();
        
        String[] publickey = message.split(" ", 2);

        if(publickey[0].equals("PublicKey") && publickey[1] != null){
            publicKeys.put(userName, publickey[1]);
        }
    }

    public String getUserName() {
        return this.userName;
    }

    public void username_validation(BufferedReader in, DataOutputStream out) throws IOException {
        // Validazione dell'username
        String x = "";
        do {
            x = UsernameIdentification.username_status(in, clients);
            out.writeBytes(x + "\n");
            System.out.println("Status dell'username: " + x);
        } while (x.contains(">") || x.contains("ERROR") || x.contains("<"));

        userName = UsernameIdentification.getUsername();
    }

    public DataOutputStream getOut() {
        return this.out;
    }

    public BufferedReader getIn() {
        return this.in;
    }
}
