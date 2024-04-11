package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import java.util.List;

public interface Strategy {

    void addTask(List<Server> serverList, Task task, SimulationManager simulationManager) throws InterruptedException;
}
