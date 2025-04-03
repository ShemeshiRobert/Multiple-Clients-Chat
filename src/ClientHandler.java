import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clients = new ArrayList<>();
    Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try{
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
            this.clientUsername = input.readLine();
            clients.add(this);
            broadcastMessage("SERVER" + clientUsername + "has entered the chat.");
        }catch(IOException e){
            closeEverything(socket,input,output);
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        String message;
        while(socket.isConnected()){
            try{
                message = input.readline();
                broadcastMessage(message);
            }catch(IOException e){
                closeEverything(socket,input,output);
                break;
            }
        }
    }

    public void broadcastMessage(String message){
        for (ClientHandler client : clients) {
            try{
                if(!client.clientUsername.equals(clientUsername)){
                    client.output.println(message);
                    client.output.flush();
                }
            }catch(IOException){
                closeEverything(socket,input,output);
            }
        }
    }

    public void removeClientHandler(){
        clients.remove(this);
        broadcastMessage("SEVER: " + clientUsername + "has left the chat.");
    }

    public void closeEverything(Socket socktet, BufferedReader input, PrintWriter output){
        removeClientHandler();
        try{
            if (input!=null){
                input.close();
            }
            if (output!=null){
                output.close();
            }
            if(socket!=null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
