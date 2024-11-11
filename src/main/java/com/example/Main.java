package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {

        //Per avere la lista di tutti i client che si collegano e i gruppi che vengono creati con relativo codice
        ArrayList<ChatApplicationThread> clients = new ArrayList<>();
        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<String> generated_group_codes = new ArrayList<>();

        System.out.println("Server avviato");
        ServerSocket server = new ServerSocket(3000);

        do {
            Socket s = server.accept();
            System.out.println("Il Client si è connesso");

            ChatApplicationThread t = new ChatApplicationThread(s, clients, groups,generated_group_codes);
            clients.add(t);
            t.start();

        } while (true);

    }
}