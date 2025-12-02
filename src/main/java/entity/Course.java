package entity;

import java.util.List;

public class Course {
    private final String courseCode;
    private final String courseName;
    private final String description;
    private final float credits;
    private Rating courseRating;
    private final String term;
    private final List<Section> sections;
    private final Building location;
    private final int breadthCategory;

    // Legacy Course constructor, included to avoid breaking the program.
    // Should be phased out in favour of the Builder construction!
    public Course(String courseCode, String courseName, String description,
                  float credits, String term,
                  List<Section> sections, Building location, int breadthCategory) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.courseRating = null;
        this.term = term;
        this.sections = sections;
        this.location = location;
        this.breadthCategory = breadthCategory;
    }

    private Course(Builder builder) {
        this.courseCode = builder.courseCode;
        this.courseName = builder.courseName;
        this.description = builder.description;
        this.credits = builder.credits;
        this.courseRating = builder.courseRating;
        this.term = builder.term;
        this.sections = builder.sections;
        this.location = builder.location;
        this.breadthCategory = builder.breadthCategory;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public List<Section> getSections() {
        return sections;
    }

    /**
     * Add a section to this course.
     * @param section the section to be added.
     */
    public void addSection(Section section) {
        sections.add(section);
    }

    /**
     * Remove a section from this course.
     * @param section the section to be removed.
     */
    public void removeSection(Section section) {
        sections.remove(section);
    }

    public Rating getCourseRating() {
        return courseRating;
    }

    public void setCourseRating(Rating courseRating) {
        this.courseRating = courseRating;
    }

    /**
     * Get the average rating of this course.
     * @return a float of the average rating on a 1-5 scale.
     */
    public float getAvgRating() {
        final float result;
        if (courseRating == null) {
            result = 0f;
        }
        else {
            result = courseRating.getAvgRating();
        }
        return result;
    }

    public String getTerm() {
        return term;
    }

    public boolean isYearLong() {
        return "Y".equals(term);
    }

    public boolean isFall() {
        return "F".equals(term);
    }

    public boolean isWinter() {
        return "S".equals(term);
    }

    public String getDescription() {
        return description;
    }

    public float getCredits() {
        return credits;
    }

    public int getBreadthCategory() {
        return breadthCategory;
    }

    public Building getLocation() {
        return location;
    }

    /**
     * Search for and return a section by its section ID string.
     * @param sectionCode the section ID (e.g. LEC0101)
     * @return the Section with that ID
     * @throws SectionNotFoundException if no Section object is found.
     */
    public Section getSectionByCode(String sectionCode) throws SectionNotFoundException {
        for (Section section : sections) {
            if (section.getSectionId().equals(sectionCode)) {
                return section;
            }
        }
        throw new SectionNotFoundException("Section not found: " + sectionCode);
    }

    public static class SectionNotFoundException extends Exception {
        public SectionNotFoundException(String message) {
            super(message);
        }
    }

    public static class Builder {
        private String courseCode;
        private String courseName;
        private String description;
        private float credits;
        private Rating courseRating;
        private String term;
        private List<Section> sections;
        private Building location;
        private int breadthCategory;

        /**
         * Add a course code to this Builder.
         * @param courseCodeInput the course code, e.g. CSC110
         * @return the builder
         */
        public Builder courseCode(String courseCodeInput) {
            this.courseCode = courseCodeInput;
            return this;
        }

        /**
         * Add a course name to this Builder.
         * @param courseNameInput the course name, e.g. Foundations of Computer Science I
         * @return the builder
         */
        public Builder courseName(String courseNameInput) {
            this.courseName = courseNameInput;
            return this;
        }

        /**
         * Add a class description to this Builder.
         * @param descriptionInput the course description
         * @return the builder
         */
        public Builder description(String descriptionInput) {
            this.description = descriptionInput;
            return this;
        }

        /**
         * Add the number of credits to this Builder.
         * @param creditsInput the credit count as a float, e.g. 1.0f
         * @return the builder
         */
        public Builder credits(float creditsInput) {
            this.credits = creditsInput;
            return this;
        }

        /**
         * Attach a course rating to this Builder.
         * @param courseRatingInput a Rating object related to the course.
         * @return the builder
         */
        public Builder courseRating(Rating courseRatingInput) {
            this.courseRating = courseRatingInput;
            return this;
        }

        /**
         * Add the course term to this Builder.
         * @param termInput the term string
         * @return the builder
         */
        public Builder term(String termInput) {
            this.term = termInput;
            return this;
        }

        /**
         * Add a list of sections to this Builder.
         * @param sectionsInput a List of Section objects corresponding to this course.
         * @return the builder
         */
        public Builder sections(List<Section> sectionsInput) {
            this.sections = sectionsInput;
            return this;
        }

        /**
         * Add the location of the course to this Builder.
         * @param locationInput the Building object the course is located in
         * @return the builder
         */
        public Builder location(Building locationInput) {
            this.location = locationInput;
            return this;
        }

        /**
         * Add the breadth category that this course belongs to, to the Builder.
         * @param breadthCategoryInput the integer breadth category (1-5)
         * @return the builder
         */
        public Builder breadthCategory(int breadthCategoryInput) {
            this.breadthCategory = breadthCategoryInput;
            return this;
        }

        /**
         * Build a Course in accordance with the Builder.
         * @return the Course object.
         */
        public Course build() {
            return new Course(this);
        }
    }
}
