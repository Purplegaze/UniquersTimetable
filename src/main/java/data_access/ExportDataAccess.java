package data_access;

import entity.Section;
import org.json.JSONObject;
import usecase.export.ExportTimetableDataAccessInterface;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ExportDataAccess implements ExportTimetableDataAccessInterface {

    @Override
    public void save(String filepath, List<Section> sections) {
        try (FileWriter fileWriter = new FileWriter(filepath)) {
            JSONObject courseObject = JSONConverter.logSections(sections);
            fileWriter.write(courseObject.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
