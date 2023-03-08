package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripImage

class MenuShape() : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: androidx.compose.ui.unit.LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Generic(
            Path().apply {
                val sw = size.width
                val sh = size.height /1.5f
                moveTo(
                    sw,  sh*0.380915385.toFloat()
                )
                cubicTo(sw, sh*0.966562637.toFloat(), sw*0.935567961.toFloat(),  (sh*0.93140989.toFloat()), sw*0.867458738.toFloat(),  (sh*0.86401978).toFloat())
                lineTo(0f, sh*0.0057.toFloat())
                lineTo(sw, 0f)
                close()

            });
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TestPolygonPreview() {
    AppTheme {
        val configuration = LocalConfiguration.current
        AppTheme() {
            Scaffold(
                modifier = Modifier.fillMaxSize(), topBar = {
                    SmallTopAppBar(
                        modifier = Modifier.clip(MenuShape()),
                        navigationIcon = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack, contentDescription = null
                                )
                            }
                        },
                        title = {
                            Text("trip id: ${1}")
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        actions = {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Filled.Share, contentDescription = null
                                )
                            }
                        },
                    )
                }, floatingActionButton = {
                    FloatingActionButton(
                        onClick = {},
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,

                        ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null,
                        )
                    }
                }) {


                TripImage(modifier = Modifier
                    .zIndex(1F)
                    .offset((configuration.screenWidthDp.dp) / 2 - 45.dp, -50.dp), imageUrl = "trips[0].imgUrl", contentDescription = null)



            }
        }
    }
}

