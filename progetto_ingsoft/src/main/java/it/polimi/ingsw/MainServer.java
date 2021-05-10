package it.polimi.ingsw;


import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {
    private final ConnectionServer connectionServer;

    //this map identifies a client from their id;
    private final Map<Integer, Client> clientFromId;
    private final Map<Integer,String> nameFromId;
    private final Map<Integer,Integer> fromClientIDToGameID;

    public MainServer(int port) {
        this.connectionServer= new ConnectionServer(port,this);
        this.fromClientIDToGameID = new HashMap<>();
        this.clientFromId=new HashMap<>();
        this.nameFromId = new HashMap<>();
    }

    public static void main(String[] args) {
        System.out.println("I'm the Server, welcome!");
        String hostname=args[0];
        int portNumber = Integer.parseInt(args[1]);
        if(portNumber<0||(portNumber>0 && portNumber<1024)){
            System.err.println("Port number not valid, restart the program");
            System.exit(0);
        }
        System.err.println("Creating socket server");
        MainServer mainServer=new MainServer(portNumber);
        /*ExecutorService executorService= Executors.newCachedThreadPool();
        executorService.submit(mainServer.connectionServer);
         */
        mainServer.getConnectionServer().startServer();
    }

    //TODO controlla se getconn ha bisogno di sinc
    public ConnectionServer getConnectionServer() {
        return connectionServer;
    }

    public void quitActiveConnections(){
        Scanner scanner=new Scanner(System.in);
        while (true){
            if(scanner.next().equalsIgnoreCase("quit")){
                getConnectionServer().setActive(false);
                System.exit(0);
                break;
            }
        }
    }

   public GameHandler getGameByClientID(int id){
        return clientFromId.get(id).getGameHandler();
   }


}
