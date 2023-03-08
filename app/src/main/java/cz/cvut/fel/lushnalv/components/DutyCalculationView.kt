package cz.cvut.fel.lushnalv.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cz.cvut.fel.lushnalv.models.DutyCalculation
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.CardShape
import cz.cvut.fel.lushnalv.utils.cut


@Composable
fun DutyCalculationView(duty: DutyCalculation, sourceUser: User, targetUser: User) {

    AppTheme() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
                .background(Color.Transparent),
            shape = CardShape,
            elevation = 3.dp,
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                val (
                    avatar1, avatar2, text, text2, icon
                ) = createRefs()
                Box(modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(avatar1) {
                        start.linkTo(parent.start, 0.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }) {

                    sourceUser?.imgUrl?.let {
                        CustomImageForTripMain(
                            imageUrl = it,
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

                }

                Column(modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(text) {
                        top.linkTo(parent.top, 0.dp)
                        bottom.linkTo(parent.bottom, 0.dp)
                        start.linkTo(avatar1.end, 0.dp)
                    }) {


                    Text(
                        text = sourceUser.name.cut(10),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "${duty.amount} ${duty.currency.name}",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleSmall
                    )

                }

                Icon(
                    tint = MaterialTheme.colorScheme.primary,
                    imageVector = Icons.Filled.ArrowForward, contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(icon) {
                            start.linkTo(text.end, 0.dp)
                            end.linkTo(text2.start, 0.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                )

                Column(modifier = Modifier
                    .padding(start = 8.dp)
                    .constrainAs(text2) {
                        top.linkTo(parent.top, 0.dp)
                        bottom.linkTo(parent.bottom, 0.dp)
                        end.linkTo(avatar2.start, 0.dp)
                    }) {


                    Text(
                        text = targetUser.name.cut(10),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )

                }

                Box(modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(avatar2) {
                        end.linkTo(parent.end, 0.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }) {

                    CustomImageForTripMain(
                        imageUrl = targetUser.imgUrl,
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

            }


        }

    }
}