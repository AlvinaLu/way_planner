package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.utils.cut


@Composable
fun ChipForCreateTrip(
    user: User,
    onClick: (Long) -> Unit,
    onDelete: (String) -> Unit = {},
    email: Boolean = false,
    showDelete: Boolean = false,
) {
    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)

    Surface(
        color = MaterialTheme.colorScheme.outline.copy(0.3f),
        contentColor = MaterialTheme.colorScheme.onBackground,
        shape = CircleShape,
        modifier = Modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomRoundImageForChip(
                imageUrl = user.imgUrl,
                modifier = Modifier.padding(4.dp),
                contentDescription = null,
                height = 20.dp,
                width = 20.dp,
                shadow = 1.dp,
                borderStroke = BorderStroke(0.dp, MaterialTheme.colorScheme.outline.copy(0.3f)),
                shape = CircleShape

            )
            Spacer(modifier = Modifier.width(4.dp))
            if (appPreferences.userDetails?.id == user.userId) {
                Box() {
                    androidx.compose.material3.Text(
                        text = "Just you",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 2.dp),
                        maxLines = 1
                    )
                }

            } else {
                androidx.compose.material3.Text(
                    text = user.name.cut(6),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(4.dp))


            if (appPreferences.userDetails?.id != user.userId && !email && !showDelete) {
                Icon(
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .clickable { onClick(user.userId) },
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Clear"
                )
            }else if (!showDelete){
                Icon(
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier
                        .padding(end = 2.dp)
                        .clickable { onDelete(user.email) },
                    imageVector = Icons.Filled.Clear,
                    contentDescription = "Clear"
                )
            }


        }

    }

}


@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ChipForCreateTripPreview() {
    AppTheme {
    }
}