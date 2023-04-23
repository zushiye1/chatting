package cn.edu.sustech.cs209.chatting.server;

import java.util.List;

public class chatRoom {
    public String roomName;
    public List<String> roomMember;

    public chatRoom(final String roomName, final List<String> roomMember) {
        this.roomName = roomName;
        this.roomMember = roomMember;
    }
}
