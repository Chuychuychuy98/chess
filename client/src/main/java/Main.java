import client.Client;
import serverfacade.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url;
        if (args.length == 1) {
            url = "localhost";
        }
        else if (args.length == 2) {
            url = args[1];
        }
        else if (args.length == 3) {
            url = args[1] + ":" + args[2];
        }
        else {
            System.out.println("Usage: chess [server name] [port]");
            return;
        }
        Client client = new Client(new ServerFacade(url));
        client.start();
    }


}