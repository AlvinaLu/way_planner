package cz.cvut.fel.lushnalv.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cz.cvut.fel.lushnalv.Routes
import cz.cvut.fel.lushnalv.data.local.AppPreferences




@Composable
fun NavigationListItem(
    appPreferences: AppPreferences,
    item: NavigationDrawerItem,
    unreadBubbleColor: Color = Color(0xFF0FFF93),
    itemClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                itemClick()
            }.testTag("log_out_tag")
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // icon and unread bubble
        Box {

            Icon(
                modifier = Modifier
                    .padding(all = if (item.showUnreadBubble && item.label == "Messages") 5.dp else 2.dp)
                    .size(size = if (item.showUnreadBubble && item.label == "Messages") 24.dp else 28.dp),
                imageVector = item.image,
                contentDescription = null,
                tint = Color.White
            )

            // unread bubble
            if (item.showUnreadBubble) {
                Box(
                    modifier = Modifier
                        .size(size = 8.dp)
                        .align(alignment = Alignment.TopEnd)
                        .background(color = unreadBubbleColor, shape = CircleShape)
                )
            }
        }

        // label
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = item.label,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun prepareNavigationDrawerItems(appPreferences: AppPreferences, navigate: (String) -> Unit, ): List<NavigationDrawerItem> {
    val itemsList = arrayListOf<NavigationDrawerItem>()

    itemsList.add(
        NavigationDrawerItem(
            image = Icons.Filled.Settings,
            label = "Home",
            showUnreadBubble = true,
            route = Routes.AuthRoute.route
        )
    )
    itemsList.add(
        NavigationDrawerItem(
            image = Icons.Filled.Settings,
            label = "Messages",
            showUnreadBubble = true,
            route = Routes.AuthRoute.route
        )
    )
    itemsList.add(
        NavigationDrawerItem(
            image = Icons.Filled.Settings,
            label = "Notifications",
            showUnreadBubble = true,
            route = Routes.AuthRoute.route
        )
    )
    itemsList.add(
        NavigationDrawerItem(
            image = Icons.Filled.Settings,
            label = "Logout",
            showUnreadBubble = false,
            route = Routes.AuthRoute.route
        )
    )

    return itemsList
}

data class NavigationDrawerItem(
    val image: ImageVector,
    val label: String,
    val showUnreadBubble: Boolean = false,
    val route: String
)