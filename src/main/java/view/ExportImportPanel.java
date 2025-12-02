package view;

import interface_adapter.export.ExportTimetableController;
import interface_adapter.export.ExportTimetableViewModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FilenameFilter;
//import javax.jnlp.*;
import java.awt.FileDialog;

public class ExportImportPanel extends JPanel implements PropertyChangeListener {

    private JButton exportButton;
    private JButton importButton;

    private ExportTimetableController controller = null;
    private ExportTimetableViewModel viewModel;

    public ExportImportPanel() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(350, 40));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
        layoutComponents();
        setupEventHandlers();

    }

    public void setViewModel(ExportTimetableViewModel viewModel) {
        if (this.viewModel != null) {
            this.viewModel.removePropertyChangeListener(this);
        }

        this.viewModel = viewModel;
        viewModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "exported":
                announceSuccess();
                break;
            case "exportCancelled":
                announceCancel();
                break;
            case "error":
                handleError();
                break;
        }
    }

    private void announceSuccess() {
        if (viewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                "Successfully exported timetable to file:" + viewModel.getExportedPath(),
                "Export",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void announceCancel() {
        if (viewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                "Export cancelled.",
                "Export",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void handleError() {
        if (viewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                viewModel.getErrorMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
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

    private void onExportButtonClicked() {
        try {
            if (controller == null) {
                throw new NullPointerException("Controller is null");
            }

            String path = runFileDialog();
            controller.ExportTimetable(path);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    @Nullable
    private String runFileDialog() {
        FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Save", FileDialog.SAVE);
        String currentDirectory = System.getProperty("user.dir");
        fileDialog.setDirectory(currentDirectory);
        fileDialog.setFile(currentDirectory + "\\" + "courseExport.json");
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
            return null;
        } else {
            return fileDialog.getDirectory() + fileDialog.getFile();
        }

    }

    public void setController(ExportTimetableController controller) {
        this.controller = controller;
    }
}
