package sample.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.rmi.server.UID;

public class UserSocThread extends Thread {
    String name;
    UID uid;
    Socket socket;
    public  UserSocThread(Socket socket){
        this.socket = socket;
        uid = new UID();
        name = "noName";
        starListenSocket();
    }
    private void starListenSocket(){
        Thread th = new Thread(() -> {
            try {
                InputStream in = socket.getInputStream();
                try {
                    while (true){
                        byte[] buffer = new byte[1024];
                        int readed = in.read(buffer);
                        if (readed == -1)
                        {
                            socketList.remove(socket);
                            break;
                        }
                        String msg = new String(buffer, 0, readed);
                        if(!msg.isEmpty())
                            sendAll(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                socketList.remove(socket);
            }
        });

        th.setDaemon(true);
        th.start();
    }
