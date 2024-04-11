package GUI;

import BusinessLogic.*;
import ControlLogic.SimulationControl;
import Model.Server;
import Model.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;
import java.util.SplittableRandom;
import java.util.concurrent.BlockingQueue;

public class SimulationFrame extends JFrame {
    private JComboBox<Integer> maxServersComboBox;
    private JComboBox<Integer> maxTasksPerServerComboBox;
    private JComboBox<Integer> minArrivalTimeComboBox;
    private JComboBox<Integer> maxArrivalTimeComboBox;
    private JComboBox<Integer> minServiceTimeComboBox;
    private JComboBox<Integer> maxServiceTimeComboBox;
    private JComboBox<String> queueStrategyComboBox;
    private JTextField timeLimitField;
    private JTextField nrOfTasksField;

    private JPanel mainPanel;
    private JPanel optionPanel;
    private JPanel extensionPanel;

    private JButton startButton;

    private ArrayList<JTextArea> serversTextAreaList;
    private JPanel waitingTasksPanel;
    private JTextArea waitingTasksTextArea = null;

    private SimulationManager simulationManager;
    private JLabel timeLabel;
    public SimulationFrame(SimulationManager simulationManager){
        this.simulationManager = simulationManager;

        setTitle("Server simulation");
        setSize(2000, 1400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(6,2));
        mainPanel = new JPanel(new CardLayout());
        optionPanel = new JPanel(new GridLayout(10,2));
        startButton = new JButton("Start simulation");

        maxServersComboBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8});
        maxTasksPerServerComboBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8});
        minArrivalTimeComboBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20});
        maxArrivalTimeComboBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30});
        minServiceTimeComboBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15});
        maxServiceTimeComboBox = new JComboBox<>(new Integer[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30});
        queueStrategyComboBox = new JComboBox<>(new String[]{"Time strategy", "Shortest Queue strategy"});
        timeLimitField = new JTextField("60");
        nrOfTasksField = new JTextField("20");

        maxServersComboBox.setSelectedItem(4);
        maxTasksPerServerComboBox.setSelectedItem(4);
        minArrivalTimeComboBox.setSelectedItem(4);
        maxArrivalTimeComboBox.setSelectedItem(20);
        minServiceTimeComboBox.setSelectedItem(2);
        maxServiceTimeComboBox.setSelectedItem(15);


        optionPanel.add(new JLabel("Max servers"));
        optionPanel.add(maxServersComboBox);
        optionPanel.add(new JLabel("Max server tasks"));
        optionPanel.add(maxTasksPerServerComboBox);
        optionPanel.add(new JLabel("Minimum arrival time"));
        optionPanel.add(minArrivalTimeComboBox);
        optionPanel.add(new JLabel("Maximum arrival time"));
        optionPanel.add(maxArrivalTimeComboBox);
        optionPanel.add(new JLabel("Minimum service time"));
        optionPanel.add(minServiceTimeComboBox);
        optionPanel.add(new JLabel("Maximum service time"));
        optionPanel.add(maxServiceTimeComboBox);
        optionPanel.add(new JLabel("Queue strategy"));
        optionPanel.add(queueStrategyComboBox);
        optionPanel.add(new JLabel("Time limit"));
        optionPanel.add(timeLimitField);
        optionPanel.add(new JLabel("Nr. of tasks"));
        optionPanel.add(nrOfTasksField);

        optionPanel.add(startButton);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createExtensionPanel();
                setSimulationManagerSettings();
                simulationManager.startSimulation();
            }
        });

        mainPanel.add(optionPanel);
        add(mainPanel, BorderLayout.CENTER);

    }

    public void createExtensionPanel() {
        int activeServers = maxServersComboBox.getSelectedIndex() + 1;
        serversTextAreaList = new ArrayList<>();

        extensionPanel = new JPanel(new GridBagLayout());
        waitingTasksPanel = new JPanel(new GridLayout(3,1));
        for (int i = 0; i < activeServers; i++) {
            JLabel label = new JLabel("Server " + (i + 1));
            JTextArea serverTasks = new JTextArea(4,3);
            serverTasks.setLineWrap(true);
            serverTasks.setEditable(false);

            extensionPanel.add(label);
            extensionPanel.add(serverTasks);
            serversTextAreaList.add(i, serverTasks);
        }
        waitingTasksTextArea = new JTextArea();
        waitingTasksTextArea.setLineWrap(true);
        waitingTasksTextArea.setEditable(false);

        timeLabel = new JLabel("TIME: 0");
        waitingTasksPanel.add(timeLabel);

        waitingTasksPanel.add(new JLabel("Waiting tasks"));
        waitingTasksPanel.add(waitingTasksTextArea);

        remove(mainPanel);
        add(extensionPanel, BorderLayout.CENTER);
        add(waitingTasksPanel);
        revalidate();
        repaint();
    }

    public void updateWaitingTasksTextArea(ArrayList<Task> waitingTasks){
        waitingTasksTextArea.setText("");
        for(Task task : waitingTasks){
            waitingTasksTextArea.append(task.getTaskAsString() + " , ");
        }
    }

    public void updateServerTextArea(BlockingQueue<Task> serverTasks, int serverNumber){
        Task[] tasksArray = serverTasks.toArray(new Task[0]);
        serversTextAreaList.get(serverNumber).setText("");
        for(Task task : tasksArray){
            serversTextAreaList.get(serverNumber).append(task.getTaskAsString() + "\n");
        }
    }

    public ArrayList<JTextArea> getServersTextAreaList(){
        return this.serversTextAreaList;
    }

    public void updateTime(int time){
        this.timeLabel.setText("TIME: " + time);
    }

    public void setSimulationManager(SimulationManager simulationManager){
        this.simulationManager = simulationManager;
    }

    public JComboBox<Integer> getMaxServersComboBox() {
        return maxServersComboBox;
    }

    public JComboBox<Integer> getMaxTasksPerServerComboBox() {
        return maxTasksPerServerComboBox;
    }

    public JComboBox<Integer> getMinArrivalTimeComboBox() {
        return minArrivalTimeComboBox;
    }

    public JComboBox<Integer> getMaxArrivalTimeComboBox() {
        return maxArrivalTimeComboBox;
    }

    public JComboBox<Integer> getMinServiceTimeComboBox() {
        return minServiceTimeComboBox;
    }

    public JComboBox<Integer> getMaxServiceTimeComboBox() {
        return maxServiceTimeComboBox;
    }

    public JComboBox<String> getQueueStrategyComboBox() {
        return queueStrategyComboBox;
    }

    public void setSimulationManagerSettings(){
        simulationManager.setMaxServers(maxServersComboBox.getSelectedIndex() + 1);
        simulationManager.setMaxTasks(maxTasksPerServerComboBox.getSelectedIndex() + 1);
        simulationManager.setMinArrivalTime(minArrivalTimeComboBox.getSelectedIndex() + 1);
        simulationManager.setMaxArrivalTime(maxArrivalTimeComboBox.getSelectedIndex() + 1);
        simulationManager.setMinServiceTime(minServiceTimeComboBox.getSelectedIndex() +1);
        simulationManager.setMaxServiceTime(maxServiceTimeComboBox.getSelectedIndex() + 1);
        simulationManager.setTimeLimit(Integer.parseInt(timeLimitField.getText()));
        simulationManager.setNrOfTasks(Integer.parseInt(nrOfTasksField.getText()));
        if(queueStrategyComboBox.getSelectedIndex() == 0){
            simulationManager.setSelectionPolicy(SelectionPolicy.SHORTEST_TIME_QUEUE);
        }else simulationManager.setSelectionPolicy(SelectionPolicy.SHORTEST_QUEUE);
    }
}


