package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

    //Togliere un po' di sout di debug
public class Main { //controlla l'eccezione nel server quando un client si disconnette improvvisamente
    public static void main(String[] args) throws IOException {
        //Attributi
        ArrayList<ChatApplicationThread> clients = new ArrayList<>();
        ArrayList<Group> groups = new ArrayList<>();    
        ArrayList<String> generated_group_codes = new ArrayList<>();
        HashMap<String, String> publicKeys = new HashMap<>();

        System.out.println("Server avviato");
        ServerSocket server = new ServerSocket(3000);

        do {
            Socket s = server.accept();
            System.out.println("Il Client si è connesso");

            ChatApplicationThread t = new ChatApplicationThread(s, clients, groups,generated_group_codes, publicKeys);
            clients.add(t);
            t.start();

        } while (true);

    }
}