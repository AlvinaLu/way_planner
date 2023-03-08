package cz.cvut.fel.lushnalv.ui.daypoint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.CustomImageForTripMain
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.TripWithUsers
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.utils.cut

@Composable
fun UsersView(trip: TripWithUsers) {

    AppTheme() {
        LazyColumn(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(count = trip.users.size) { index->
                UserCard(user = trip.users[index])
            }

        }
    }
}


@Composable
fun UserCard(user: User){
    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)

    AppTheme() {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth().testTag("user_card_tag")
        ) {
            val (
                avatar1, text,
            ) = createRefs()
            Box(modifier = Modifier
                .padding(8.dp)
                .constrainAs(avatar1) {
                    start.linkTo(parent.start, 0.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }) {

                CustomImageForTripMain(
                    imageUrl = user.imgUrl,
                    contentDescription = null,
                    borderStroke = BorderStroke(
                        (2).dp,
                        MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .height(50.dp)
                        .width(50.dp),
                    shape = CircleShape
                )

            }

            Column(modifier = Modifier
                .padding(start = 8.dp)
                .constrainAs(text) {
                    top.linkTo(parent.top, 0.dp)
                    bottom.linkTo(parent.bottom, 0.dp)
                    start.linkTo(avatar1.end, 0.dp)
                }) {

                var text = user.name.cut(20)
                if(appPreferences.userDetails?.id == user.userId){
                    text = stringResource(R.string.you)
                }
                androidx.compose.material.Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )



            }





        }
    }
}
