package app;

import java.awt.*;

/**
 * Helper class for managing course colors on the timetable.
 */
public class CourseColorHelper {
    
    private static final Color[] COURSE_COLORS = {
            new Color(173, 216, 230),  // Light blue
            new Color(175, 255, 175),  // Light green
            new Color(255, 182, 193),  // Light pink
            new Color(255, 218, 185),  // Peach
            new Color(221, 160, 221),  // Plum
            new Color(176, 224, 230),  // Powder blue
            new Color(255, 255, 153),  // Light yellow
            new Color(147, 241, 164),  // Pale green
    };
    
    private int colorIndex = 0;
    
    /**
     * Get the next color in the palette.
     * @return the next color
     */
    public Color getNextColor() {
        Color color = COURSE_COLORS[colorIndex % COURSE_COLORS.length];
        colorIndex++;
        return color;
    }
    
    /**
     * Reset the color index to start from the beginning.
     */
    public void reset() {
        colorIndex = 0;
    }
}
