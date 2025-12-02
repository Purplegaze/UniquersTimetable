package view;

import interface_adapter.deletesection.DeleteSectionController;
import interface_adapter.viewmodel.TimetableSlotViewModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class TimetableView extends JPanel {

    private static class TimetableSlotItem {
        private final String courseCode;
        private final String sectionCode;
        private final String location;
        private final Color color;
        private final boolean hasConflict;

        public TimetableSlotItem(String courseCode, String sectionCode,
                                 String location, Color color, boolean hasConflict) {
            this.courseCode = courseCode;
            this.sectionCode = sectionCode;
            this.location = location;
            this.color = color;
            this.hasConflict = hasConflict;
        }

        public String getCourseCode() { return courseCode; }
        public String getSectionCode() { return sectionCode; }
        public String getLocation() { return location; }
        public Color getColor() { return color; }
        public boolean hasConflict() { return hasConflict; }
    }
    private TimetableClickListener listener;

    public void setClickListener(TimetableClickListener listener) {
        this.listener = listener;
    }

    private static final int START_TIME = 9;
    private static final int END_TIME = 21;
    private static final String[] DAYS = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    private Map<String, JPanel> slotPanels;
    private Map<String, String> slotCourseKeys;
    private DeleteSectionController deleteSectionController;

    public TimetableView() {
        slotPanels = new HashMap<>();
        slotCourseKeys = new HashMap<>();
        setLayout(new BorderLayout());
        initializeComponents();
    }

    public void setDeleteController(DeleteSectionController controller) {
        this.deleteSectionController = controller;
    }

    /**
     * Connect AddCourseViewModel to this view.
     */
    public void setAddCourseViewModel(interface_adapter.addcourse.AddCourseViewModel viewModel) {
        viewModel.addPropertyChangeListener(this::handleAddCourseViewModelChange);
    }

    /**
     * Handle property changes from AddCourseViewModel.
     */
    private void handleAddCourseViewModelChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "slotsAdded":
                interface_adapter.addcourse.AddCourseViewModel vm =
                        (interface_adapter.addcourse.AddCourseViewModel) evt.getSource();
                List<TimetableSlotViewModel> slots = vm.getSlots();
                // Display all slots
                for (TimetableSlotViewModel slot : slots) {
                    displaySlotViewModel(slot);
                }
                break;
            case "conflict":
                interface_adapter.addcourse.AddCourseViewModel conflictVM =
                        (interface_adapter.addcourse.AddCourseViewModel) evt.getSource();
                showConflictWarning(conflictVM.getConflictMessage());
                break;
            case "error":
                interface_adapter.addcourse.AddCourseViewModel errorVM =
                        (interface_adapter.addcourse.AddCourseViewModel) evt.getSource();
                showErrorMessage(errorVM.getErrorMessage());
                break;
            case "cleared":
                clearAll();
                break;
        }
    }

    /**
     * Connect DeleteSectionViewModel to this view.
     */
    public void setDeleteSectionViewModel(interface_adapter.deletesection.DeleteSectionViewModel viewModel) {
        viewModel.addPropertyChangeListener(this::handleDeleteSectionViewModelChange);
    }

    /**
     * Handle property changes from DeleteSectionViewModel.
     */
    private void handleDeleteSectionViewModelChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "sectionDeleted":
                interface_adapter.deletesection.DeleteSectionViewModel vm =
                        (interface_adapter.deletesection.DeleteSectionViewModel) evt.getSource();
                removeCourse(vm.getDeletedCourseCode(), vm.getDeletedSectionCode());
                break;
            case "error":
                interface_adapter.deletesection.DeleteSectionViewModel errorVM =
                        (interface_adapter.deletesection.DeleteSectionViewModel) evt.getSource();
                showErrorMessage(errorVM.getErrorMessage());
                break;
        }
    }

    /**
     * Convert TimetableSlotViewModel to internal representation and display.
     */
    private void displaySlotViewModel(TimetableSlotViewModel viewModel) {
        String day = viewModel.getDayName();
        int startHour = viewModel.getStartHour();
        int endHour = viewModel.getEndHour();

        TimetableSlotItem item = new TimetableSlotItem(
                viewModel.getCourseCode(),
                viewModel.getSectionCode(),
                viewModel.getLocation(),
                viewModel.getColor(),
                viewModel.hasConflict()
        );

        displayCourse(day, startHour, endHour, item);
    }

    private void displayCourse(String day, int startHour, int endHour, TimetableSlotItem item) {
        List<String> slotKeys = generateSlotKeys(day, startHour, endHour);
        String courseKey = item.getCourseCode() + "-" + item.getSectionCode();

        for (int i = 0; i < slotKeys.size(); i++) {
            String key = slotKeys.get(i);
            JPanel slot = slotPanels.get(key);
            if (slot != null) {
                slotCourseKeys.put(key, courseKey);
                boolean isFirstSlot = (i == 0);
                displayCourseInSlot(slot, item, isFirstSlot);
            }
        }
    }

    private void removeCourse(String courseCode, String sectionCode) {
        String courseKey = courseCode + "-" + sectionCode;

        // Find and clear all slots with this course
        List<String> slotsToRemove = new ArrayList<>();
        for (Map.Entry<String, String> entry : slotCourseKeys.entrySet()) {
            if (courseKey.equals(entry.getValue())) {
                String slotKey = entry.getKey();
                slotsToRemove.add(slotKey);

                JPanel slot = slotPanels.get(slotKey);
                if (slot != null) {
                    slot.removeAll();
                    slot.setBackground(Color.WHITE);
                    slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    slot.revalidate();
                    slot.repaint();
                }
            }
        }

        // Remove from tracking
        for (String slotKey : slotsToRemove) {
            slotCourseKeys.remove(slotKey);
        }
    }

    private void clearAll() {
        slotCourseKeys.clear();

        for (JPanel slot : slotPanels.values()) {
            slot.removeAll();
            slot.setBackground(Color.WHITE);
            slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            slot.revalidate();
            slot.repaint();
        }
    }

    private void showConflictWarning(String conflictMessage) {
        JOptionPane.showMessageDialog(this, conflictMessage,
                "Schedule Conflict", JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    private List<String> generateSlotKeys(String day, int startHour, int endHour) {
        List<String> keys = new ArrayList<>();
        for (int hour = startHour; hour < endHour; hour++) {
            keys.add(day + "-" + hour);
        }
        return keys;
    }

    /**
     * Display a course in a slot.
     * Only shows course info and X button in the first slot.
     */
    private void displayCourseInSlot(JPanel slot, TimetableSlotItem item, boolean isFirstSlot) {
        slot.removeAll();
        slot.setBackground(item.getColor());
        slot.setLayout(new BorderLayout());

        if (isFirstSlot) {
            // FIRST SLOT: Show course info + X button
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setOpaque(false);

            // Course label in center
            JLabel label = createCourseLabel(item);
            contentPanel.add(label, BorderLayout.CENTER);

            // X button in top-right
            JButton deleteButton = createDeleteButton(item.getCourseCode(), item.getSectionCode());
            contentPanel.add(deleteButton, BorderLayout.NORTH);

            slot.add(contentPanel, BorderLayout.CENTER);
        }
        // Other slots are just colored (no content)

        slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        slot.revalidate();
        slot.repaint();
    }

    /**
     * Create X delete button.
     */
    private JButton createDeleteButton(String courseCode, String sectionCode) {
        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 16));
        deleteButton.setForeground(Color.RED);
        deleteButton.setOpaque(false);
        deleteButton.setContentAreaFilled(false);
        deleteButton.setBorderPainted(false);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setPreferredSize(new Dimension(25, 20));

        // Hover effect
        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                deleteButton.setForeground(new Color(200, 0, 0));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                deleteButton.setForeground(Color.RED);
            }
        });

        // Delete action
        deleteButton.addActionListener(e -> {
            if (deleteSectionController != null) {
                int confirm = JOptionPane.showConfirmDialog(
                        this,
                        "Delete " + courseCode + " " + sectionCode + " from timetable?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (confirm == JOptionPane.YES_OPTION) {
                    deleteSectionController.deleteSection(courseCode, sectionCode);
                }
            }
        });

        return deleteButton;
    }

    private JLabel createCourseLabel(TimetableSlotItem item) {
        String labelText = "<html><center>" + item.getCourseCode() + "<br>"
                + item.getSectionCode() + "<br>" + item.getLocation() + "</center></html>";
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.PLAIN, 10));
        return label;
    }

    private void initializeComponents() {
        int numRows = END_TIME - START_TIME;

        // Header container
        JPanel headerContainer = new JPanel(new BorderLayout());

        // Time header
        JLabel timeHeader = new JLabel("Time", SwingConstants.CENTER);
        timeHeader.setFont(new Font("Arial", Font.BOLD, 12));
        timeHeader.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        timeHeader.setPreferredSize(new Dimension(60, 25));
        headerContainer.add(timeHeader, BorderLayout.WEST);

        // Day headers
        JPanel dayHeaderPanel = new JPanel(new GridLayout(1, DAYS.length));
        for (String day : DAYS) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            dayHeaderPanel.add(label);
        }
        headerContainer.add(dayHeaderPanel, BorderLayout.CENTER);

        // Time column
        JPanel timePanel = new JPanel(new GridLayout(numRows, 1));
        timePanel.setPreferredSize(new Dimension(60, 0));
        for (int hour = START_TIME; hour < END_TIME; hour++) {
            JLabel timeLabel = new JLabel(hour + ":00", SwingConstants.CENTER);
            timeLabel.setFont(new Font("Arial", Font.PLAIN, 11));
            timeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            timePanel.add(timeLabel);
        }

        // Timetable grid
        JPanel gridPanel = new JPanel(new GridLayout(numRows, DAYS.length));
        for (int hour = START_TIME; hour < END_TIME; hour++) {
            for (String day : DAYS) {
                JPanel slot = new JPanel();
                slot.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                slot.setBackground(Color.WHITE);

                String key = day + "-" + hour;
                slotPanels.put(key, slot);

                // Add click handler for empty-slot detection
                int finalHour = hour;      // Needed because lambda requires effectively-final variables
                String finalDay = day;     // Capture the day as well

                slot.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        // Empty slot = no course mapped to this key
                        boolean slotIsEmpty = !slotCourseKeys.containsKey(key);

                        if (listener != null && slotIsEmpty) {
                            String startTime = String.format("%02d:00", finalHour);
                            String endTime   = String.format("%02d:00", finalHour + 1);
                            listener.onEmptySlotClicked(finalDay, startTime, endTime);
                        }
                    }
                });

                gridPanel.add(slot);
            }
        }

        add(headerContainer, BorderLayout.NORTH);
        add(timePanel, BorderLayout.WEST);
        add(gridPanel, BorderLayout.CENTER);}}
