package com.adach.fuzzycon.fis.data;

import com.adach.fuzzycon.model.Lane;

public class CrispOutputData {

  public double acceleration;
  public Lane lane;

  public CrispOutputData(double acceleration, Lane lane) {
    this.acceleration = acceleration;
    this.lane = lane;
  }
}