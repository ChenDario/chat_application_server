package com.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main {
    //Per avere la lista di tutti i client che si collegano
    private static ArrayList<ChatApplicationThread> clients = new ArrayList<>();
    public static void main(String[] args) throws IOException {

        System.out.println("Server avviato");
        ServerSocket server = new ServerSocket(3000);

        do {
            Socket s = server.accept();
            System.out.println("Il Client si è connesso");

            ChatApplicationThread t = new ChatApplicationThread(s, clients);
            clients.add(t);
            t.start();

        } while (true);

    }
}