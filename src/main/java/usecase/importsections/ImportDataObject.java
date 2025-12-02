package usecase.importsections;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Class for compartmentalizing data stored within an exported timetable.
 *
 * While it currently contains only the section list, this is included as a class for expandability reasons --
 * if other extension ideas were to be added to the timetable app (e.g. choosing your major, etc.) these
 * would be stored in a timetable's exported JSON file as well.
 */

public class ImportDataObject {
    private JSONObject sectionObject = null;

    public void setSectionObject(JSONObject sectionObject) {
        this.sectionObject = sectionObject;
    }

    public JSONObject getSectionObject() {
        return sectionObject;
    }

    public HashMap<String, List<String>> getSectionMap() {
        HashMap<String, List<String>> result = new HashMap<>();
        for (String key : sectionObject.keySet()) {
            if (!(sectionObject.get(key) instanceof JSONArray)) {
                continue;
            }
            JSONArray sectionArray = sectionObject.getJSONArray(key);
            List sectionList = sectionArray.toList();
            result.put(key, sectionList);
        }
        return result;
    }
}
