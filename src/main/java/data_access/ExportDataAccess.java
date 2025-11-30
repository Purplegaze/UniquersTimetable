package data_access;

import org.json.JSONObject;
import usecase.export.ExportTimetableDataAccessInterface;

import java.io.FileWriter;
import java.io.IOException;

public class ExportDataAccess implements ExportTimetableDataAccessInterface {

    public void save(String filepath, JSONObject courseObject) {
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            fileWriter.write(courseObject.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
