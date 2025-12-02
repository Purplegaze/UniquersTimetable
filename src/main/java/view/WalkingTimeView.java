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

        JLabel title = new JLabel("Walking Time Calculator");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        walkingTimesArea.setEditable(false);
        walkingTimesArea.setFont(new Font("Arial", Font.PLAIN, 14));
        walkingTimesArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(calculateButton, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(walkingTimesArea), BorderLayout.CENTER);

        errorLabel.setForeground(Color.RED);
        add(errorLabel, BorderLayout.SOUTH);

        calculateButton.addActionListener(this::handleCalculateClicked);
    }

    public void setWalkingController(CalculateWalkingController controller) {
        this.walkingController = controller;
    }

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

        boolean hasLongWalk = text.contains("[LONG_WALK_WARNING]");

        String cleaned = text.replace("[LONG_WALK_WARNING]", "");
        walkingTimesArea.setText(cleaned);

        walkingTimesArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        if (hasLongWalk) {
            JOptionPane.showMessageDialog(
                    this,
                    "You have back-to-back classes with more than 10 minutes of walking.",
                    "Back-to-back warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    public void showErrorMessage(String message) {
        walkingTimesArea.setText("");
        errorLabel.setText("Error: " + message);
        JOptionPane.showMessageDialog(
                this,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
