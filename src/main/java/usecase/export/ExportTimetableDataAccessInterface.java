package usecase.export;

import entity.Section;
import org.json.JSONObject;

import java.util.List;

public interface ExportTimetableDataAccessInterface {

    void save(String filepath, List<Section> sections);
}
