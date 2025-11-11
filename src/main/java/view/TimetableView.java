package view;

import java.awt.*;
import javax.swing.*;

public class TimetableView extends JPanel {

    public TimetableView(){
        setLayout(new BorderLayout());

        String[] weeklyTimetable = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        JPanel headerPanel = new JPanel(new GridLayout(1,weeklyTimetable.length));
        JPanel timePanel = new JPanel();
        JPanel timeSlots = new JPanel();

        int start_time = 9;
        int end_time = 21;
        int num_rows = end_time-start_time+1;

        timePanel.setLayout(new GridLayout(num_rows, 1));
        timeSlots.setLayout(new GridLayout(num_rows, weeklyTimetable.length - 1));

        for (String day : weeklyTimetable) {
            headerPanel.add(new JLabel(day));
        }

        add(headerPanel, BorderLayout.NORTH);
        add(timePanel, BorderLayout.WEST);
        add(timeSlots, BorderLayout.CENTER);

        for (int i = 9; i <= end_time; i++){
            for (int j = 0; j < weeklyTimetable.length; j++){
                if (j == 0){
                    JLabel timeLabel = new JLabel( i + ":00");
                    timePanel.add(timeLabel);
                }
                else {
                    JPanel slot = new JPanel();
                    slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    slot.setBackground(Color.WHITE);
                    timeSlots.add(slot);
                }
            }
        }

    }

}
