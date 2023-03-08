package cz.cvut.fel.lushnalv.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.Duty
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModel
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import java.math.BigDecimal


@Composable
fun OpenDutyInfo(
    duty: Duty, listUsers: List<User>, viewModel: DayPointViewModel, setShowDialog: (Boolean) -> Unit,
) {

    val author = listUsers.filter { it.userId == duty.author }.firstOrNull()
    val listUsersWithOutAuthor = listUsers.filter { it.userId != duty.author }
    val appPreferences =
        AppPreferences.create(LocalContext.current.applicationContext)

    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                androidx.compose.material3.Text(text = stringResource(R.string.delete_expense_confirm))
            },
            text = {
                androidx.compose.material3.Text("")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        viewModel.deleteDuty(duty.dutyId)
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
                androidx.compose.material3.OutlinedButton(onClick = {
                    openDialog.value = false
                }) {
                    androidx.compose.material3.Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }

    AppTheme() {
        Dialog(onDismissRequest = { setShowDialog(false) }) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),

                ) {
                Box(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.total),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "${duty.amount} ${duty.currency}",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                        )

                        Text(
                            text = "${duty.title}",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                        )
                        if (listUsers.size > 1) {
                            Text(
                                text = "Split between ${listUsers.size} people",
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = stringResource(R.string.whole_balance),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                textAlign = TextAlign.Center,
                            )
                        }


                        val part = duty.amount / BigDecimal(listUsers.size)

                        author?.name?.let {
                            UserCardDuty(user = author, part, duty, true)
                        }

                        listUsersWithOutAuthor.forEach {
                            UserCardDuty(user = it, part, duty, false)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {

                            OutlinedButton(onClick = {
                                setShowDialog(
                                    false
                                )
                            }) {
                                Text(text = stringResource(R.string.cancel))
                            }
                            if (appPreferences.userDetails?.id == duty.author) {
                                Button(
                                    onClick = {
                                        openDialog.value = true
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                ) {
                                    Text(
                                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                                        text = stringResource(R.string.delete)
                                    )
                                }
                            }
                        }

                    }


                }
            }


        }
    }
}