package usecase.calculatewalkingtime;

import entity.Building;
import entity.Timetable;

/**
 * The Data Access Interface for the Calculate Walking Time Use Case.
 */
public interface CalculateWalkingDataAccessInterface {

    /**
     * Calculate the walking time between two buildings.
     */

    double calculateWalking(Building building1, Building building2);

    /**
     * Retrieve the current timetable.
     */
    Timetable getTimetable();
}
