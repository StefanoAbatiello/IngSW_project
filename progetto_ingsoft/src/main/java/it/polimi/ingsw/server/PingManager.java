package it.polimi.ingsw.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingManager extends TimerTask {

    private final ExecutorService executorService;

    public PingManager() {
        this.executorService= Executors.newCachedThreadPool();
    }

    @Override
    public void run() {
        ConnectionServer connectionServer = MainServer.getConnectionServer();
        if (!connectionServer.getObservers().isEmpty()) {
            for (PingObserver observer : connectionServer.getObservers()) {
                executorService.submit(observer);
            }
        }
    }
}
