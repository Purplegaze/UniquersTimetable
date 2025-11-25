package view;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 */
public class MainView extends JFrame {

    private TimetableView timetableView;
    private SearchPanel searchPanel;

    public MainView() {
        setTitle("UofT Timetable Builder");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLayout(new BorderLayout(10, 10));

        initializeComponents();
        layoutComponents();

        setLocationRelativeTo(null);
    }

    private void initializeComponents() {
        timetableView = new TimetableView();
        searchPanel = new SearchPanel();
    }

    private void layoutComponents() {
        add(timetableView, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.EAST);
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    public TimetableView getTimetableView() {
        return timetableView;
    }

    public void display() {
        setVisible(true);
    }
}
