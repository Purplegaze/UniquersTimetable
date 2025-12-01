package view;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window.
 */
public class MainView extends JFrame {

    private TimetableView timetableView;
    private SearchPanel searchPanel;
    private WalkingTimeView walkingTimeView;
    private ExportImportPanel exportImportPanel;

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
        walkingTimeView = new WalkingTimeView();
        exportImportPanel = new ExportImportPanel();
    }

    private void layoutComponents() {
//        add(timetableView, BorderLayout.CENTER);
//        add(searchPanel, BorderLayout.EAST);
//
        add(timetableView, BorderLayout.CENTER);

        JPanel sideBar = new JPanel();
        sideBar.setLayout(new BorderLayout());
        sideBar.add(searchPanel, BorderLayout.CENTER);
        sideBar.add(walkingTimeView, BorderLayout.SOUTH);
        sideBar.add(exportImportPanel, BorderLayout.NORTH);

        add(sideBar, BorderLayout.EAST);
    }

    public SearchPanel getSearchPanel() {
        return searchPanel;
    }

    public TimetableView getTimetableView() {
        return timetableView;
    }

    public WalkingTimeView getWalkingTimeView() {return walkingTimeView;}

    public ExportImportPanel getExportImportPanel() {return exportImportPanel;}

    public void display() {
        setVisible(true);
    }
}
