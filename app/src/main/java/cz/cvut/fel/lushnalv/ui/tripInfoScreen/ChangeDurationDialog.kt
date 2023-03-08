package cz.cvut.fel.lushnalv.ui.tripInfoScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.chargemap.compose.numberpicker.FullHours
import com.chargemap.compose.numberpicker.Hours
import com.chargemap.compose.numberpicker.HoursNumberPicker
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModel
import java.time.Duration
import java.time.LocalDateTime


@Composable
fun ChangeDurationDialog(
    viewModel: TripViewModel,
    setShowDialog: (Boolean) -> Unit,
    dayPointId: Long,
    duration: Duration
) {
    var pickerValue by remember {
        mutableStateOf<Hours>(
            FullHours(
                duration.toHours().toInt(),
                duration.toMinutes().toInt() - (duration.toHours().toInt() * 60)
            )
        )
    }

    AppTheme() {
        Dialog(onDismissRequest = { setShowDialog(false) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.chande_duration),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 3,
                        )
                        HoursNumberPicker(
                            modifier = Modifier.fillMaxWidth(1f),
                            leadingZero = true,
                            dividersColor = MaterialTheme.colorScheme.primary,
                            value = pickerValue,
                            onValueChange = {
                                pickerValue = it
                            },
                            hoursDivider = {
                                Text(
                                    modifier = Modifier.size(24.dp),
                                    textAlign = TextAlign.Center,
                                    text = ":"
                                )
                            },
                            textStyle =
                            MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            OutlinedButton(onClick = {
                                setShowDialog(
                                    false
                                )
                            }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            Button(
                                onClick = {
                                    viewModel.changeDayPointsDuration(
                                        dayPointId = dayPointId,
                                        duration = Duration.ofHours(pickerValue.hours.toLong())
                                            .plus(
                                                Duration.ofMinutes(pickerValue.minutes.toLong())
                                            )
                                    )
                                    setShowDialog(
                                        false
                                    )
                                },
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                    text = stringResource(R.string.confirm)
                                )
                            }
                        }


                    }
                }
            }


        }
    }
}

@Composable
fun ChangeTimeDialog(
    viewModel: TripViewModel,
    setShowDialog: (Boolean) -> Unit,
    dayPointId: Long,
    localDateTime: LocalDateTime
) {
    var date = localDateTime
    var pickerValue by remember { mutableStateOf<Hours>(FullHours(date.hour, date.minute)) }

    AppTheme() {
        Dialog(onDismissRequest = { setShowDialog(false) }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.change_start_time),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 3,
                        )
                        HoursNumberPicker(
                            modifier = Modifier.fillMaxWidth(1f),
                            leadingZero = true,
                            dividersColor = MaterialTheme.colorScheme.primary,
                            value = pickerValue,
                            onValueChange = {
                                pickerValue = it
                            },
                            hoursDivider = {
                                Text(
                                    modifier = Modifier.size(24.dp),
                                    textAlign = TextAlign.Center,
                                    text = ":"
                                )
                            },
                            textStyle =
                            MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onBackground)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            OutlinedButton(onClick = {
                                setShowDialog(
                                    false
                                )
                            }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            Button(
                                onClick = {
                                    val finalDate = LocalDateTime.of(date.year, date.month, date.dayOfMonth, pickerValue.hours, pickerValue.minutes )
                                    viewModel.changeDayPointsStartTime(
                                        dayPointId = dayPointId,
                                        dayTime = finalDate
                                    )
                                    setShowDialog(
                                        false
                                    )
                                },
                            ) {
                                Text(
                                    modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                    text = stringResource(R.string.confirm)
                                )
                            }
                        }


                    }
                }
            }


        }
    }
}