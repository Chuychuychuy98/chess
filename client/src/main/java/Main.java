import client.Client;
import serverfacade.ServerFacade;
import ui.EscapeSequences;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String url;
        if (args.length == 0) {
            url = "localhost";
        }
        else if (args.length == 1) {
            url = args[0];
        }
        else if (args.length == 2) {
            url = args[0] + ":" + args[1];
        }
        else {
            System.out.println("Usage: chess [server name] [port]");
            return;
        }
        Client client = new Client();
        client.setServer(new ServerFacade(url, client));
        client.start();
    }


}