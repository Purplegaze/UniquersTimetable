package usecase.export;

import org.json.JSONObject;

public interface ExportTimetableDataAccessInterface {

    void save(String filepath, JSONObject courseObject);
}
