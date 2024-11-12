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
        try {

            //Richiesta di stampa di tutti i gruppi che l'utente è dentro
            if(richiesta.charAt(1) == '@'){
                if(messaggio.isBlank() && richiesta.equals("G@_list") && !groups.isEmpty()){
                    out.writeBytes("SRV_200" + "\n");
                    out.writeBytes(getAllGroups(this.groups) + "\n");
                }


                // per gestire l'uscita da un gruppo
                if(richiesta.contains("/left_G@")){
                   
                        
                    
                }
                
                //per visualizzare tutti i membri di un gruppo
                if(richiesta.contains("/users_group")){
                    
                }

                //ottengo il nome del gruppo
                String groupName = richiesta.substring(2);
                //Se deve mandare un messaggio ad un gruppo, ed è presente
                if(!messaggio.isBlank() && findGroup(groups, groupName) != -1){
                    groups.get(findGroup(groups, groupName)).sendMessageToGroupChat(clients, out, messaggio, from_user);
                    out.writeBytes("SUCC_200" + "\n");
                }
                    
                


            } else {
                out.writeBytes("ERROR_405" + "\n");
            }
        } catch (Exception e) {
            // TODO: handle exception
            out.writeBytes("ERROR_404" + "\n");
        }
        
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
    public String getAllGroups(ArrayList<Group> groups){
        if(!groups.isEmpty()){
            StringBuilder ans = new StringBuilder("Gruppi: ");

            for(Group x: groups){
                ans.append(x.getGroup_name()).append(", ");
            }
            //Rimuovo la virgola
            ans.setLength(ans.length() - 2);
            //returno la la stringa in formato String
            return ans.toString();
        }
        return "ERROR_404_G";
    }

    //to let the user abbandone a group 
  
}
