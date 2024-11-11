package com.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class PrivateRequest {
    private DataOutputStream out;
    ArrayList<ChatApplicationThread> clients;

    public PrivateRequest(DataOutputStream out, ArrayList<ChatApplicationThread> clients){
        this.out = out;
        this.clients = clients;
    }

    //Handle all the private_chat kind of requests
    public void private_chat_requests(String richiesta, String messaggio, String from_user) throws IOException{
        try {

            // Se richiede la lista degli utenti disponibili
            if (richiesta.equals("@_list")) {
                this.out.writeBytes("SRV_200\n");
                this.out.writeBytes(getAllPrivateChats() + "\n");
            } else{
                // Invio Messaggio a chat privata
                sendMessageToChat(richiesta, messaggio, from_user);
            }
        } catch (Exception e) {
            // TODO: handle exception
            this.out.writeBytes("ERROR_405\n");
        }
    }

    //Get all the available private chats
    public String getAllPrivateChats(){
        
        //If not empty 
        if(!this.clients.isEmpty()){
            StringBuilder availableUsers = new StringBuilder("Available Users: ");

            for (ChatApplicationThread user : this.clients) {
                availableUsers.append(user.getUserName()).append(", ");
            }

            // Remove the trailing comma and space
            availableUsers.setLength(availableUsers.length() - 2);

            return availableUsers.toString();
        }
        return "ERROR_404_P";
    }

    //Send message to chat
    public void sendMessageToChat(String richiesta, String messaggio, String from_user) throws IOException{

        //Recovere the user destination's username
        String user_username = richiesta.substring(1);

        //Se user_dest esiste
        int pos_user = findUser(user_username);

        //output verso il client destinazione
        DataOutputStream clientOut;

        //Controllo se l'utente esiste 
        if(pos_user != -1){
            //Prendo out del client destinazione
            clientOut = this.clients.get(pos_user).getOut();

            String mes = "From " + from_user + ": " + messaggio;

            clientOut.writeBytes("RCV_100" + "\n");
            clientOut.writeBytes(mes + "\n");

            out.writeBytes("SUCC_201\n");

        } else {
            out.writeBytes("ERROR_404_P" + "\n");
        }

    }

    //Verifica presenza dell'utente
    public int findUser(String user_dest){
        
        if(!this.clients.isEmpty()){
            for(int i = 0; i < this.clients.size(); i++){
                if(this.clients.get(i).getUserName().equals(user_dest))
                    return i;
            }
        }
        return -1; 
    }


}
