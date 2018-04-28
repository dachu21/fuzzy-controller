package com.adach.fuzzycon.fis;

import com.adach.fuzzycon.fis.data.CrispInputData;
import com.adach.fuzzycon.model.Car;
import com.adach.fuzzycon.model.Lane;

public class LaneSelector {

  public Lane selectLane(CrispInputData crispInput) {
    final int MIN_RESERVE_DISTANCE_TO_RETURN = -2;
    final int MIN_RESERVE_DISTANCE_TO_OVERTAKE = 2;
    final int MAX_VELOCITY = 25;
    final int MAX_DISTANCE_IN_SECOND = 25;

    if (crispInput.laneA == Lane.LEFT) {
      if (crispInput.distanceAB <= MIN_RESERVE_DISTANCE_TO_RETURN - 2 * Car.CAR_LENGTH_METERS) {
        return Lane.RIGHT;
      } else if (crispInput.distanceAB
          > MIN_RESERVE_DISTANCE_TO_OVERTAKE + MAX_DISTANCE_IN_SECOND) {
        return Lane.RIGHT;
      }
    } else {
      if (crispInput.distanceAC < (-1) * (2 * Car.CAR_LENGTH_METERS
          + MIN_RESERVE_DISTANCE_TO_OVERTAKE) &&
          crispInput.velocityA > crispInput.velocityB &&
          crispInput.distanceAB <= MIN_RESERVE_DISTANCE_TO_OVERTAKE + MAX_DISTANCE_IN_SECOND) {
        return Lane.LEFT;
      }

      double relativeVelocityAB = crispInput.velocityA - crispInput.velocityB;
      double overtakingTime =
          (crispInput.distanceAB + Car.CAR_LENGTH_METERS + MIN_RESERVE_DISTANCE_TO_RETURN)
              / relativeVelocityAB;
      double ApositionAfterOvertaking = MAX_VELOCITY * overtakingTime;
      double CpositionAfterOvertaking =
          crispInput.distanceAC - crispInput.velocityC * overtakingTime;
      if (ApositionAfterOvertaking < CpositionAfterOvertaking
          - 2 && crispInput.velocityA > crispInput.velocityB &&
          crispInput.distanceAB <= MIN_RESERVE_DISTANCE_TO_OVERTAKE + MAX_DISTANCE_IN_SECOND) {
        return Lane.LEFT;
      }
    }
    return crispInput.laneA;
  }
}
