package cz.cvut.fel.lushnalv.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Shapes

val Shapes = Shapes(
    small = RoundedCornerShape(percent = 50),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(0.dp)
)


val MainSheetShape = RoundedCornerShape(
    topStart = 30.dp,
    topEnd = 30.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

val BottomSheetShape = RoundedCornerShape(
    topStart = 30.dp,
    topEnd = 30.dp,
    bottomStart = 0.dp,
    bottomEnd = 0.dp
)

val CardShape = RoundedCornerShape(
    topStart = 10.dp,
    topEnd = 10.dp,
    bottomStart = 10.dp,
    bottomEnd = 10.dp
)


val DragHandleShape = RoundedCornerShape(
    topStart = 3.dp,
    topEnd = 3.dp,
    bottomStart = 3.dp,
    bottomEnd = 3.dp
)

class TriangleEdgeShape(val offset: Int) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val trianglePath = Path().apply {
            moveTo(x = 0f, y = size.height-offset)
            lineTo(x = 0f, y = size.height)
            lineTo(x = 0f + offset, y = size.height)
        }
        return Outline.Generic(path = trianglePath)
    }
}
