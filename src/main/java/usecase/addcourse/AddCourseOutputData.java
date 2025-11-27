package usecase.addcourse;

import entity.Section;

/**
 * Output data from the Add Course use case.
 */
public class AddCourseOutputData {
    private final Section section;

    public AddCourseOutputData(Section section, boolean hasConflict) {
        if (section == null) {
            throw new IllegalArgumentException("Section cannot be null");
        }
        this.section = section;
    }

    public Section getSection() {
        return section;
    }
}