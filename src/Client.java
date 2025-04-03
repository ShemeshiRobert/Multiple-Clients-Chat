import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private String clientUsername;

    public Client(Socket socket, String clientUsername) {
        try{
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintWriter(socket.getOutputStream(), true);
            this.clientUsername = clientUsername;
            output.println(clientUsername);
            output.flush();
        }catch(IOException e){
            closeEverything(socket,input,output);
            e.printStackTrace();
        }
    }

    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        while(socket.isConnected()){
            String message = scanner.nextLine();
            output.println(clientUsername + ": "+message);
            output.flush();

        }
    }

    public void listenForMessage(){
        new Thread(new Runnable(){
            public void run(){
                String messageGroup;
                while (socket.isConnected()){
                    try {
                        {
                            messageGroup = input.readLine();
                            System.out.println(messageGroup);
                        }
                    } catch (IOException e){
                        closeEverything(socket, input, output);
                    }
                }
            }

        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader input, PrintWriter output){
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
