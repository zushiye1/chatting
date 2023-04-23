package cn.edu.sustech.cs209.chatting.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private  String name;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    public ChatClient(final String serverAddress, final int port)
            throws IOException {
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void start() throws IOException {
        // 发送客户端名称给服务器

        // 启动一个线程来读取来自服务器的消息并立即打印
        new Thread(() -> {
            String response;
            try {
                while ((response = in.readLine()) != null) {
                    System.out.println(response);
                }
            } catch (IOException ex) {
                System.out.println("Error reading from server: "
                        + ex.getMessage());
            }
        }).start();

        // 从标准输入读取用户输入的消息并发送给服务器
        BufferedReader userInput = new
                BufferedReader(new InputStreamReader(System.in));
        String input;
        while ((input = userInput.readLine()) != null) {
            out.println(input);
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java ChatClient <server> <port>");
            System.exit(1);
        }

        String serverAddress = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(serverAddress, port);
        client.start();
    }
}


