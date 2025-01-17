package com.example;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Group {
    //ArrayList contenente tutti gli username degli utenti presenti
    private ArrayList<String> group;
    private String group_creator; 
    private String group_name = "";
    private String group_code = "";

    public Group(String name, String creator_user, String code){
        this.group = new ArrayList<>();
        this.group_name = name;
        this.group_creator = creator_user;
        this.group_code = code;
    }

    public String addIntoGroup(String user, String from_user){ //aggiunge un'utente ad un gruppo
        if(!findUserInGroup(this.group_creator)){
            group.add(group_creator);
        }
        if(findUserInGroup(from_user)){
            System.out.println("sta provando ad aggiungere al gruppo" + from_user);
            if(!findUserInGroup(user)){
                //If the group is empty
                this.group.add(user);
                return "SUCC_200";
            }
        }
        return "ERROR_404_G"; 
    }

    public String getGroup_name() {
        return this.group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String sendMessageToGroupChat(ArrayList<ChatApplicationThread> clients, DataOutputStream out, String messaggio, String from_user) throws IOException {
        if(!messaggio.equals("null")){
            //Invio del messaggio ai membri del gruppo
            try {
                // Crea una mappa per una ricerca più rapida dei client per username
                HashMap<String, ChatApplicationThread> clientMap = new HashMap<>();
                for (ChatApplicationThread user : clients) {
                    clientMap.put(user.getUserName(), user);
                }

                if (findUserInGroup(from_user)) {
                    for (String username : this.group) {
                        if (!username.equals(from_user) && clientMap.containsKey(username)) {
                            ChatApplicationThread user = clientMap.get(username);
                            try {
                                DataOutputStream clientOutput = user.getOut();
                                clientOutput.writeBytes("RCV_101\n");
                                clientOutput.writeBytes("GROUP " + this.group_name + " by " + from_user + ": " + messaggio + "\n");
                            } catch (IOException e) {
                                System.out.println("Errore nell'invio del messaggio a " + username);
                                e.printStackTrace();
                            }
                        }
                    }
                    // Messaggio inviato con successo
                    return "SUCC_200";
                } 
                return "ERROR_404_G";

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "ERROR_404";
        }
        
        return "ERROR_404_G";
    }
    

    public String getGroupUsers(){ 
        String ans = "Group " + this.group_name + " - Users: ";

        if(this.group.isEmpty())
            return "ERROR_404";

        for(String client: this.group){
            ans += client + ", ";
        }
        //Tolgo la virgola finale
        System.out.println(ans.substring(0, ans.length() - 2));
        return ans.substring(0, ans.length() - 2);
    }

    public boolean findUserInGroup(String username){ // metodo che cerca l'utente all'interno del gruppo

        if(!this.group.isEmpty()){
            for(String user: this.group){
                if(user.equals(username))
                    return true;
            }
        }
        return false; 
    }

    public String removeUserFromGroup(String userName){ // metodo per rimuovere un'utente da un gruppo
        if (!this.group.isEmpty()) {
            if (this.findUserInGroup(userName)) {
                this.group.remove(userName);
                return "RMV_200";
            }
        }
        return "ERROR_404_P";
    }

    public int usersInGroup(){
        return this.group.size();
    }

    //Get e set
    public String getGroup_code() {
        return this.group_code;
    }

    public void setGroup_code(String group_code) {
        this.group_code = group_code;
    }

    public String getGroup_creator() {
        return this.group_creator;
    }

    public void setGroup_creator(String group_creator) {
        this.group_creator = group_creator;
    }

    public ArrayList<String> getGroup() {
        return group;
    }

}
