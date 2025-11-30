package view;

import entity.Timetable;
import interface_adapter.controller.ExportTimetableController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ExportImportPanel extends JPanel {

    private ExportTimetableController controller = null;

    public ExportImportPanel() {


        // TODO: add null check

        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(350, 0));

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));



    }

    private void onExportButtonClicked(Timetable timetable) {
        try {
            // Don't allow export if timetable is blank
            if (timetable.getBlocks().isEmpty()) {
                JOptionPane.showMessageDialog(this, "No classes to export.");
                return;
            }
            controller.ExportTimetable();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void setController(ExportTimetableController controller) {
        this.controller = controller;
    }
}
