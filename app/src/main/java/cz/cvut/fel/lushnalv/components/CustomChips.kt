package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.cvut.fel.lushnalv.ui.theme.AppTheme


@Composable
fun CustomDayChip(
    selected: Boolean,
    text: String,
    weekEnd: Boolean = false
) {
    Surface(
        color = when {
            selected -> MaterialTheme.colorScheme.primary.copy(0.5f)
            else -> Color.Transparent
        },
        contentColor = MaterialTheme.colorScheme.outline,
        shape = CircleShape,
        border = BorderStroke(
            width = when {
                selected -> 0.dp
                else -> 1.dp
            },
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier
            .padding(start = 8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = CircleShape,
                modifier = Modifier.padding(4.dp),
                shadowElevation = 2.dp,
                border = BorderStroke((2).dp, if(weekEnd){MaterialTheme.colorScheme.error}else{MaterialTheme.colorScheme.primary}),
            ) {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .width(25.dp)
                        .height(25.dp),
                    contentAlignment = Alignment.Center,
                ) {



                }
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(start = 8.dp, end = 16.dp, top = 4.dp, bottom = 4.dp)
            )
        }

    }
}

@Composable
fun CustomSortChip(
    selected: Boolean,
    text: String,
    modifier: Modifier = Modifier
) {
        Surface(
            color = when {
                selected -> MaterialTheme.colorScheme.primary.copy(0.5f)
                else -> Color.Transparent
            },
            contentColor = MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(
                width = when {
                    selected -> 0.dp
                    else -> 1.dp
                },
                color = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 8.dp).testTag(text),
                textAlign = TextAlign.Center

            )

        }

}


@Preview("light theme", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview("dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun CustomChipPreview() {
    AppTheme() {
        Row(modifier =  Modifier.background(MaterialTheme.colorScheme.background)) {
            CustomDayChip(
                selected = true,
                text = "1 day",
            )
            CustomDayChip(
                selected = false,
                text = "2 day",
            )
        }

    Row(modifier = Modifier
        .background(MaterialTheme.colorScheme.background)
        .fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            CustomSortChip(selected = true, text = "Spending",  Modifier.fillMaxWidth())
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            CustomSortChip(selected = false, text = "Files",  Modifier.fillMaxWidth())
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            CustomSortChip(selected = false, text = "Comments", Modifier.fillMaxWidth())
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f)) {
            CustomSortChip(selected = false, text = "Team", Modifier.fillMaxWidth())
        }



    }
    }


}

