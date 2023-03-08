package cz.cvut.fel.lushnalv.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.Duty
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import java.math.BigDecimal



@Composable
fun UserCardDuty(user: User, amount: BigDecimal, duty: Duty, author: Boolean) {
    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)

    AppTheme() {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val (
                avatar1, text, amountView
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
                if (author) {
                    Text(
                        text = stringResource(R.string.paid_by),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                    )
                }
                var text = user.name
                if (appPreferences.userDetails?.id == user.userId) {
                    text = stringResource(R.string.you)
                }
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )


            }

            Column(modifier = Modifier
                .padding(end = 8.dp)
                .constrainAs(amountView) {
                    top.linkTo(parent.top, 0.dp)
                    bottom.linkTo(parent.bottom, 0.dp)
                    end.linkTo(parent.end, 0.dp)
                }) {

                Text(
                    text = "${amount} ${duty.currency}",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium
                )

            }


        }
    }
}