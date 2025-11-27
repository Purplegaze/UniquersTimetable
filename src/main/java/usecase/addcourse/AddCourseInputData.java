package usecase.addcourse;

import entity.Section;

/**
 * Input data for the Add Course use case.
 */
public class AddCourseInputData {
    private final Section section;

    public AddCourseInputData(Section section) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }
        this.section = section;
    }

    public Section getSection() {
        return section;
    }
}