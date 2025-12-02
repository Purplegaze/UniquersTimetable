package usecase.importsections;

import usecase.addcourse.AddCourseOutputData;

import java.util.ArrayList;
import java.util.List;

public class ImportTimetableOutputData {
    private final String filepath;

    private final List<String> sectionsNotFound = new ArrayList<>();

    private final List<AddCourseOutputData> addCourseOutputDataList = new ArrayList<>();

    int coursesAdded = 0;
    int sectionsAdded = 0;

    public ImportTimetableOutputData(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {return filepath;}

    public void addToSectionsNotFound(String sectionCode) {
        sectionsNotFound.add(sectionCode);
    }

    public void addToAddCourseOutputData(AddCourseOutputData addCourseOutputData) {
        addCourseOutputDataList.add(addCourseOutputData);
    }

    public List<AddCourseOutputData> getCoursesToAdd() {
        return addCourseOutputDataList;
    }

    public void incrementCoursesAdded(){
        coursesAdded++;
    }
    public void incrementSectionsAdded(){
        sectionsAdded++;
    }

    public int getCoursesAdded()  {return coursesAdded;}
    public int getSectionsAdded()  {return sectionsAdded;}
    public String getNotFoundString()  {
        return String.join(", ", sectionsNotFound);
    }

    public String importDataString() {
        StringBuilder sb = new StringBuilder()
                .append("Successfully imported ")
                .append(sectionsAdded).append(" sections from ")
                .append(coursesAdded).append(" courses!");
        if (!getNotFoundString().isEmpty()) {
            sb.append("\n\nThese course sections could not be found:\n")
                    .append(getNotFoundString());
        }
        return sb.toString();
    }

}
