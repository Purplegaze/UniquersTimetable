package data_access;

import org.json.JSONObject;
import usecase.importsections.ImportDataObject;
import usecase.importsections.ImportTimetableDataAccessInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImportDataAccess implements ImportTimetableDataAccessInterface {

    @Override
    public ImportDataObject open(String filepath) {
        try {
            String jsonString = Files.readString(Paths.get(filepath));
            System.out.println(jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            ImportDataObject importDataObject = new ImportDataObject();
            importDataObject.setSectionObject(jsonObject);
            return importDataObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
