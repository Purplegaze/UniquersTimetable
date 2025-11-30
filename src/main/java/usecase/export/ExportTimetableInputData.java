package usecase.export;

public class ExportTimetableInputData {
    private final String filepath;

    public ExportTimetableInputData(String filepath) {
        this.filepath = filepath;
    }

    String getFilepath() {return filepath;}
}
