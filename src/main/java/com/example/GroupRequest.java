package com.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GroupRequest {

    DataOutputStream out;
    ArrayList<ChatApplicationThread> clients;
    ArrayList<Group> groups;

    public GroupRequest(DataOutputStream out, ArrayList<ChatApplicationThread> clients, ArrayList<Group> groups){
        this.out = out;
        this.clients = clients; 
        this.groups = groups;
    }

    //Handle all the group_chat kind of requests
    public void group_requests(String richiesta, String messaggio, String from_user) throws IOException{
        String ans = "";
        try {
            //Richiesta di stampa di tutti i gruppi che l'utente è dentro
            if(richiesta.charAt(1) == '@'){
                if(messaggio.isBlank() && richiesta.equals("G@_list")){
                    getAllGroups();
                } else {
                    //ottengo il nome del gruppo
                    String groupName = richiesta.substring(2);
                    System.out.println("Nome gruppo: " + groupName);
                    //Se deve mandare un messaggio ad un gruppo, ed è presente
                    if(!messaggio.isBlank() && findGroup(groups, groupName) != -1){
                        System.out.println("Messaggio di gruppo inviato");
                        ans = groups.get(findGroup(groups, groupName)).sendMessageToGroupChat(clients, out, messaggio, from_user);
                        //out.writeBytes("SUCC_200" + "\n");
                    } else {
                        //out.writeBytes("ERROR_404" + "\n");
                        ans = "ERROR_404";
                    }
                }
            } else {
                //out.writeBytes("ERROR_405" + "\n");
                ans = "ERROR_405";

            }
        } catch (Exception e) {
            // TODO: handle exception
            //out.writeBytes("ERROR_404" + "\n");
            ans = "ERROR_404";

        }
        out.writeBytes(ans + "\n");
    }

    //If the user is in that group
    public static int findGroup(ArrayList<Group> groups, String nome_gruppo){

        if(!groups.isEmpty()){
            for(int i = 0; i < groups.size(); i++){
                if(groups.get(i).getGroup_name().equals(nome_gruppo))
                    return i;
            }
        }
        return -1;
    }

    //Get all the available group chats
    public void getAllGroups() throws IOException{
        if(!groups.isEmpty()){
            StringBuilder ans = new StringBuilder("GROUPS: ");

            for(Group x: groups){
                ans.append(x.getGroup_name()).append(", ");
            }
            //Rimuovo la virgola
            ans.setLength(ans.length() - 2);

            //Invio la la stringa in formato String
            out.writeBytes("SRV_200\n");
            out.writeBytes(ans.toString() + "\n");
        } else {
            out.writeBytes("ERROR_404_G" + "\n");
        }
    }

    //to let the user abbandone a group 
  
}
