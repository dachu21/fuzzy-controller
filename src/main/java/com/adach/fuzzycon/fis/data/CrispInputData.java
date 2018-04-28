package com.adach.fuzzycon.fis.data;

import com.adach.fuzzycon.model.Lane;

public class CrispInputData {

  public double velocityA;
  public double velocityB;
  public double velocityC;
  public double distanceAB;
  public double distanceAC;
  public double distanceAtoEnd;
  public Lane laneA;

  public CrispInputData(double velocityA, double velocityB, double velocityC, double distanceAB,
      double distanceAC, double distanceAtoEnd, Lane laneA) {
    this.velocityA = velocityA;
    this.velocityB = velocityB;
    this.velocityC = velocityC;
    this.distanceAB = distanceAB;
    this.distanceAC = distanceAC;
    this.distanceAtoEnd = distanceAtoEnd;
    this.laneA = laneA;
  }
}