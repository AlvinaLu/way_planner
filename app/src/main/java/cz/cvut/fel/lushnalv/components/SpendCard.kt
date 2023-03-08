package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.*
import cz.cvut.fel.lushnalv.ui.daypoint.*
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.CardShape
import cz.cvut.fel.lushnalv.utils.cut
import java.util.*


@Composable
fun SpendCard(duty: Duty, listUsers: List<User>, viewModel: DayPointViewModel) {

    val showDialog = remember { mutableStateOf(false) }
    val author = listUsers.filter { it.userId == duty.author }.firstOrNull()
    var listUsersWithOutAuthor = listUsers.filter { it.userId != duty.author }
    if (listUsersWithOutAuthor.size > 5) {
        listUsersWithOutAuthor = listUsersWithOutAuthor.subList(0, 5)
    }

    if (showDialog.value) {
        OpenDutyInfo(duty, listUsers, viewModel) { showDialog.value = it }
    }


    AppTheme() {
        Row( horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
                    .background(Color.Transparent)
                    .clickable { showDialog.value = true },
                shape = CardShape,
                elevation = 3.dp,
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    val (
                        avatar1, avatar2, text, icon
                    ) = createRefs()
                    Box(modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(avatar1) {
                            start.linkTo(parent.start, 0.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }) {

                        author?.imgUrl?.let {
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
                            text = duty.title.cut(25),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "${duty.amount} ${duty.currency.name}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall
                        )
                    }


                    Box(modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(avatar2) {
                            end.linkTo(parent.end, 0.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }) {

                        LazyRow(reverseLayout = true) {
                            items(listUsersWithOutAuthor.size) {
                                Box(
                                    modifier = Modifier
                                        .offset(((it) * 25).dp)
                                        .zIndex(1F)
                                ) {
                                    CustomImageForTripMain(
                                        imageUrl = listUsersWithOutAuthor[it].imgUrl,
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
                    if (listUsersWithOutAuthor.isNotEmpty()) {
                        Icon(
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = Icons.Filled.ArrowForward, contentDescription = null,
                            modifier = Modifier
                                .padding(8.dp)
                                .constrainAs(icon) {
                                    start.linkTo(text.end, 0.dp)
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                }
                        )
                    }


                }

            }
        }
    }
}





@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PersonCardPreview() {
    AppTheme {
        //SpendCard(duty)

    }
}