package sample.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.application.Platform;

public class TestServer implements  AutoCloseable{
    private Communicator delegate;
    private  ServerSocket serverSocket;
    private List<Socket> socketList;
    Thread thrListen;

    public TestServer(Communicator delegate) throws IOException {
        this.delegate = delegate;
        socketList = new CopyOnWriteArrayList<>();
        try {
            serverSocket = new ServerSocket(60000);
        }catch (IOException e){
            e.printStackTrace();
            delegate.getMessage(e.toString());
            return;
        }
        startListen();
    }

    private void startListen(){
        thrListen = new Thread(() -> {
            while (true) {
                Socket s = null;
                try {
                    s = serverSocket.accept();
                    socketList.add(s);
                    starListenSocket(s);
                } catch (IOException e) {
                    e.printStackTrace();
                    delegate.getMessage(e.toString());
                }
            }
        });
        thrListen.setDaemon(true);
        thrListen.start();
    }
    private void starListenSocket(Socket s){
        Thread th = new Thread(() -> {
            try {
                InputStream in = s.getInputStream();
//                InputStreamReader inputReader = new InputStreamReader(in);
                try /*(BufferedReader bReader = new BufferedReader(inputReader))*/{
                    while (true){
                        byte[] buffer = new byte[1024];
                        int readed = in.read(buffer);
                        if (readed == -1)
                        {
                            socketList.remove(s);
                            break;
                        }
//                        String msg = bReader.readLine();
                        String msg = new String(buffer, 0, readed);
                        if(!msg.isEmpty())
                            sendAll(msg);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                socketList.remove(s);
            }
        });

        th.setDaemon(true);
        th.start();

    }
    synchronized public void sendAll(String msg){
        for(Socket s: socketList){
            BufferedWriter out;
            try {
                out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
                out.write(msg);
                out.flush();

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        delegate.getMessage(msg);
                    }
                });
            } catch (IOException e){
                e.printStackTrace();
                socketList.remove(s);
            }
        }
    }

    @Override
    public void close() throws Exception {

        thrListen.interrupt();
    }
}
