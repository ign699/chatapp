import org.eclipse.jetty.websocket.api.Session;

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

    public void handleMessage(){
        if(message.getType().equals("nickname")){
            Chat.userUsernameMap.put(session, message.getText());
            Chat.sendRoomList(session);
        }
        if(message.getType().equals("room")){
            Chat.userRoomMap.put(session, message.getText());
            if(!Chat.roomList.contains(message.getText())){
                Chat.roomList.add(message.getText());
            }
            Chat.broadcastMessage("Server", Chat.getNickName(session) + " joined the chat", Chat.getRoom(session) );
        }
        if(message.getType().equals("message")){
            Chat.broadcastMessage(Chat.getNickName(session), message.getText(), Chat.getRoom(session));
        }
    }
}
