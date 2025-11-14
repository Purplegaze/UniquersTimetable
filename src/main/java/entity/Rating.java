import java.util.Map;
import java.util.List;

public class Rating {
    private String instructorName;
    private String courseCode;
    private Map<String, Float> ratingData;

    public Rating(String instructorName, String courseCode, Map<String, Float> ratingData) {
        this.instructorName = instructorName;
        this.courseCode = courseCode;
        this.ratingData = ratingData;
        }
        public String getInstructorName() {
            return instructorName;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public Float getRating(String key) {
            return ratingData.get(key);
        }

        public void setRating(String key, float value) {
            ratingData.put(key, value);
        }

        public float getavgRating() {
            if (ratingData.isEmpty()) return 0f;
            float sum = 0f;
            for (Float v : ratingData.values()) {
                sum += v;
            }
            return sum / ratingData.size();
        }
    }