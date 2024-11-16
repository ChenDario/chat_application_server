package com.example;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;

public class OtherRequests {
    private BufferedReader in;
    private DataOutputStream out;
    private ArrayList<ChatApplicationThread> clients;
    private ArrayList<String> generated_group_codes;
    private ArrayList<Group> groups;
    private HashMap<String, String> users_key;

    public OtherRequests(BufferedReader in, DataOutputStream out, ArrayList<ChatApplicationThread> clients, ArrayList<Group> groups, ArrayList<String> generated_group_codes, HashMap<String, String> users_key){
        this.in = in;
        this.out = out;
        this.clients = clients;
        this.groups = groups;
        this.generated_group_codes = generated_group_codes;
        this.users_key = users_key;
    }

    //Handle all the remaing kind of request (/)
    public void handleRequest(String richiesta, String messaggio, String from_user){
        try {
            switch(richiesta){
                case "/RequestKey":
                    break;

                case "/create_group":
                    //Sout for debug
                    System.out.println("Richiesta inoltrata 1");
                    create_group(messaggio, this.groups, from_user);
                    break;
                
                case "/list_all":
                    PrivateRequest pr = new PrivateRequest(out, clients);
                    GroupRequest gr = new GroupRequest(out, clients, groups);

                    pr.getAllPrivateChats();
                    gr.getAllGroups();
                    break;
                
                case "/join_G@":
                    addUserToGroupChat(messaggio, this.groups);
                    break;

                case "/users_group": //per mostrare tutti i membri di un gruppo
                    usersGroup(messaggio, this.groups);
                    break;

                case "/left_G@": // per gestire l'uscità da un gruppo
                    int pos = GroupRequest.findGroup(groups, messaggio); // cerco la posizione del gruppo dal qual l'utente vuole uscire
                    
                    if(pos != -1){
                        String ans = groups.get(pos).removeUserFromGroup(from_user);
                        if(ans.equals("RMV_200")){
                            out.writeBytes("RMV_200" + "\n"); // informo il client che la rimozione è avvenuta con successo
                            out.writeBytes(groups.get(pos).getGroup_name() + "\n");
                            out.writeBytes(groups.get(pos).getGroup_code() + "\n");
                        }
                    } else {
                        out.writeBytes("ERROR_404_G\n");
                    }
                   break;


                default:
                    out.writeBytes("ERROR_500" + "\n");
                    System.out.println("Errore nell'inserimento del comando");
                    break;
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("ERROR with the handling request");
        }
    }

    public void sendKey(String user, ArrayList<ChatApplicationThread> clients) throws IOException{
        if(UsernameIdentification.findUser(user, clients)){
            String key = users_key.get(user);
            out.writeBytes("PUBLIC_KEY\n");
            out.writeBytes(user + "\n");
            out.writeBytes(key + "\n");
        }
    }

    public void usersGroup(String messaggio, ArrayList<Group> groups) throws IOException{
        if(!groups.isEmpty()){
            for(Group g : groups){
                if(g.getGroup_name().equals(messaggio)){
                    out.writeBytes("SRV_200" + "\n");
                    out.writeBytes(g.getGroupUsers() + "\n");
                    break;
                }
            }
        } else {
            out.writeBytes("ERROR_404_G");
        }
    }

    public void addUserToGroupChat(String messaggio, ArrayList<Group> groups) throws IOException{
        //Divide the String in two to get the group_name and the lists of user to add to that group
        String[] group_users = messaggio.split(" - ", 2);

        //Suddivido il nome del gruppo con quello degli username da aggiungere
        String group_name = group_users[0].trim();
        String add_users = group_users.length > 1 ? group_users[1].trim() : "";
        //Sout for debug
        System.out.println("Nome gruppo: " + group_name);
        System.out.println("Nome utenti: " + add_users);

        int pos_group = findGroup(group_name);
        System.out.println("Pos gruppo: " + pos_group);

        if(pos_group != -1){
            String[] usernames = add_users.split(",\\s*");

            for(String name : usernames){
                int pos_user = findUser(name);
                //Sout for debug
                System.out.println("Nome utente: " + name + " Utente in pos: " + pos_user);

                DataOutputStream out_dest;
                //Se l'utente esiste, invia al client il risultato dell'inserimento
                if(pos_user != -1){
                    String ans = groups.get(pos_group).addIntoGroup(name);
                    //Sout for debug
                    System.out.println(ans);
                    out.writeBytes(ans + "\n");

                    //Invio all'user che è stato aggiunto i dettagli del gruppo in modo
                    out_dest = clients.get(pos_user).getOut();
                    out_dest.writeBytes("GRP_INFO" + "\n");
                    out_dest.writeBytes(group_name + "\n");
                    //Sout for debug
                    System.out.println(group_name);
                    out_dest.writeBytes(getGroupCode(group_name) + "\n");
                    //Sout for debug
                    System.out.println(getGroupCode(group_name));
                    System.out.println("Invio dati a user_dest" + clients.get(pos_user).getUserName());
                }
            }

            this.out.writeBytes("SUCC_200\n");
        } else {
            this.out.writeBytes("ERROR_404_G" + "\n");
        }
    }

    //Find the pos of the given group_name in the lists of group
    public int findGroup(String group_name){

        if(!groups.isEmpty()){
            for(int i = 0; i < this.groups.size(); i++){
                if(this.groups.get(i).getGroup_name().equals(group_name))
                    return i;
            }
        }
        return -1;
    }

    public void create_group(String messaggio, ArrayList<Group> groups, String from_user) throws IOException{
        //Controllo degli caratteri invalidi
        if(!UsernameIdentification.invalid_character(messaggio)){

            String group_code = generateGroupCode();

            //Creo un gruppo col nome inserito dall'utente
            Group g_tmp = new Group(messaggio, from_user, group_code);

            this.out.writeBytes("CL_200" + "\n");

            //Invio del codice del gruppo e il nome del gruppo
            this.out.writeBytes("RCV_200" + "\n");
            this.out.writeBytes(messaggio + "\n");
            this.out.writeBytes(group_code + "\n");

            //Add the code to the group
            g_tmp.setGroup_code(group_code);

            //Group added to the list
            groups.add(g_tmp);

            for(Group g : groups){
                System.out.println("Gruppi: " + g.getGroup_name());
            }

            //Invio il messaggio di conferma al client
            this.out.writeBytes("SUCC_200\n");

        } else {
            this.out.writeBytes("ERROR_400\n");
        }
    }

    public String generateGroupCode(){
        String character = "0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
        StringBuilder code = new StringBuilder();
        Random random = new Random();

        do {
            //Verificare questa linea di codice se resetta tutto o meno
            code.setLength(0);
            //Genera un codice di 5 caratteri casuali presenti nella stringa character
            for(int i = 0; i < 5; i++){
                //Append a random character from character string at pos = random.nextInt(character.length()) from 0 to character.length()
                code.append(character.charAt(random.nextInt(character.length())));
            }
        } while (getGeneratedCodes(code.toString()));

        generated_group_codes.add(code.toString());

        return code.toString();
    }

    public boolean getGeneratedCodes(String code){
        if(!generated_group_codes.isEmpty()){
            for(String x : this.generated_group_codes){
                if(x.equals(code))
                    return true;
            }
        }
        return false;
    }

    public int findUser(String name){

        if(!this.clients.isEmpty()){
            for(int i  = 0; i < this.clients.size(); i++){
                if(this.clients.get(i).getUserName().equals(name))
                    return i;
            }
        }
        return -1; 
    }

    public String getGroupCode(String group_name){

        for(Group g : this.groups){
            if(g.getGroup_name().equals(group_name))
                return g.getGroup_code();
        }

        return "ERROR_404_G";
    }

}
