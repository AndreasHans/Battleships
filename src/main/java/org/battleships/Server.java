package org.battleships;
import org.jspace.*;
public class Server {

    public static String ip = "192.168.1.9";
    public static String port = "1000";

    public static void main(String[] args) {

        SpaceRepository repository = new SpaceRepository();
        Space lobby = new SequentialSpace();
        repository.add("lobby", lobby);

        repository.addGate("tcp://" + ip + ":" + port + "/lobby?keep");
        System.out.println("Lobby open");
    }
}
