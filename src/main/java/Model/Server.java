package Model;

import BusinessLogic.SimulationManager;
import GUI.SimulationFrame;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable{
    private BlockingQueue<Task> tasksQueue;
    private AtomicInteger waitingPeriod;
    private int serverNumber;
    private int tasksProcessed = 0;
    private SimulationManager simulationManager;


    public Server(int maxServerCapacity, int serverNumber, SimulationManager simulationManager){
        this.waitingPeriod = new AtomicInteger();
        this.tasksQueue = new LinkedBlockingQueue<>(maxServerCapacity);
        this.serverNumber = serverNumber;
        this.simulationManager = simulationManager;
    }

    @Override
    public void run() {
        while (true) {
            //printServerDetails();
            try {
                simulationManager.getFrame().updateServerTextArea(tasksQueue, serverNumber);
                if (!tasksQueue.isEmpty()) {
                    Task currentTask = tasksQueue.peek(); // Peek the first task
                    int currentTaskProcessingTime = currentTask.getServiceTime();
                    for (int i = currentTaskProcessingTime; i > 0; i--) {
                        // Sleep for 1 second
                        Thread.sleep(1000);
                        // Update waiting period
                        if (waitingPeriod.get() > 0) {
                            waitingPeriod.decrementAndGet();
                        }
                        currentTask.decrementServiceTime();
                        if(currentTask.getServiceTime() > 0) simulationManager.getFrame().updateServerTextArea(tasksQueue, serverNumber);
                    }
                    int waitingTime = simulationManager.getCurrentTime() - currentTask.getArrivalTime();
                    simulationManager.incrementTotalWaitingTime(waitingTime);
                    simulationManager.incrementTotalServiceTime(currentTaskProcessingTime);
                    tasksProcessed++;
                    tasksQueue.poll(); // Remove the task from the queue after service time
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }



    public void addTask(Task task) throws InterruptedException {
        if(task == null){
            return;
        }
        tasksQueue.put(task);
        waitingPeriod.addAndGet(task.getServiceTime());
    }

    public void printServerDetails(){
        if(tasksQueue.isEmpty()){
            System.out.println("SERVER" + serverNumber + " : EMPTY" + " processed : " + tasksProcessed);
            return;
        }
        System.out.println("SERVER" + serverNumber + ": ");
        for(Task task : tasksQueue){
            System.out.print(task.getTaskAsString() + "  ,  ");
        }
        System.out.println();
    }

    public boolean hasEmptyQueue(){
        return tasksQueue.isEmpty();
    }

    public int getQueueLength(){
        return this.tasksQueue.size();
    }

    public int getWaitingPeriod(){
        return this.waitingPeriod.get();
    }

    public BlockingQueue<Task> getTasksQueue(){
        return this.tasksQueue;
    }
}
