package view;

import entity.Timetable;
import interface_adapter.controller.ExportTimetableController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
//import javax.jnlp.*;
import java.awt.FileDialog;

public class ExportImportPanel extends JPanel {

    private JButton exportButton;
    private JButton importButton;

    private ExportTimetableController controller = null;

    public ExportImportPanel() {

        // TODO: add null check

        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(350, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
        layoutComponents();
        setupEventHandlers();



    }

    private void initializeComponents() {
        exportButton = new JButton("Export");
        importButton = new JButton("Import");
    }

    private void layoutComponents() {
        add(exportButton, BorderLayout.WEST);
        add(importButton, BorderLayout.EAST);
    }

    private void setupEventHandlers() {
        exportButton.addActionListener(e -> onExportButtonClicked());
        // importButton.addActionListener(e -> onImportButtonClicked());

    }

    private class ExtensionFilenameFilter implements FilenameFilter {
        private String extension;
        public ExtensionFilenameFilter(String extension) {
            this.extension = extension;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith("." + extension);
        }
    }


    private void onExportButtonClicked() {
        try {
//            controller.ExportTimetable();
//            File tempFile = new File("./testExport.json");
//            if (tempFile.exists()) {
//                InputStream fis = new FileInputStream(tempFile);
//
//            }
            FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save", FileDialog.SAVE);
            String currentDirectory = System.getProperty("user.dir");
            System.out.println(currentDirectory);
            fileDialog.setDirectory(currentDirectory);
            //FilenameFilter jsonFilter = new ExtensionFilenameFilter("json"); // Implementation doesn't work on Windows.
            //fileDialog.setFilenameFilter(jsonFilter);
            fileDialog.setFile(currentDirectory + "\\" + "courseExport.json");
            fileDialog.setVisible(true);
            String path = fileDialog.getDirectory() + fileDialog.getFile();

            controller.ExportTimetable(path);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void setController(ExportTimetableController controller) {
        this.controller = controller;
    }
}
