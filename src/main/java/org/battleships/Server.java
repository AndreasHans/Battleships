package org.battleships;
import org.jspace.*;

import java.io.IOException;

public class Server {

    static String ip = "82.211.207.77";
    static String port = "1000";

    static Space server;
    static Space games;

    public static void main(String[] args) throws InterruptedException, IOException {

        SpaceRepository repository = new SpaceRepository();
        server = new SequentialSpace();
        games = new SequentialSpace();
        repository.add("server", server);
        repository.addGate("tcp://" + ip + ":" + port + "/?keep");
        System.out.println("Server started");

        while (true) {
            Object[] request = server.get(new FormalField(String.class), new FormalField(String.class), new FormalField(String.class), new FormalField(String.class));
            String requestType = request[0].toString();
            if (requestType.equals("create")) {
                createGame(request);
            } else if (requestType.equals("join")) {
                joinGame(request);
            }
        }
    }

    private static void createGame(Object[] request) throws InterruptedException, IOException {
        String playerIp = request[1].toString();
        String playerPort = request[2].toString();
        String gameId = generateGameId();

        Space responseSpace = new RemoteSpace("tcp://" + playerIp + ":" + playerPort + "/response?conn");
        responseSpace.put(gameId);
        server.get(new ActualField(gameId), new ActualField("ok"));
        games.put(gameId, playerIp, playerPort);
    }

    private static void joinGame(Object[] request) throws InterruptedException, IOException {
        String playerIp = request[1].toString();
        String playerPort = request[2].toString();
        String gameId = request[3].toString();

        Space responseSpace = new RemoteSpace("tcp://" + playerIp + ":" + playerPort + "/response?conn");
        Object[] gameInfo = games.getp(new ActualField(gameId), new FormalField(String.class), new FormalField(String.class));
        boolean gameExists = gameInfo != null;
        if (gameExists) {
            String opponentIp = gameInfo[1].toString();
            String opponentPort = gameInfo[2].toString();
            responseSpace.put("ok", opponentIp, opponentPort);
            responseSpace = new RemoteSpace("tcp://" + opponentIp + ":" + opponentPort + "/response?conn");
            responseSpace.put(playerIp, playerPort);
        } else {
            responseSpace.put("fail", "", "");
        }
    }

    private static String generateGameId() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String id = "";
        for (int i = 0; i < 5; i++) {
            int index = (int) (alphabet.length() * Math.random());
            id = id + alphabet.charAt(index);
        }
        return id;
    }
}