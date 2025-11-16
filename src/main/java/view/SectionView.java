package view;
import javax.swing.*;
import java.awt.*;
import entity.*;

public class SectionView extends JDialog {

        public SectionView(Course course) {
            setTitle("Section Details - " + course.getCourseCode());
            setSize(500, 400);
            setModal(true);
            setLayout(new BorderLayout());

            JPanel panel = new JPanel(new GridLayout(0, 1));

            for (Section s : course.getSections()) {
                panel.add(new JLabel("Section: " + s.getSectionId()));
                panel.add(new JLabel("Time: " + s.getTimes()));
                panel.add(new JLabel("Instructor: " + s.getInstructors()));
                panel.add(new JSeparator());
            }

            add(new JScrollPane(panel), BorderLayout.CENTER);
            setLocationRelativeTo(null);

            setVisible(true);
        }
}


