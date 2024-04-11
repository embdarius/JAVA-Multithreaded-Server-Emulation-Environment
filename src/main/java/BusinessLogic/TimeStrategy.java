package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import java.util.List;

public class TimeStrategy implements Strategy{

    @Override
    public void addTask(List<Server> serverList, Task task, SimulationManager simulationManager) throws InterruptedException {
        Server bestServer = null;
        int minWaitingTime = Integer.MAX_VALUE;

        for(Server server : serverList){
            if(server.hasEmptyQueue()){
                server.addTask(task);
                return;
            }
            int currentServerWaitingTime = server.getWaitingPeriod();
            if(currentServerWaitingTime < minWaitingTime){
                minWaitingTime = currentServerWaitingTime;
                bestServer = server;
            }
        }
        if(bestServer != null) bestServer.addTask(task);
        else System.out.println("Couldn't find best strategy");
    }
}
