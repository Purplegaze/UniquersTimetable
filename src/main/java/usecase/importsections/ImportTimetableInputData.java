package usecase.importsections;

public class ImportTimetableInputData {
    private final String filepath;

    public ImportTimetableInputData(String filepath) {
        this.filepath = filepath;
    }

    String getFilepath() {return filepath;}
}
