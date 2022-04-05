package sample;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader bufferedReader; // reads data from the client
    private BufferedWriter bufferedWriter; // wraps the socket's output stream so we can send messages to the client

    public Server(ServerSocket serverSocket) {
        try{
            this.serverSocket = serverSocket;
            this.socket = serverSocket.accept(); //the accept method is a blocking method. It halts until a client connects to the server. It returns the socket object used to communicate with our client
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); // reads input from who we're connected to
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e){
            System.out.println("Error creating server.");
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessageToClient(String messageToClient){ // this method is called from the controller when the send button is clicked
        try{
            bufferedWriter.write(messageToClient);
            bufferedWriter.newLine(); //confirms that it is the end of the message
            bufferedWriter.flush();
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Error sending message to the client");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }

    }

    public void receiveMessageFromClient(VBox vBox){
        // needs to be run on a separate thread so that our whole program is not blocked by constantly for messages from the client
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()){
                    try{
                        String messageFromClient = bufferedReader.readLine();
                        Controller.addLabel(messageFromClient, vBox);
                    } catch (IOException e){
                        e.printStackTrace();
                        System.out.println("Error receiving message from the client");
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break; // if there's an error break out
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{

            if(bufferedReader != null){ // closes the underlying streams as well
                bufferedReader.close();
            }

            if(bufferedWriter != null){ // closes the underlying streams as well
                bufferedWriter.close();
            }

            if(socket != null){
                socket.close();
            }

        } catch (IOException e){
            e.printStackTrace();
        }
    }

}