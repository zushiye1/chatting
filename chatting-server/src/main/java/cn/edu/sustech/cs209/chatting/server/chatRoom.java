package cn.edu.sustech.cs209.chatting.server;

import java.util.ArrayList;
import java.util.List;

public class chatRoom {
    public String roomName;
    public List<String> roomMember;

    public chatRoom(String roomName, List<String> roomMember) {
        this.roomName = roomName;
        this.roomMember = roomMember;
    }
}
