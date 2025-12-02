package view;

import interface_adapter.calculatewalkingtime.CalculateWalkingController;
import interface_adapter.calculatewalkingtime.CalculateWalkingState;
import interface_adapter.calculatewalkingtime.CalculateWalkingViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WalkingTimeView extends JPanel implements PropertyChangeListener {


    private CalculateWalkingController walkingController;
    private final CalculateWalkingViewModel viewModel;

    private final JTextArea walkingTimesArea = new JTextArea(12, 35);
    private final JLabel errorLabel = new JLabel();
    private final JButton calculateButton = new JButton("Calculate Walking Times");

    public WalkingTimeView(CalculateWalkingViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addPropertyChangeListener(this);

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

    private void handleCalculateClicked(ActionEvent e) {
        if (walkingController == null) {
            showErrorMessage("Internal error: No controller connected.");
            return;
        }

        walkingController.execute();
    }

    public void displayWalkingTimes(String text) {
        errorLabel.setText("");
        walkingTimesArea.setText(text);
    }

    public void showErrorMessage(String message) {
        walkingTimesArea.setText("");
        errorLabel.setText("Error: " + message);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        CalculateWalkingState state = viewModel.getState();

        if (state.getErrorMessage() != null && !state.getErrorMessage().isEmpty()) {
            showErrorMessage(state.getErrorMessage());
        } else {
            displayWalkingTimes(state.getWalkingTimesText());
        }
    }

    public CalculateWalkingViewModel getViewModel() {
        return viewModel;
    }
}
