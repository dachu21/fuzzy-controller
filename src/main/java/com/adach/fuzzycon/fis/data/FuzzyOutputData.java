package com.adach.fuzzycon.fis.data;

import com.adach.fuzzycon.fis.partitioning.Point;
import com.adach.fuzzycon.fis.partitioning.Universe;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FuzzyOutputData {

  public Universe acceleration = new Universe();

  public void cutTo(String linguisticValueName, double level) {
    boolean levelCrossed = false;
    if (level == 1.0) {
      return;
    }
    Map<Character, Point> originalPoints = new HashMap<>();
    for (Map.Entry<Character, Point> pointEntry : acceleration.linguisticValues
        .get(linguisticValueName).points.entrySet()) {
      originalPoints.put(pointEntry.getKey(), new Point(pointEntry.getValue()));
    }
    Map<Character, Point> points = acceleration.linguisticValues.get(linguisticValueName).points;

    Set<Character> keysSet = points.keySet();
    Character[] keys = keysSet.toArray(new Character[keysSet.size()]);
    if (level == 0.0) {
      Map<Character, Point> flatPoints = new HashMap<>();
      flatPoints.put('a', new Point(points.get(keys[0]).x, 0));
      flatPoints.put('b', new Point(points.get(keys[keys.length - 1]).x, 0));
      acceleration.linguisticValues.get(linguisticValueName).points = flatPoints;
      return;
    }
    boolean aboveLevel = points.get(keys[0]).y > level;
    for (int i = 1; i < keys.length; i++) {
      if (aboveLevel) {
        if (points.get(keys[i]).y < level) {
          levelCrossed = true;
          aboveLevel = false;
        } else {
          levelCrossed = false;
        }
      } else {
        if (points.get(keys[i]).y > level) {
          levelCrossed = true;
          aboveLevel = true;
        } else {
          levelCrossed = false;
        }
      }
      if (levelCrossed) {
        Point toChange;
        if (!aboveLevel && keys.length == 3) {
          toChange = new Point(points.get(keys[i]).x, points.get(keys[i]).y);
          for (int j = keys.length - 1; j >= i; j--) {
            points.put((char) (keys[j] + 1), points.get(keys[j]));
          }
          points.put(keys[i], toChange);
        } else {
          toChange = points.get(keys[i]);
        }
        if (aboveLevel) {
          toChange = calcCrossPoint(originalPoints.get(keys[i - 1]), toChange,
              level);
          points.put(keys[i], toChange);
        } else {
          toChange = calcCrossPoint(originalPoints.get(keys[i - 1]), toChange,
              level);
          if (keys.length == 3) {
            points.put(keys[i], toChange);
          } else {
            points.put(keys[i - 1], toChange);
          }
        }
      }
    }
    acceleration.linguisticValues.get(linguisticValueName).points = points;
  }

  private Point calcCrossPoint(Point point1, Point point2, double level) {
    double a = (point1.y - point2.y) / (point1.x - point2.x);
    double b = point1.y - (a * point1.x);
    return new Point((level - b) / a, level);
  }
}