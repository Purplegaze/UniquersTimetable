package interface_adapter.importsections;

import interface_adapter.ViewModel;

public class ImportTimetableViewModel extends ViewModel {

    private String importDataString;
    private String errorMessage;

    public ImportTimetableViewModel() {
        super("importTimetable");
    }

    public String getImportDataString() {
        return importDataString;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setImportDataString(String importDataString) {
        this.importDataString = importDataString;
        firePropertyChanged("imported");
    }

    public void setCancelled() {
        firePropertyChanged("importCancelled");
    }

    public void setError(String message) {
        this.errorMessage = message;
        firePropertyChanged("importError");
    }
}
