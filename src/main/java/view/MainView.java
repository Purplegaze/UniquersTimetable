package view;

import javax.swing.*;
import java.awt.*;

/**
 * Timetable on the left and Search panel on the right
 */
public class MainView extends JFrame {
    private TimetableView timetableView;
    private SearchPanel searchPanel;

    public MainView() {
        setTitle("UofT Timetable Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLayout(new BorderLayout(10, 10));

        timetableView = new TimetableView();
        searchPanel = new SearchPanel(timetableView);

        add(timetableView, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.EAST);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainView());
    }
}