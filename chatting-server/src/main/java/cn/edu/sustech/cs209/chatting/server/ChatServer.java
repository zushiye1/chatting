package cn.edu.sustech.cs209.chatting.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private int port;
    private List<ClientHandler> clients;
    private List<String> names;

    public ChatServer(final int port) {
        this.port = port;
        this.clients = new ArrayList<>();
        this.names = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler clientHandler = new ClientHandler
                        (clientSocket, this);
                clients.add(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        }
    }
    public void ok(int type, ClientHandler sender){
        if(type == 1){
            System.out.println("New client connected: " + sender.getUsername());
        } else if (type == 2) {
            sender.sendMessage("you can not send empty message");
        }

    }

    public void createRoom(ClientHandler sender, List roomMember,String roomName){
        chatRoom room = new chatRoom(roomName, roomMember);
            for (ClientHandler client : clients) {
                if (roomMember.contains(client.getUsername())) {
                    client.addRoom(room);
                }
        }
    }
    public void roomchat(ClientHandler sender, String room, String message){
        for(chatRoom room1:sender.rooms){
            if(room1.roomName.equals(room)){
                for (ClientHandler client : clients) {
                    if(client!=sender){
                        if (room1.roomMember.contains(client.getUsername())) {
                            client.sendMessage("[" + sender.getUsername() + "]: " + message+"(room:"+room+")");
                        }
                    }
                }
                break;
            }
        }
    }
    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message+"(broadcast)");
            }
        }
    }

    public void sendPrivateMessage(String message, String recipient, ClientHandler sender) {
        boolean isS = false;
        for (ClientHandler client : clients) {
            if (client.getUsername().equals(recipient)) {
                isS = true;
                client.sendMessage("[" + sender.getUsername() + "]: " + message+"(private)");
            }
        }
        if(!isS){
            sender.sendMessage("user "+recipient+" left,you can not communicate with him");
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void ls(ClientHandler sender) {
        StringBuffer sb =new StringBuffer();
        sb.append("online list: ");
        int  i = 0;
        for (ClientHandler client : clients) {
            if (client != sender) {
                i++;
                sb.append(i+","+client.getUsername()+" ");
            }
        }
        sender.sendMessage(sb.toString());
    }
    public boolean checkName(String name){
        if(names.contains(name)){
            return false;
        }
        else {
            names.add(name);
            return true;
        }
    }
    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        ChatServer chatServer = new ChatServer(port);
        chatServer.start();
    }
}
