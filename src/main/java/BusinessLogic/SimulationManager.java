package BusinessLogic;

import GUI.SimulationFrame;
import Model.Server;
import Model.Task;

import javax.naming.SizeLimitExceededException;
import java.io.FileWriter;
import java.io.IOError;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SimulationManager implements Runnable{
    private Scheduler scheduler;
    private SimulationFrame frame;
    private List<Task> tasksList = new ArrayList<>();
    private SelectionPolicy selectionPolicy;
    private int maxServers;
    private int maxTasks;
    private int minArrivalTime = 2;
    private int maxArrivalTime = 20;
    private int minServiceTime = 3;
    private int maxServiceTime = 7;
    private int timeLimit = 100;
    private int nrOfTasks = 20;
    private int totalServiceTime = 0;
    private int totalWaitingTime = 0;
    private AtomicInteger tasksProcessed = new AtomicInteger(0);
    private int currentTime = 0;

    private List<Integer> tasksAtCertainTime = new ArrayList<>();
    private List<Double> waitingTimes = new ArrayList<>();

    public SimulationManager(){

    }

    private void generateRandomTasks(){
        /*Task task1 = new Task(1, 2, 2);
        Task task2 = new Task(2, 3,3);
        Task task3 = new Task(3,4,3);
        Task task4 = new Task(4,10,2);
        tasksList.add(task1);
        tasksList.add(task2);
        tasksList.add(task3);
        tasksList.add(task4);*/


        Random random = new Random();

        for(int i = 0 ; i < nrOfTasks; i++){
            int arrivalTime = random.nextInt(maxArrivalTime - minArrivalTime + 1) + minArrivalTime;
            int serviceTime = random.nextInt(maxServiceTime - minServiceTime + 1) + minServiceTime;

            Task task = new Task(i, arrivalTime, serviceTime);
            tasksList.add(task);
        }
    }

    public void startSimulation(){

        this.generateRandomTasks();
        if(selectionPolicy == SelectionPolicy.SHORTEST_QUEUE){
            scheduler = new Scheduler(maxServers, maxTasks, new ShortestQueueStrategy(), this);
        }else scheduler = new Scheduler(maxServers, maxTasks, new TimeStrategy(), this);

        scheduler.initialiseServerThreads();
        Thread mainThread = new Thread(this);
        mainThread.start();
    }

    private void writeWaitingClients(FileWriter writer) throws IOException{
        writer.write("WAITING CLIENTS: ");
        for(Task task : tasksList){
            writer.append(task.getTaskAsString()).append(" , ");
        }
        writer.write("\n");
    }
    private void writeServersTasks(FileWriter writer) throws IOException {
        int serverListSize = scheduler.getServersList().size();

        for(int i = 0 ; i < serverListSize; i++){
            writer.write("SERVER" + (i+1) + ": " );
            BlockingQueue<Task> tasksQueue = scheduler.getServersList().get(i).getTasksQueue();

            if(!tasksQueue.isEmpty())
                for(Task task : scheduler.getServersList().get(i).getTasksQueue()){
                    writer.append(task.getTaskAsString()).append(" , ");
                }
            else writer.append("closed");
            writer.write("\n");
        }
    }

    private void addPeakHourTasks(){
        int totalTasksCurrently = 0;
        for(Server server : scheduler.getServersList()){
            for(Task task : server.getTasksQueue()){
                totalTasksCurrently++;
            }
        }
        tasksAtCertainTime.add(currentTime, totalTasksCurrently);
    }

    public int getPeakHour(){
        int max = 0;
        int maxIndex = 0;
        for(int i = 0 ; i < tasksAtCertainTime.size(); i++){
            if(tasksAtCertainTime.get(i) > max){
                max = tasksAtCertainTime.get(i);
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    @Override
    public void run() {
        frame.updateWaitingTasksTextArea((ArrayList<Task>) tasksList);
        try(FileWriter writer = new FileWriter("log.txt")){
            while(true){
                if(tasksProcessed.get() == nrOfTasks || currentTime >= timeLimit){
                    frame.updatePeakHourTimes();
                    writer.write("PEAK HOUR: " + getPeakHour() + "\n");
                    writer.write("AVERAGE SERVICE TIME: " + getAverageServiceTime() + " seconds!\n");
                    writer.write("AVERAGE WAITING TIME: " + getOverallAverageWaitingTime() + " seconds!\n");
                    frame.updateFinalAverageWaitingTime(getOverallAverageWaitingTime());
                    return;
                }

                frame.updateTime(currentTime);
                writer.write("TIME: " + currentTime + "\n");
                writer.flush();
                Iterator<Task> iterator = tasksList.iterator();
                while (iterator.hasNext()) {
                    Task task = iterator.next();
                    if (task.getArrivalTime() == currentTime) {
                        try {
                            scheduler.dispatchTask(task);
                            iterator.remove();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                writeServersTasks(writer);
                addPeakHourTasks();
                writeWaitingClients(writer);
                frame.updateWaitingTasksTextArea((ArrayList<Task>) tasksList);
                frame.updateAverageTimes();
                currentTime++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void setMaxServers(int maxServers) {
        this.maxServers = maxServers;
    }

    public void setMaxTasks(int maxTasks) {
        this.maxTasks = maxTasks;
    }

    public void setFrame(SimulationFrame frame) {
        this.frame = frame;
    }

    public void setMinArrivalTime(int minArrivalTime) {
        this.minArrivalTime = minArrivalTime;
    }

    public void setMaxArrivalTime(int maxArrivalTime) {
        this.maxArrivalTime = maxArrivalTime;
    }

    public void setMinServiceTime(int minServiceTime) {
        this.minServiceTime = minServiceTime;
    }

    public void setMaxServiceTime(int maxServiceTime) {
        this.maxServiceTime = maxServiceTime;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setSelectionPolicy(SelectionPolicy selectionPolicy) {
        this.selectionPolicy = selectionPolicy;
    }

    public static void main(String[] args) {
        SimulationManager simulationManager = new SimulationManager();
        SimulationFrame simulationFrame = new SimulationFrame(simulationManager);
        simulationManager.setFrame(simulationFrame);

        simulationFrame.setVisible(true);
    }

    public SimulationFrame getFrame(){
        return this.frame;
    }

    public void setNrOfTasks(int nrOfTasks) {
        this.nrOfTasks = nrOfTasks;
    }

    public void incrementTotalServiceTime(int inc){
        tasksProcessed.incrementAndGet();
        totalServiceTime += inc;
    }
    public void incrementTotalWaitingTime(int inc){
        totalWaitingTime += inc;
    }

    public double getAverageServiceTime(){
        if(tasksProcessed.get() == 0) return 0D;

        return (double) totalServiceTime / tasksProcessed.get();
    }

    public double getOverallAverageWaitingTime(){
        int count = 0;
        double totalWaitingTime = 0;

        for(Double entry : waitingTimes){
            count++;
            totalWaitingTime += entry;
        }

        if(count == 0) return 0D;

        return totalWaitingTime/count;
    }

    public double getAverageWaitingTime(){
        int currentWaitingTime = 0;
        int tasksInServers = 0;
        for(Server server : scheduler.getServersList()){
            if(server.hasEmptyQueue()) return 0D;

            Task[] serverQueue = server.getTasksQueue().toArray(new Task[0]);
            if(serverQueue.length < 1) continue;

            for(int i = 0 ; i < serverQueue.length; i++){
                currentWaitingTime += serverQueue[i].getServiceTime();
                tasksInServers++;
            }
        }
        if(tasksInServers == 0) return 0D;

        waitingTimes.add((double)currentWaitingTime / tasksInServers);
        return (double) currentWaitingTime / tasksInServers;
    }

    public int getCurrentTime(){
        return this.currentTime;
    }


}
