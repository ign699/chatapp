import org.eclipse.jetty.websocket.api.Session;
import Bot.Bot;
import org.json.JSONObject;

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
            default:
                break;
        }
    }

    private void handleNickname(){
        Chat.userUsernameMap.put(session, message.getText());
        Chat.sendRoomList(session);
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
        Chat.broadcastMessage("Server", Chat.getNickName(session) + " joined the chat", Chat.getRoom(session) );
    }

    private void handleMessage(){
        Chat.broadcastMessage(Chat.getNickName(session), message.getText(), Chat.getRoom(session));
        if(Chat.getRoom(session).equals("chatbot")){
            Bot bot = new Bot();
            Chat.broadcastMessage("chatbot", bot.askQuestion(message.getText()), Chat.getRoom(session));
        }
    }

    private void handleLeave(){
        String room = Chat.getRoom(session);
        Chat.userRoomMap.remove(session);
        Chat.broadcastMessage("server", Chat.getNickName(session) + " left the chat", room);
        Chat.sendRoomList(session);
    }
}
