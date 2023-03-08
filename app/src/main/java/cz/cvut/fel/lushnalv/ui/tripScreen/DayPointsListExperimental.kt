package cz.cvut.fel.lushnalv.ui.tripScreen

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.components.DayPointCard
import cz.cvut.fel.lushnalv.components.DayPointCardTravel
import cz.cvut.fel.lushnalv.models.DayPoint
import cz.cvut.fel.lushnalv.models.DayWithPoints
import cz.cvut.fel.lushnalv.ui.theme.tripScreen.TripViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun DayPointsList(
    day: DayWithPoints? = null,
    dayPointsList: MutableList<DayPoint>,
    appState: WayPlannerAppState,
    tripId: Long,
    onMove: (Int, Int) -> Unit,
    viewModel: TripViewModel
) {
    Log.i("ITEM","DayPointsList recomposition")

    val scope = rememberCoroutineScope()
    var overScrollJob by remember { mutableStateOf<Job?>(null) }
    var onDrag by remember { mutableStateOf<Boolean>(false) }
    val dragDropListState = rememberDragDropListState(onMove = onMove)


    Column(
        modifier = Modifier
            .fillMaxSize().padding(top=190.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGesturesAfterLongPress(
                        onDrag = { change, offset ->
                            change.consumeAllChanges()
                            dragDropListState.onDrag(offset = offset)

                            if (overScrollJob?.isActive == true)
                                return@detectDragGesturesAfterLongPress

                            dragDropListState
                                .checkForOverScroll()
                                .takeIf { it != 0f }
                                ?.let {
                                    overScrollJob = scope.launch {
                                        dragDropListState.lazyListState.scrollBy(it)
                                    }
                                } ?: kotlin.run { overScrollJob?.cancel() }
                        },
                        onDragStart = { offset ->
                            Log.i("Offset", offset.toString())
                            dragDropListState.onDragStart(offset);
                            onDrag = true
                        },
                        onDragEnd = {
                            dragDropListState.onDragInterrupted();
                            onDrag = false;
                            viewModel.reorderDayPoints()
                        },
                        onDragCancel = { dragDropListState.onDragInterrupted();
                            onDrag = false }
                    )
                }
                .fillMaxSize()
                .padding(top = 10.dp, start = 10.dp, end = 10.dp),
            state = dragDropListState.lazyListState
        ) {
            itemsIndexed(dayPointsList) { index, item ->
                DayPointCardTravel(index, item, onDrag)
                Column(modifier = Modifier
                    .composed {
                        val offsetOrNull = dragDropListState.elementDisplacement.takeIf {
                            index == dragDropListState.currentIndexOfDraggedItem
                        }
                        if (offsetOrNull!=null) {
                            Log.i("ITEM",item.title)
                            return@composed Modifier.zIndex(10F).graphicsLayer { translationY = offsetOrNull }
                        } else {
                            return@composed Modifier.graphicsLayer { translationY =  0f }
                        }
                    }
                    .fillMaxWidth()
                ) {
                    DayPointCard(index, item, appState, tripId, day, viewModel)
                }

            }
            item { Box(modifier = Modifier.height(72.dp)) }

        }
    }
}

// inspiration by https://github.com/MakeItEasyDev/Jetpack-Compose-Drag-And-Drop-List-Item
@Composable
fun rememberDragDropListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onMove: (Int, Int) -> Unit,
): DragDropListState {
    return remember { DragDropListState(lazyListState = lazyListState, onMove = onMove) }
}

class DragDropListState(
    val lazyListState: LazyListState,
    private val onMove: (Int, Int) -> Unit
) {
    var draggedDistance by mutableStateOf(0f)
    var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    var currentIndexOfDraggedItem by mutableStateOf<Int?>(null)
    val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let {
            Pair(it.offset, it.offsetEnd)
        }
    val elementDisplacement: Float?
        get() = currentIndexOfDraggedItem
            ?.let {
                lazyListState.getVisibleItemInfoFor(absolute = it)
            }
            ?.let { item ->
                (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
            }

    val currentElement: LazyListItemInfo?
        get() = currentIndexOfDraggedItem?.let {
            lazyListState.getVisibleItemInfoFor(absolute = it)
        }

    var overScrollJob by mutableStateOf<Job?>(null)

    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                currentIndexOfDraggedItem = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDragInterrupted() {
        draggedDistance = 0f
        currentIndexOfDraggedItem = null
        initiallyDraggedElement = null
        overScrollJob?.cancel()
    }

    fun onDrag(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hovered ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item ->
                        item.offsetEnd < startOffset || item.offset > endOffset || hovered.index == item.index
                    }
                    .firstOrNull { item ->
                        val delta = startOffset - hovered.offset
                        when {
                            delta > 0 -> (endOffset > item.offsetEnd)
                            else -> (startOffset < item.offset)
                        }
                    }?.also { item ->
                        currentIndexOfDraggedItem?.let { current ->
                            onMove.invoke(current, item.index)
                        }
                        currentIndexOfDraggedItem = item.index
                    }
            }
        }
    }

    fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff ->
                    diff > 0
                }
                draggedDistance < 0 -> (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff ->
                    diff < 0
                }
                else -> null
            }
        } ?: 0f
    }
}

fun LazyListState.getVisibleItemInfoFor(absolute: Int): LazyListItemInfo? {
    return this.layoutInfo.visibleItemsInfo.getOrNull(absolute - this.layoutInfo.visibleItemsInfo.first().index)
}

val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size

fun <T> MutableList<T>.move(from: Int, to: Int) {
    if (from == to)
        return
    val element = this.removeAt(from) ?: return
    this.add(to, element)
}
