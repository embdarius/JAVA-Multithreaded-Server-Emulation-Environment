package Model;

public class Task {
    private int ID;
    private int arrivalTime;
    private int serviceTime = 0;
    public Task(int id, int arrivalTime, int serviceTime){
        this.ID = id;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    public Task getTask(){
        return this;
    }

    public int getServiceTime(){
        return serviceTime;
    }

    public int getArrivalTime(){
        return this.arrivalTime;
    }

    public String getTaskAsString(){
        return "(" + this.ID + " , " + this.arrivalTime + " , " + this.serviceTime + ")";
    }

    public void decrementServiceTime(){
        if(this.serviceTime <= 0) return;

        this.serviceTime--;
    }
}
