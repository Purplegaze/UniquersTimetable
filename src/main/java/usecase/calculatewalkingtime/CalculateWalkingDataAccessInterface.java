package usecase.calculatewalkingtime;

import entity.Building;

/**
 * The DA interface for the Calculate Walking Use Case
 */
public interface CalculateWalkingDataAccessInterface {

    /**
     * Calculate the walking time between two buildings
     */
    double calculateWalking(Building building1, Building building2);
}
