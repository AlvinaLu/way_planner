package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.utils.cut

@Composable
fun CheckBoxUser(user: User, listMember: List<Long>, checkedChange: (Long) -> Unit) {
    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)

    DropdownMenuItem(onClick = { }) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 6.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomImageForTripMain(
                imageUrl = user.imgUrl,
                contentDescription = null,
                modifier = Modifier
                    .height(30.dp)
                    .width(30.dp),
                shape = CircleShape
            )
            Spacer(modifier = Modifier.width(8.dp))
            var text =
                if (appPreferences.userDetails != null && appPreferences.userDetails?.id == user.userId) {
                    "You"
                } else {
                    user.name.cut(10)
                }
            Text(
                modifier = Modifier.weight(1F),
                text = text,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )

            Checkbox(
                modifier = Modifier.clip(CircleShape),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.tertiaryContainer,
                    uncheckedColor = MaterialTheme.colorScheme.outline,
                    checkmarkColor = MaterialTheme.colorScheme.background,
                ),
                checked = listMember.contains(user.userId),
                onCheckedChange = { checkedChange(user.userId) })
        }

    }
}


@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CheckBoxUserPreview() {
    AppTheme {

    }
}