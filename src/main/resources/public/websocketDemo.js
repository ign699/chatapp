/**
 * Created by Jan on 10.01.2017.
 */

var webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/chat/");

document.getElementById("send").style.display="none";
document.getElementById("userlist").style.display="none";
document.getElementById("leave").style.display="none";

webSocket.onmessage = function (msg) {
    var data = JSON.parse(msg.data);
    if(data.type==="roomlist"){
        displayRoomList(data)
    }
    if(data.type==="message"){
        updateChat(data);
    }
};

webSocket.onclose = function () {
    alert("WebSocket connection closed");
};

webSocket.onopen = function () {
    var nickname = prompt("Name","Your name");
    sendMessage({
        type:"nickname",
        text:nickname
    })

};

id("send").addEventListener("click", function(){
    sendMessage(id("message").value);
});

id("message").addEventListener("keypress", function(e){
    if (e.keyCode === 13){sendMessage({
        type:"message",
        text:e.target.value
    });}
});

id("room").addEventListener("click", function (e) {
    sendMessage({
        type:"room",
        text:id("message").value
    })
    showChat();
});
function showChat() {
    document.getElementById("send").style.display="block";
    document.getElementById("userlist").style.display="block";
    document.getElementById("room").style.display="none";
    document.getElementById("roomlist").style.display="none";
    document.getElementById("leave").style.display="block";
}
id("roomlist").addEventListener('click', function (e) {
    if(e.target.nodeName==="LI"){
        sendMessage({
            type:"room",
            text:e.target.innerText
        })
    }
    showChat();
});
function sendMessage(message) {
    if(message !== ""){
        webSocket.send(JSON.stringify(message));
        id("message").value = "";
    }
}
function displayRoomList(data) {
    data.roomlist.forEach(function(room){
        insert("roomlist", "<li>" + room + "</li>");
    });
}
function updateChat(data) {
    insert("chat", data.userMessage);
    id("userlist").innerHTML = "";
    data.userlist.forEach(function(user){
        insert("userlist", "<li>" + user + "</li>");
    });
}


function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

function id(id) {
    return document.getElementById(id);
}
