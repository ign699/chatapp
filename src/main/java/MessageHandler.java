import org.eclipse.jetty.websocket.api.Session;
import Bot.Bot;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import static j2html.TagCreator.*;

/**
 * Created by Jan on 21.01.2017.
 */
public class MessageHandler {
    private Message message;
    private Session session;

    public MessageHandler(Message message, Session session){
        this.message = message;
        this.session = session;
    }

    public void actOnMessage(){
        switch(message.getType()){
            case "nickname":
                handleNickname();
                break;
            case "room":
                handleRoom();
                break;
            case "message":
                handleMessage();
                break;
            case "leave":
                handleLeave();
                break;
            case "getnames":
                handleNameList();
                break;
            case "onclose":
                handleOnClose();
                break;
            default:
                break;
        }
    }

    private void handleNickname(){
        Chat.userUsernameMap.put(session, message.getText());
        sendRoomList(session);
    }

    private void handleOnClose(){
        String name = Chat.getNickName(session);
        String room = Chat.getRoom(session);
        Chat.userUsernameMap.remove(session);
        Chat.userRoomMap.remove(session);
        broadcastMessage("Server", name + " left the chat", room);
    }
    private void handleNameList(){
        try{
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("type", "userlist")
                    .put("userlist", Chat.userUsernameMap.values())
            ));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void handleRoom(){
        Chat.userRoomMap.put(session, message.getText());
        if(!Chat.roomList.contains(message.getText())){
            Chat.roomList.add(message.getText());
        }
        broadcastMessage("Server", Chat.getNickName(session) + " joined the chat", Chat.getRoom(session) );
    }
    private void handleMessage(){
        broadcastMessage(Chat.getNickName(session), message.getText(), Chat.getRoom(session));
        if(Chat.getRoom(session).equals("chatbot")){
            Bot bot = new Bot();
            broadcastMessage("chatbot", bot.askQuestion(message.getText()), Chat.getRoom(session));
        }
    }

    private void handleLeave(){
        String room = Chat.getRoom(session);
        Chat.userRoomMap.remove(session);
        broadcastMessage("server", Chat.getNickName(session) + " left the chat", room);
        sendRoomList(session);
    }

    private void broadcastMessage(String sender, String message, String room){
        LinkedList<String> users = createRoomUsersList(room);
        Chat.userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session ->{
            try{
                if (Chat.userRoomMap.containsKey(session)) {
                    if (Chat.userRoomMap.get(session).equals(room)) {
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

    private  String createHtmlMessageFromSender(String sender, String message){
        return article().with(
                b(sender + " says:"),
                p(message),
                span().withClass("timestamp").withText(new SimpleDateFormat("HH:mm:ss").format(new Date()))
        ).render();
    }


    private LinkedList<String> createRoomUsersList(String room){
        LinkedList<String> users = new LinkedList<>();
        Chat.userUsernameMap.keySet().forEach(
                user -> {
                    if (Chat.userRoomMap.containsKey(user)) {
                        if (Chat.userRoomMap.get(user).equals(room)) {
                            users.add(Chat.userUsernameMap.get(user));
                        }
                    }
                }
        );
        return users;
    }

    private void sendRoomList(Session session){
        try{
            session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("type", "roomlist")
                    .put("roomlist", Chat.roomList.toArray())
            ));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
