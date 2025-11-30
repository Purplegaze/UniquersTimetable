package view;

import entity.Timetable;
import interface_adapter.calculatewalkingtime.CalculateWalkingController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class WalkingTimeView extends JPanel {

    private CalculateWalkingController walkingController;
    private Timetable currentTimetable;

    private final JTextArea walkingTimesArea = new JTextArea(12, 35);
    private final JLabel errorLabel = new JLabel();
    private final JButton calculateButton = new JButton("Calculate Walking Times");

    public WalkingTimeView() {

        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel title = new JLabel("Walking Time Calculator");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        walkingTimesArea.setEditable(false);
        walkingTimesArea.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(calculateButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(walkingTimesArea), BorderLayout.CENTER);

        errorLabel.setForeground(Color.RED);
        add(errorLabel, BorderLayout.SOUTH);

        calculateButton.addActionListener(this::handleCalculateClicked);
    }

    /** Called by main to provide controller */
    public void setWalkingController(CalculateWalkingController controller) {
        this.walkingController = controller;
    }

    /** Called by main to update timetable when user adds/drops classes */
    public void setTimetable(Timetable timetable) {
        this.currentTimetable = timetable;
    }

    private void handleCalculateClicked(ActionEvent e) {
        if (walkingController == null) {
            showErrorMessage("Internal error: No controller is connected.");
            return;
        }

        if (currentTimetable == null || currentTimetable.getCourses().isEmpty()) {
            showErrorMessage("Error No timetable found. Add courses first.");
            return;
        }

        walkingController.execute(currentTimetable);
    }

    public void displayWalkingTimes(String text) {
        errorLabel.setText("");
        walkingTimesArea.setText(text);
    }

    public void showErrorMessage(String message) {
        walkingTimesArea.setText("");
        errorLabel.setText("Error: " + message);
    }
}
