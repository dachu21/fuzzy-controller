package com.adach.fuzzycon;

import com.adach.fuzzycon.fis.data.CrispOutputData;
import com.adach.fuzzycon.model.Car;

public class Controller {

  public void modifyCarState(Car car, CrispOutputData crispOutput) {
    car.setLane(crispOutput.lane);
    car.setAccelerationMS2(crispOutput.acceleration);
  }
}