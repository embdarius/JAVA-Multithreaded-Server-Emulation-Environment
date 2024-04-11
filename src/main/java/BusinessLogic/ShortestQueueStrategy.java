package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import java.util.List;

public class ShortestQueueStrategy implements Strategy{

    @Override
    public void addTask(List<Server> serverList, Task task, SimulationManager simulationManager) throws InterruptedException {
        System.out.println("SHORTEST QUEUE");
        if(serverList.isEmpty()){
            System.out.println("ERROR: server list empty");
            return;
        }

        Server bestServer = null;
        int minQueueLength = Integer.MAX_VALUE;

        for(Server server : serverList){
            if(server.hasEmptyQueue()){
                server.addTask(task);
                return;
            }
            int currentQueueLength = server.getQueueLength();
            if(currentQueueLength < minQueueLength){
                minQueueLength = currentQueueLength;
                bestServer = server;
            }
        }
        if(bestServer != null) bestServer.addTask(task);
        else System.out.println("ERROR : couldn't find best server");
    }
}
