package cz.cvut.fel.lushnalv.ui.daypoint

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.components.*
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.DutyWithUsers
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.MainSheetShape
import cz.cvut.fel.lushnalv.ui.theme.authorization.LoadingState
import cz.cvut.fel.lushnalv.utils.getHoursOrMinutesWithZero
import kotlinx.coroutines.CoroutineScope
import java.text.DateFormatSymbols

@OptIn(
    ExperimentalPermissionsApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun DayPointContainer(
    viewModel: DayPointViewModel,
    dayPointId: Long,
    navController: NavController,
    permissionsStateCamera: MultiplePermissionsState,
    bottomSheetScaffoldState: BottomSheetScaffoldState,
    coroutineScope: CoroutineScope,
    dutyListWithUsers: List<DutyWithUsers>?,
    clickedButtonAddPhoto: () -> Unit
) {

    val dayPoint by viewModel.dayPointById.observeAsState()
    val loadingState: LoadingState by viewModel.loadingState.collectAsState()
    val dayPointChipState: DayPointChips by viewModel.dayPointChipState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val userListByDayPointId = viewModel.userListByDayPointId.observeAsState()
    val currentTrip = viewModel.tripById.observeAsState()
    val newCommentState: NewCommentState = remember { NewCommentState() }


    AppTheme() {

        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.error),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(if (bottomSheetScaffoldState.bottomSheetState.isExpanded) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary),
                shape = if (bottomSheetScaffoldState.bottomSheetState.isExpanded) RectangleShape else MainSheetShape,
                color = MaterialTheme.colorScheme.background,
                shadowElevation = if (bottomSheetScaffoldState.bottomSheetState.isExpanded) 0.dp else 3.dp,
                tonalElevation = if (bottomSheetScaffoldState.bottomSheetState.isExpanded) 0.dp else 3.dp
            ) {
                Column() {
                    Surface(
                        shape = RoundedCornerShape(30.dp),
                        shadowElevation = 4.dp
                    ) {
                        DayPointPhotoGallery(
                            viewModel = viewModel,
                            permissionsStateCamera,
                            bottomSheetScaffoldState,
                            coroutineScope,
                            clickedButtonAddPhoto, dayPointId
                        )
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp)
                    ) {
                        if (dayPoint != null && dayPoint?.dayPoint != null) {
                            val date = dayPoint!!.dayPoint.date
                            val duration = dayPoint!!.dayPoint.duration
                            val dateWithDuration = date.plusMinutes(duration.toMinutes())
                            var text =
                                "${date.dayOfMonth} ${DateFormatSymbols().shortMonths[date.monthValue - 1]} ${date.year} "
                            text += if (duration.toMinutes() == 0L) {
                                "${date.hour.getHoursOrMinutesWithZero()}:${date.minute.getHoursOrMinutesWithZero()}"
                            } else {
                                "${date.hour.getHoursOrMinutesWithZero()}:${date.minute.getHoursOrMinutesWithZero()}  -  " +
                                        "${dateWithDuration.hour.getHoursOrMinutesWithZero()}:${dateWithDuration.minute.getHoursOrMinutesWithZero()}"
                            }
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp, bottom = 8.dp),
                                text = text,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Divider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.75f)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        DayPointChips.values().forEachIndexed { index, it ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                CustomSortChip(
                                    selected = dayPointChipState == it,
                                    text = it.name,
                                    Modifier.clickable { viewModel.changeDayPointChips(index) }.testTag(it.name))
                            }

                        }
                    }
                    if (loadingState.status == LoadingState.Status.RUNNING) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    } else {
                        if (loadingState.status == LoadingState.Status.FAILED) {
                            viewModel.changeStatus(LoadingState.IDLE)
                            Toast.makeText(
                                LocalContext.current,
                                loadingState.msg ?: "Error",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        when (dayPointChipState.ordinal) {
                            0 -> {
                                if (dayPoint?.duties?.isNotEmpty() == true && !userListByDayPointId.value.isNullOrEmpty()) {
                                    val listDuties =
                                        dayPoint?.duties!!.sortedByDescending { it.dayPointId }
                                    val listUsers = userListByDayPointId.value
                                    if (!listDuties.isNullOrEmpty()) {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxHeight(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                        ) {
                                            items(count = listDuties.size) { dutyIndex ->
                                                val users =
                                                    listUsers!!.filter { it.duty.dutyId == listDuties[dutyIndex].dutyId }
                                                        .firstOrNull()
                                                if (users != null) {
                                                    SpendCard(
                                                        duty = listDuties[dutyIndex],
                                                        users.users,
                                                        viewModel
                                                    )
                                                }
                                            }
                                            item { Box(modifier = Modifier.height(72.dp)) }
                                        }
                                    } else {
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = stringResource(R.string.isnt_expense),
                                            textAlign = TextAlign.Center,
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onBackground,
                                        )
                                    }
                                } else {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = stringResource(R.string.isnt_expense),
                                        textAlign = TextAlign.Center,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                            1 -> {
                                val documentList = mutableListOf<String>()
                                val isOwner = currentTrip.value?.trip?.ownerId == AppPreferences.create(LocalContext.current.applicationContext).userDetails?.id
                                dayPoint?.dayPoint?.getDocumentList()?.forEach {
                                    if (it.isNotEmpty()) {
                                        documentList.add(it)
                                    }
                                }
                                if (!documentList.isNullOrEmpty()) {
                                    LazyColumn(
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                    ) {
                                        items(count = documentList.size) { index ->
                                            if (documentList[index].toString().isNotEmpty()) {
                                                FileCard(
                                                    url = documentList[index].toString(),
                                                    viewModel,
                                                    dayPointId, isOwner
                                                )
                                            }
                                        }
                                        item { Box(modifier = Modifier.height(72.dp)) }
                                    }
                                } else{
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = stringResource(R.string.isnt_doc),
                                        textAlign = TextAlign.Center,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                            }
                            2 -> {
                                Column() {
                                    NewComment(
                                        newCommentState = newCommentState,
                                        viewModel,
                                        dayPointId
                                    )
                                    if (dayPoint?.comments?.isNotEmpty() == true && currentTrip.value?.users != null) {
                                        var commentList = dayPoint?.comments
                                        val listUsers = currentTrip.value!!.users
                                        if (!commentList.isNullOrEmpty()) {
                                            commentList = commentList.sortedByDescending { it.date }
                                            LazyColumn(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(start = 8.dp, end = 8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                            ) {
                                                items(count = commentList.size) { index ->
                                                    val author =
                                                        listUsers.filter { it.userId == commentList[index].author }
                                                            .firstOrNull()
                                                    if (author != null) {
                                                        val appPreferences =
                                                            AppPreferences.create(LocalContext.current.applicationContext)
                                                        if (appPreferences.userDetails?.id == commentList[index].author) {
                                                            CommentCardYou(
                                                                comment = commentList[index],
                                                                author = author, viewModel = viewModel
                                                            )
                                                        } else {
                                                            CommentCard(
                                                                comment = commentList[index],
                                                                author = author
                                                            )
                                                        }
                                                    }
                                                }
                                                item { Box(modifier = Modifier.height(72.dp)) }
                                            }
                                        }
                                    }
                                }
                            }
                            else -> {}
                        }
                    }


                }
            }
        }

    }

}