package it.polimi.ingsw;

import it.polimi.ingsw.server.MainServer;

import java.util.Scanner;

public class App {

    private static final Scanner scanner=new Scanner(System.in);

    public static void main( String[] args )
    {
        System.out.println("WELCOME IN:");
        System.out.println("MASTERS OF RENAISSANCE!");
        System.out.println("Would you like to play or to host server?");
        System.out.println("1)client");
        System.out.println("2)server");
        String input=scanner.nextLine();
        if(input.equalsIgnoreCase("server"))
            MainServer.main(args);
        else
            MainClient.main(args);
    }
}
