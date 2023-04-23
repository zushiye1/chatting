package cn.edu.sustech.cs209.chatting.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ChatServer chatServer;
    private PrintWriter out;
    private BufferedReader in;
    private String username;

    public List<chatRoom> rooms;

    public void addRoom(chatRoom a){
        rooms.add(a);
        out.println("Someone invited you to join the chat room "+a.roomName+" the members of the chat room have "+a.roomMember.toString());
    }

    public ClientHandler(Socket clientSocket, ChatServer chatServer) {
        this.clientSocket = clientSocket;
        this.chatServer = chatServer;
        rooms = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            boolean setname = false;
            out.println("Enter your username:");
            while (setname == false){
                username = in.readLine();
                if(chatServer.checkName(username)){
                    out.println("Welcome to the chat room, " + username + "!");
                    setname = true;
                    }
                else {
                    out.println("Your username has already been taken, please choose a different one：");
                }
            }
            chatServer.ok(1,this);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.startsWith("/quit")) {
                    break;
                } else if (inputLine.startsWith("/ls")) {
                    chatServer.ls(this);
                } else if (inputLine.startsWith("/rls")){
                    this.lsRoom();
                } else if (inputLine.startsWith("/private")) {
                    String[] parts = inputLine.split(" ");
                    String recipient = parts[1];
                    if(parts.length>2){
                    String message = inputLine.substring(inputLine.indexOf(recipient) + recipient.length() + 1);
                    chatServer.sendPrivateMessage(message, recipient, this);}
                    else{
                        chatServer.ok(2,this);
                    }
                } else if (inputLine.startsWith("/createRoom")) {
                    String[] parts = inputLine.split(" ");
                    String roomname = parts[1];
                    List<String> member = new ArrayList<>();
                    for (int i = 2;i< parts.length;i++){
                        member.add(parts[i]);
                    }
                    member.add(username);
                    chatServer.createRoom(this,member,roomname);
                } else if (inputLine.startsWith("/room")) {
                    String[] parts = inputLine.split(" ");
                    String roomname = parts[1];
                    String message = inputLine.substring(inputLine.indexOf(roomname) + roomname.length() + 1);
                    chatServer.roomchat(this,roomname,message);
                } else {
                    chatServer.broadcast("[" + username + "]: " + inputLine, this);
                }
            }

            chatServer.removeClient(this);
            for(chatRoom room:rooms){
                room.roomMember.remove(username);
            }
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error in client handler: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void lsRoom() {
        StringBuffer sb =new StringBuffer();
        sb.append("online room: ");
        int  i = 0;
        for (chatRoom room:rooms) {
                i++;
                sb.append(i+","+room.roomName+" ");

        }
        this.sendMessage(sb.toString());
    }
}
