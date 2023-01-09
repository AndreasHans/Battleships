package org.battleships;

import org.jspace.Space;
import org.jspace.PileSpace;
import org.jspace.SpaceRepository;

import java.net.URI;


public class Server {

    public static void main(String[] args) {
        try {

            SpaceRepository repository = new SpaceRepository();

            Space boardA = new PileSpace();
            Space boardB = new PileSpace();

            repository.add("boardA", boardA);
            repository.add("boardB", boardB);

            // Set URI
            String addressA = "tcp://192.168.1.9:1000/boardA?keep";
            String addressB = "tcp://192.168.1.9:1000/boardB?keep";

            // Open gates
            System.out.println("Opening gates");
            repository.addGate(addressA);
            repository.addGate(addressB);

            // Select player 1 and 2
            boardA.put(1);
            boardA.put(2);

            // Start game
            System.out.println("Starting game");
            boardA.put("token");

        } catch (Exception e) { }
    }
}
