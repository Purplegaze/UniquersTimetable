package view;
import javax.swing.*;
import java.awt.*;
import entity.*;

public class SectionView extends JDialog {

        public SectionView(Course course) {
            setTitle("Section Details - " + course.getCourseCode());
            setSize(500, 400);
            setModal(true);
            setLayout(new BorderLayout(10, 10));

            // Main content
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createTitledBorder("Section Details"));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Title of window
            JLabel header = new JLabel("Section Information");
            header.setFont(new Font(header.getFont().getFontName(), Font.BOLD, 12));
            header.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(header);
            panel.add(Box.createVerticalStrut(10));

            // placeholder sections for now

            for (int i = 1; i <=5; i++) {
                JPanel placeholder = new JPanel();
                placeholder.setLayout(new BoxLayout(placeholder, BoxLayout.Y_AXIS));
                placeholder.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
                placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);

                placeholder.add(new JLabel("Section: Placeholder" + i));
                placeholder.add(Box.createVerticalStrut(5));
                placeholder.add(new JLabel("Time: TBD"));
                placeholder.add(Box.createVerticalStrut(5));
                placeholder.add(new JLabel("Instructor: TBD"));

                panel.add(placeholder);
                panel.add(Box.createVerticalStrut(15));

                // Add course button
                JButton addButton = new JButton("Add Course");
                addButton.setAlignmentX(Component.LEFT_ALIGNMENT);

                addButton.addActionListener(e -> {
                    JOptionPane.showMessageDialog(this,
                            "This is a placeholder",
                            "Info", JOptionPane.INFORMATION_MESSAGE);
                });

                placeholder.add(addButton);

                panel.add(placeholder);
                panel.add(Box.createVerticalStrut(5));
            }

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


