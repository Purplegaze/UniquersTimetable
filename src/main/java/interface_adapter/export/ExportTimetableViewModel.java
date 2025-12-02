package interface_adapter.export;

import interface_adapter.ViewModel;

public class ExportTimetableViewModel extends ViewModel {

    private String exportedPath;
    private String errorMessage;

    public ExportTimetableViewModel() {
        super("exportTimetable");
    }

    public String getExportedPath() {
        return exportedPath;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setExported(String path) {
        this.exportedPath = path;
        firePropertyChanged("exported");
    }

    public void setCancelled() {
        firePropertyChanged("exportCancelled");
    }

    public void setError(String message) {
        this.errorMessage = message;
        firePropertyChanged("exportError");
    }
}
