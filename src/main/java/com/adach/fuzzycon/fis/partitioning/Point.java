package com.adach.fuzzycon.fis.partitioning;

public class Point implements Comparable<Point> {

  public double x;
  public double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public Point() {

  }

  public Point(Point other) {
    this.x = other.x;
    this.y = other.y;
  }

  @Override
  public int compareTo(Point o) {
    Double xBoxed = this.x;
    return xBoxed.compareTo(o.x);
  }
}
