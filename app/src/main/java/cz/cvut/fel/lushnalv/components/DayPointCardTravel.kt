package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.*
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.models.*
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import java.time.LocalDateTime

@Composable
fun DayPointCardTravel(index: Int, dayPoint: DayPoint, onDrag: Boolean) {
    AppTheme {
        if(index==0){
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
            ) {}
        }else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(0.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier.padding(start = 8.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                        .width(60.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.secondaryContainer)
                                            .height(82.dp)
                                            .width(2.dp)
                                    ) {

                                    }
                                    val painter = if (onDrag) {
                                        painterResource(id = R.drawable.question_mark)
                                    } else {
                                        when (dayPoint.travelType) {
                                            TypeOfDayPointActive.AUTO -> painterResource(R.drawable.car_outline)
                                            TypeOfDayPointActive.PEDESTRIAN -> painterResource(R.drawable.directions_walk_outline)
                                        }
                                    }

                                    IconButton(onClick = { }) {
                                        Icon(
                                            modifier = Modifier.background(MaterialTheme.colorScheme.background),
                                            tint = MaterialTheme.colorScheme.primary,
                                            painter = painter,
                                            contentDescription = null
                                        )
                                    }


                                }
                            }
                            Row(
                                modifier = Modifier.padding(start = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Text(
                                    text = dayPoint.travelTime.toMinutes().getHoursAndMinutesString(),
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text(
                                    text = dayPoint.travelDistance.getKmMetersString(),
                                    color = MaterialTheme.colorScheme.outline,
                                    style = MaterialTheme.typography.titleSmall
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.background(MaterialTheme.colorScheme.error),
                            contentAlignment = Alignment.CenterEnd
                        ) {

                        }


                    }

                }

            }
        }

    }
}

@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PointCardActivePreview() {
    AppTheme {
        DayPointCardTravel(
            0,
            DayPoint(
                title = "Start point",
                date = LocalDateTime.of(2022, 10, 25, 5, 13),
                duration = java.time.Duration.ofMinutes(3),
                typeOfDayPoint = TypeOfDayPoint.FOOD,
                dayId = 23L,
                defaultPhoto = "https://source.unsplash.com/Y4YR9OjdIMk",
                photoListString = "https://source.unsplash.com/Y4YR9OjdIMk",
                travelTime = java.time.Duration.ofMinutes(5),
                travelType = TypeOfDayPointActive.AUTO,
                travelDistance = 1000,
                openingMessage = "dfgffdg"
            ),
            false
        )
    }
}
