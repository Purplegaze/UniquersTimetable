package view;

import interface_adapter.calculatewalkingtime.CalculateWalkingController;
import interface_adapter.calculatewalkingtime.CalculateWalkingState;
import interface_adapter.calculatewalkingtime.CalculateWalkingViewModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class WalkingTimeView extends JPanel implements ActionListener, PropertyChangeListener {

    private final String viewName = "walking time";

    private final CalculateWalkingViewModel walkingViewModel;
    private CalculateWalkingController walkingController;

    private final JTextArea walkingTimesArea = new JTextArea(12, 20);
    private final JLabel errorLabel = new JLabel("");

    private final JButton calculateButton = new JButton("Calculate Walking Time");

    public WalkingTimeView(CalculateWalkingViewModel walkingViewModel) {

        this.walkingViewModel = walkingViewModel;
        this.walkingViewModel.addPropertyChangeListener(this);

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Walking Time Results");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        walkingTimesArea.setEditable(false);
        walkingTimesArea.setLineWrap(true);
        walkingTimesArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(walkingTimesArea);

        calculateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        calculateButton.addActionListener(this);

        errorLabel.setForeground(Color.RED);

        this.add(title);
        this.add(Box.createVerticalStrut(5));
        this.add(scrollPane);
        this.add(Box.createVerticalStrut(5));
        this.add(errorLabel);
        this.add(Box.createVerticalStrut(10));
        this.add(calculateButton);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource().equals(calculateButton)) {
            System.out.println("Calculating walking time");
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (!"walkingTime".equals(evt.getPropertyName())) return;

        CalculateWalkingState state = (CalculateWalkingState) evt.getNewValue();

        walkingTimesArea.setText(state.getWalkingTimesText());

        if (state.getErrorMessage() != null) {
            errorLabel.setText(state.getErrorMessage());
        } else {
            errorLabel.setText("");
        }
    }

    public String getViewName() {
        return viewName;
    }

    public void setWalkingController(CalculateWalkingController controller) {
        this.walkingController = controller;
    }
}
