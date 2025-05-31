package com.multiplatform.coinbo.coins.presentation.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PerformanceChart(
  modifier: Modifier = Modifier,
  nodes: List<Double>,
  profitColor: Color,
  lossColor: Color,
) {
  if (nodes.isEmpty()) return

  val max = nodes.maxOrNull() ?: return
  val min = nodes.minOrNull() ?: return
  val lineColor = if (nodes.last() > nodes.first()) profitColor else lossColor

  // is a drawing surface give us a drawing scope to draw custom graphics
  Canvas(
    modifier = modifier.fillMaxSize(),
  ) {
    // path is a collection of points that we can draw on the canvas
    val path = Path()
    // for each node in the list, we calculate the x and y coordinates based on the index and value
    nodes.forEachIndexed { index, value ->
      val x = index * (size.width / (nodes.size - 1))
      val y = size.height * (1 - ((value - min) / (max - min)).toFloat())

      if (index == 0) {
        path.moveTo(x, y)
      } else {
        path.lineTo(x, y)
      }
    }
    drawPath(
      path = path,
      color = lineColor,
      style = Stroke(width = 3.dp.toPx()),
    )
  }
}
