package view;

import interface_adapter.export.ExportTimetableController;
import interface_adapter.export.ExportTimetableViewModel;
import interface_adapter.importsections.ImportTimetableController;
import interface_adapter.importsections.ImportTimetableViewModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.FileDialog;

public class ExportImportPanel extends JPanel implements PropertyChangeListener {

    private final String EXPORT_DIRECTORY = System.getProperty("user.dir");
    private final String DEFAULT_EXPORT_NAME = "courseExport.json";

    private JButton exportButton;
    private JButton importButton;

    private ExportTimetableController exportController = null;
    private ExportTimetableViewModel exportViewModel;

    private ImportTimetableController importController;
    private ImportTimetableViewModel importViewModel;

    public ExportImportPanel() {
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(350, 40));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initializeComponents();
        layoutComponents();
        setupEventHandlers();

    }

    public void setExportViewModel(ExportTimetableViewModel exportViewModel) {
        if (this.exportViewModel != null) {
            this.exportViewModel.removePropertyChangeListener(this);
        }

        this.exportViewModel = exportViewModel;
        exportViewModel.addPropertyChangeListener(this);
    }

    public void setImportViewModel(ImportTimetableViewModel importViewModel) {
        if (this.importViewModel != null) {
            this.importViewModel.removePropertyChangeListener(this);
        }

        this.importViewModel = importViewModel;
        importViewModel.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "exported":
                announceExportSuccess();
                break;
            case "imported":
                announceImportSuccess();
                break;
            case "exportCancelled":
                announceExportCancelled();
                break;
            case "importCancelled":
                announceImportCancelled();
                break;
            case "exportError":
                handleExportError();
                break;
            case "importError":
                handleImportError();
                break;
        }
    }

    private void announceExportSuccess() {
        if (exportViewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                "Successfully exported timetable to file:" + exportViewModel.getExportedPath(),
                "Export",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void announceImportSuccess() {
        if (importViewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                importViewModel.getImportDataString(),
                "Import",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void announceExportCancelled() {
        if (exportViewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                "Export cancelled.",
                "Export",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void announceImportCancelled() {
        if (importViewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                "Import cancelled.",
                "Import",
                JOptionPane.WARNING_MESSAGE
        );
    }

    private void handleExportError() {
        if (exportViewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                exportViewModel.getErrorMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void handleImportError() {
        if (importViewModel == null) return;

        JOptionPane.showMessageDialog(
                this,
                importViewModel.getErrorMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void initializeComponents() {
        exportButton = new JButton("Export as JSON");
        importButton = new JButton("Import from JSON");
    }

    private void layoutComponents() {
        add(exportButton, BorderLayout.WEST);
        add(importButton, BorderLayout.EAST);
    }

    private void setupEventHandlers() {
        exportButton.addActionListener(e -> onExportButtonClicked());
        importButton.addActionListener(e -> onImportButtonClicked());

    }

    private void onExportButtonClicked() {
        try {
            if (exportController == null) {
                throw new NullPointerException("Controller is null");
            }

            String path = runSaveFileDialog();
            exportController.exportTimetable(path);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void onImportButtonClicked() {
        try {
            if (importController == null) {
                throw new NullPointerException("Controller is null");
            }

            String path = runOpenFileDialog();
            importController.importTimetable(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    @Nullable
    private String runSaveFileDialog() {
        FileDialog fileDialog = new FileDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Save",
                FileDialog.SAVE);
        fileDialog.setDirectory(EXPORT_DIRECTORY);
        fileDialog.setFile(EXPORT_DIRECTORY + "\\" + DEFAULT_EXPORT_NAME);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
            return null;
        } else {
            return fileDialog.getDirectory() + fileDialog.getFile();
        }
    }

    @Nullable
    private String runOpenFileDialog() {
        FileDialog fileDialog = new FileDialog((Frame) SwingUtilities.getWindowAncestor(this), "Open", FileDialog.LOAD);
        String currentDirectory = System.getProperty("user.dir");
        fileDialog.setDirectory(currentDirectory);
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
            return null;
        } else {
            return fileDialog.getDirectory() + fileDialog.getFile();
        }
    }

    public void setExportController(ExportTimetableController exportController) {
        this.exportController = exportController;
    }

    public void setImportController(ImportTimetableController importController) {
        this.importController = importController;
    }
}
