package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

public class Scheduler {
    private List<Server> serversList;

    private List<Thread> serversThreadList;
    private int maxNoServers = 4;
    private int maxTasksPerServer = 4;
    private Strategy strategy;
    private SimulationManager simulationManager;

    public Scheduler(int maxNoServers, int maxTasksPerServer, Strategy strategy, SimulationManager simulationManager){
        this.maxNoServers = maxNoServers;
        this.maxTasksPerServer = maxTasksPerServer;
        this.strategy = strategy;
        this.serversList = new ArrayList<>();
        this.serversThreadList = new ArrayList<>();
        this.simulationManager = simulationManager;

    }

    public void changeStrategy(SelectionPolicy selectionPolicy){
        if (selectionPolicy == SelectionPolicy.SHORTEST_TIME_QUEUE){
            strategy = new ShortestQueueStrategy();
        }else if(selectionPolicy == SelectionPolicy.SHORTEST_QUEUE){
            strategy = new TimeStrategy();
        }
    }

    public void initialiseServerThreads(){
        for(int i = 0 ; i < maxNoServers; i++){
            Server server = new Server(maxTasksPerServer, i, simulationManager);
            serversList.add(server);

            Thread serverThread = new Thread(server);
            serverThread.start();
            serversThreadList.add(serverThread);
        }
    }
    void dispatchTask(Task task) throws InterruptedException {
        strategy.addTask(serversList, task, simulationManager);
    }

    public List<Server> getServersList(){
        return this.serversList;
    }

}
