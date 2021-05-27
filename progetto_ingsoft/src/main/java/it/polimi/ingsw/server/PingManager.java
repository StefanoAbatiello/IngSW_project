package it.polimi.ingsw.server;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingManager extends TimerTask {

    private final ExecutorService executorService;
    private final MainServer server;

    public PingManager(MainServer server) {
        this.executorService= Executors.newCachedThreadPool();
        this.server=server;
    }

    @Override
    public void run() {
        ConnectionServer connectionServer = server.getConnectionServer();
        if (!connectionServer.getObservers().isEmpty()) {
            for (PongObserver observer : connectionServer.getObservers()) {
                executorService.submit(observer);
            }
        }
    }
}
