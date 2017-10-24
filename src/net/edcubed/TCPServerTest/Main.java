package net.edcubed.TCPServerTest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static ServerSocket tcpSocket;
    public static int bindPort = 26655;

    public static void closeSocket(Socket skt) {
        try {
            skt.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void addThread(Socket socket) {
        new Thread(new Runnable() {
            public void run() {
                ObjectOutputStream objectOutput;
                ObjectInputStream objectInput;
                try {
                    objectInput = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
                    objectOutput = new ObjectOutputStream(socket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    closeSocket(socket);
                    return;
                }
                Object message = "uninitialized";
                while (true) {
                    try {
                        message = objectInput.readObject();
                        if (((String) message).equalsIgnoreCase("i'm still here please don't leave me")) {
                            sendTCPData("i'll always be here for you :)", socket, objectOutput);
                        }else{
                            System.out.println(message);
                            sendTCPData("welcome!",socket, objectOutput);
                        }
                    }catch(IOException e) {
                        e.printStackTrace();
                        closeSocket(socket);
                        return;
                    }catch(ClassNotFoundException e) {
                        e.printStackTrace();
                        closeSocket(socket);
                        return;
                    }
                }
            }
        }).start();
    }

    public static void sendTCPData(Object message, Socket skt, ObjectOutputStream objectOutput) {
        try {
            objectOutput.writeObject(message);
            objectOutput.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            tcpSocket = new ServerSocket(bindPort);
            while (true) {
                try {
                    addThread(tcpSocket.accept());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}