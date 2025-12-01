package usecase.export;

public class ExportTimetableOutputData {
    private final String filepath;

    public ExportTimetableOutputData(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {return filepath;}
}
