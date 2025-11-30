package data_access;

import entity.Rating;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CourseEvalDataReader {
    private final String csvPath;
    private final Map<String, Rating> courseRatings = new HashMap<>();

    public CourseEvalDataReader(String csvPath) {
        this.csvPath = csvPath;
        loadRatings();
    }

    private void loadRatings() {
        Map<String, Double> totalRecommendation = new HashMap<>();
        Map<String, Double> totalWorkload = new HashMap<>();
        Map<String, Integer> totalResponses = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            String line = br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                // Split csv line
                String[] data = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

                if (data.length < 18) continue;

                String rawCourse = data[2];
                String courseCode = extractCode(rawCourse);

                double workload = parseDouble(data[14]);
                double recommend = parseDouble(data[15]);
                int responses = parseInt(data[17]);

                if (courseCode != null && responses > 0) {
                    totalRecommendation.merge(courseCode, recommend * responses, Double::sum);
                    totalWorkload.merge(courseCode, workload * responses, Double::sum);
                    totalResponses.merge(courseCode, responses, Integer::sum);
                }
            }

            for (String code : totalResponses.keySet()) {
                int count = totalResponses.get(code);
                if (count > 0) {
                    float avgRec = (float) (totalRecommendation.get(code) / count);
                    float avgWork = (float) (totalWorkload.get(code) / count);

                    Map<String, Float> scores = new HashMap<>();
                    scores.put("Recommendation", avgRec);
                    scores.put("Workload", avgWork);

                    courseRatings.put(code, new Rating("Average", code, scores));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Rating getRating(String courseCode) {
        return courseRatings.get(courseCode);
    }

    private String extractCode(String raw) {
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("[A-Z]{3}\\d{3}[HY]\\d").matcher(raw);
        return m.find() ? m.group(0) : null;
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return 0.0; }
    }

    private int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }
}