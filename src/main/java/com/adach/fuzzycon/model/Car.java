package com.adach.fuzzycon.model;

public class Car {

  public static final double CAR_LENGTH_METERS = 4;

  private static final double MIN_VELOCITY_MS = 0;
  private static final double MAX_VELOCITY_MS = 25;
  private static final double MIN_ACCELERATION_MS2 = -9;
  private static final double MAX_ACCELERATION_MS2 = 1.3;

  private double velocityMS;
  private double upperPositionM;
  private double accelerationMS2;
  private Lane lane;

  public Car(double velocityMS, double upperPositionM, double accelerationMS2,
      Lane lane) {
    this.velocityMS = velocityMS;
    this.upperPositionM = upperPositionM;
    this.accelerationMS2 = accelerationMS2;
    this.lane = lane;
  }

  public void changeVelocityByTime(double deltaTimeSeconds) {
    double newVelocity = this.velocityMS + this.accelerationMS2 * deltaTimeSeconds;
    if (newVelocity < MIN_VELOCITY_MS) {
      this.accelerationMS2 = 0;
      this.velocityMS = MIN_VELOCITY_MS;
    } else if (newVelocity > MAX_VELOCITY_MS) {
      this.accelerationMS2 = (MAX_VELOCITY_MS - this.velocityMS) / deltaTimeSeconds;
      this.velocityMS += this.accelerationMS2 * deltaTimeSeconds;
    } else {
      this.velocityMS = newVelocity;
    }
  }

  public void changePositionByTime(double deltaTimeSeconds, boolean upward) {
    if (upward) {
      this.upperPositionM += this.velocityMS * deltaTimeSeconds;
    } else {
      this.upperPositionM -= this.velocityMS * deltaTimeSeconds;
    }
  }

  public void changeVelocityByValue(double deltaVelocity) {
    this.velocityMS += deltaVelocity;
    if (this.velocityMS < MIN_VELOCITY_MS) {
      this.velocityMS = MIN_VELOCITY_MS;
    } else if (this.velocityMS > MAX_VELOCITY_MS) {
      this.velocityMS = MAX_VELOCITY_MS;
    }
  }

  //region SETTERS
  public void setAccelerationMS2(double accelerationMS2) {
    if (accelerationMS2 < MIN_ACCELERATION_MS2) {
      this.accelerationMS2 = MIN_ACCELERATION_MS2;
    } else if (accelerationMS2 > MAX_ACCELERATION_MS2) {
      this.accelerationMS2 = MAX_ACCELERATION_MS2;
    } else {
      this.accelerationMS2 = accelerationMS2;
    }
  }

  public void setLane(Lane lane) {
    this.lane = lane;
  }
  //endregion

  //region GETTERS
  public double getVelocityMS() {
    return velocityMS;
  }

  public double getUpperPositionM() {
    return upperPositionM;
  }

  public double getLowerPositionM() {
    return upperPositionM - CAR_LENGTH_METERS;
  }

  public double getAccelerationMS2() {
    return accelerationMS2;
  }

  public Lane getLane() {
    return lane;
  }
  //endregion
}