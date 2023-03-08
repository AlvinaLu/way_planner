package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.models.Comment
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModel
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.utils.getHoursOrMinutesWithZero
import java.text.DateFormatSymbols
import java.time.LocalDateTime

@Composable
fun CommentCard(comment: Comment, author: User, dayPointTitle: String = "") {
    AppTheme() {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(end = 50.dp)
                .background(Color.Transparent), verticalAlignment = Alignment.Bottom
        ) {
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
            Card(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .background(Color.Transparent),
                shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 0.dp),
                elevation = 3.dp,
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    val (
                        name, text, time
                    ) = createRefs()
                    Column(modifier = Modifier.constrainAs(name) {
                        top.linkTo(parent.top, 4.dp)
                        start.linkTo(parent.start, 4.dp)
                        bottom.linkTo(parent.bottom, 4.dp)
                    }) {
                        author?.let {
                            Text(
                                modifier = Modifier.padding(
                                    start = 6.dp,
                                    bottom = 2.dp,
                                    end = 8.dp
                                ),
                                text = it.name,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            modifier = Modifier.padding(start = 8.dp, bottom = 2.dp, end = 8.dp),
                            text = comment.message,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 5
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            textAlign = TextAlign.End,
                            text = "${comment.date.dayOfMonth} ${DateFormatSymbols().shortMonths[comment.date.monthValue - 1]} ${comment.date.year} ${comment.date.hour.getHoursOrMinutesWithZero()}:${comment.date.minute.getHoursOrMinutesWithZero()}",
                            color = MaterialTheme.colorScheme.outline,
                            style = androidx.compose.material.MaterialTheme.typography.caption,
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun CommentCardYou(
    comment: Comment,
    author: User,
    dayPointTitle: String = "",
    viewModel: DayPointViewModel
) {
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                androidx.compose.material3.Text(text = stringResource(R.string.delete_note_confirm))
            },
            text = {
                androidx.compose.material3.Text("")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteComment(comment.commentId)
                        openDialog.value = false
                    },
                ) {
                    androidx.compose.material3.Text(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                        text = stringResource(R.string.confirm)
                    )
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    openDialog.value = false
                }) {
                    androidx.compose.material3.Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }
    AppTheme() {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 50.dp, end = 8.dp)
                .background(Color.Transparent),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = { openDialog.value = true }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3F)
                )
            }
            Card(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(Color.Transparent),
                shape = RoundedCornerShape(20.dp, 20.dp, 0.dp, 20.dp),
                elevation = 3.dp,
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    val (
                        name, text, time
                    ) = createRefs()
                    Column(modifier = Modifier
                        .constrainAs(name) {
                            top.linkTo(parent.top, 4.dp)
                            end.linkTo(parent.end, 4.dp)
                            bottom.linkTo(parent.bottom, 4.dp)
                        }, horizontalAlignment = Alignment.End
                    ) {
                        author?.let {
                            Text(
                                modifier = Modifier.padding(
                                    end = 6.dp,
                                    bottom = 2.dp,
                                    start = 8.dp
                                ),
                                textAlign = TextAlign.Center,
                                text = it.name,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Text(
                            modifier = Modifier.padding(end = 8.dp, bottom = 2.dp, start = 8.dp),
                            text = comment.message,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 5
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            textAlign = TextAlign.End,
                            text = "${comment.date.dayOfMonth} ${DateFormatSymbols().shortMonths[comment.date.monthValue - 1]} ${comment.date.year} ${comment.date.hour.getHoursOrMinutesWithZero()}:${comment.date.minute.getHoursOrMinutesWithZero()}",
                            color = MaterialTheme.colorScheme.outline,
                            style = androidx.compose.material.MaterialTheme.typography.caption,
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
fun MessageCardPreview() {
    AppTheme {
        Column(Modifier.fillMaxWidth()) {
            CommentCard(
                comment = Comment(
                    1,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit...",
                    LocalDateTime.now(),
                    author = 9,
                    dayPointId = 0
                ),
                author = User(
                    userId = 9,
                    email = "alvina0213@gmail.com",
                    name = "Alvina Lushnikova",
                    imgUrl = "https://firebasestorage.googleapis.com/v0/b/wayplanner.appspot.com/o/avatars%2Fhippo.png?alt=media&token=2b1238f5-7e62-4ca4-85b1-46f011e40631"
                ),
                dayPointTitle = ""

            )
        }
    }
}