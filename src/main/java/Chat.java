import Bot.Bot;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static j2html.TagCreator.*;

/**
 * Created by Jan on 23.01.2017.
 */
public class Chat {

    private Map<Session, String> userUsernameMap;
    private Map<Session, String> userRoomMap;
    private LinkedList<String> roomList;
    private Bot bot;

    public Chat (){
        userUsernameMap = new ConcurrentHashMap<Session, String>();
        userRoomMap = new ConcurrentHashMap<>();
        roomList = new LinkedList<>();
        bot = new Bot();
        roomList.add("chatbot");
    }
    private void sendMessage(String sender, String message, String room){
        LinkedList<String> users = createRoomUsersList(room);
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session ->{
            try{
                if (userRoomMap.containsKey(session)) {
                    if (userRoomMap.get(session).equals(room)) {
                        session.getRemote().sendString(String.valueOf(new JSONObject()
                                .put("type", "message")
                                .put("userMessage", createHtmlMessageFromSender(sender, message))
                                .put("userlist", users))
                        );
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
    }
    public void broadcastMessage(Session session, String message){
        String sender = getNickName(session);
        String room = getRoom(session);
        sendMessage(sender, message, room);
    }

    public void serverSaysUserLeft(Session user){
        String sender = "Server";
        String message = getNickName(user) + " left the room.";
        String room = getRoom(user);
        removeUserFromRoom(user);
        sendMessage(sender, message, room);
    }

    public void serverSaysUserJoined(Session user){
        String sender = "Server";
        String message = getNickName(user) + " joined the room.";
        String room = getRoom(user);
        sendMessage(sender, message, room);
    }
    public void botAnswer(Session user, String question){
        String sender = "chatbot";
        String message = bot.askQuestion(question);
        sendMessage(sender, message, "chatbot");
    }
    private  String createHtmlMessageFromSender(String sender, String message){
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }


    private LinkedList<String> createRoomUsersList(String room){
        LinkedList<String> users = new LinkedList<>();
        userUsernameMap.keySet().forEach(
                user -> {
                    if (userRoomMap.containsKey(user)) {
                        if (userRoomMap.get(user).equals(room)) {
                            users.add(userUsernameMap.get(user));
                        }
                    }
                }
        );
        return users;
    }

    public void sendRoomList(Session session){
        try{
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("type", "roomlist")
                    .put("roomlist", roomList.toArray())
            ));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void sendTakenNicknamesToUser(Session session){
        try{
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("type", "userlist")
                    .put("userlist", userUsernameMap.values())
            ));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public String getRoom(Session user){
        return userRoomMap.get(user);
    }

    public String getNickName(Session user){
        return userUsernameMap.get(user);
    }

    public void removeUser(Session user){
        userUsernameMap.remove(user);
    }

    public void removeUserFromRoom(Session user){
        userRoomMap.remove(user);
    }

    public void addRoom(String room){
        roomList.add(room);
    }

    public void addUserNickname(Session user, String nickname){
        userUsernameMap.put(user, nickname);
    }
    public void addUserToRoom(Session user, String room){
        userRoomMap.put(user, room);
        if(!roomList.contains(room)){
            addRoom(room);
        }
    }
}
