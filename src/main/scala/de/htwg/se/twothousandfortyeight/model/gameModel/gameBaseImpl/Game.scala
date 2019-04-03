package de.htwg.se.twothousandfortyeight.model.gameModel.gameBaseImpl

import java.util.LinkedList

import de.htwg.se.twothousandfortyeight.TwoThousandFortyEight
import de.htwg.se.twothousandfortyeight.model.gameModel.GameTrait

class Game extends GameTrait {
  var score: Score = new Score
  var grid: Grid = new Grid

  def reset: Unit = {
    score = new Score
    grid = new Grid
  }

  def left(random1: Double, random2: Double): Unit = {
    var needsATile = false

    for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
      val singleLine = this.grid.getSingleLine(i)
      val mergedLine = mergeSingleLine(moveSingleLine(singleLine))
      this.grid.setSingleLine(i, mergedLine)

      if (!needsATile && !compareLines(singleLine, mergedLine)) {
        needsATile = true
      }
    }

    if (needsATile) {
      this.grid.addTile(random1, random2)
    }
  }

  def right(random1: Double, random2: Double): Unit = {
    this.grid.rotate(180)
    left(random1, random2)
    this.grid.rotate(180)
  }

  def up(random1: Double, random2: Double): Unit = {
    this.grid.rotate(270)
    left(random1, random2)
    this.grid.rotate(90)
  }

  def down(random1: Double, random2: Double): Unit = {
    this.grid.rotate(90)
    left(random1, random2)
    this.grid.rotate(270)
  }

  def moveSingleLine(oldLine: Array[Tile]): Array[Tile] = {
    val helperList = new LinkedList[Tile]
    for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
      if (!oldLine(i).isEmpty) {
        helperList.addLast(oldLine(i))
      }
    }

    if (helperList.size() == 0) {
      return oldLine
    } else {
      val newLine = new Array[Tile](TwoThousandFortyEight.FIELD_SIZE)

      while (helperList.size != TwoThousandFortyEight.FIELD_SIZE) {
        helperList.add(new Tile())
      }

      for (i <- 0 until TwoThousandFortyEight.FIELD_SIZE) {
        newLine(i) = helperList.removeFirst()
      }

      return newLine
    }
  }

  def mergeSingleLine(oldLine: Array[Tile]): Array[Tile] = {
    val helperList = new LinkedList[Tile]

    var i = 0
    while (i < TwoThousandFortyEight.FIELD_SIZE && !oldLine(i).isEmpty) {
      var oldValue = oldLine(i).value
      if (i < (TwoThousandFortyEight.FIELD_SIZE - 1) && oldLine(i).value == oldLine(i + 1).value) {
        oldValue *= 2
        this.score.value += oldValue

        i = i + 1
      }

      helperList.add(new Tile(oldValue))
      i = i + 1
    }

    if (helperList.size() == 0) {
      return oldLine
    } else {
      while (helperList.size != TwoThousandFortyEight.FIELD_SIZE) {
        helperList.add(new Tile())
      }

      return helperList.toArray(new Array[Tile](TwoThousandFortyEight.FIELD_SIZE))
    }
  }

  def compareLines(line1: Array[Tile], line2: Array[Tile]): Boolean = {
    if (line1 == line2) {
      return true
    } else if (line1.length != line2.length) {
      return false
    }

    for (i <- line1.indices) {
      if (line1(i).value != line2(i).value) {
        return false
      }
    }

    return true
  }
}
