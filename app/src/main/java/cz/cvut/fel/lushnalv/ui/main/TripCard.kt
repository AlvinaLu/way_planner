import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cz.cvut.fel.lushnalv.Routes
import cz.cvut.fel.lushnalv.WayPlannerAppState
import cz.cvut.fel.lushnalv.components.CustomImageForMain
import cz.cvut.fel.lushnalv.models.Trip
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.CardShape
import cz.cvut.fel.lushnalv.utils.cut
import java.text.DateFormatSymbols

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TripCard( navigate: (String) -> Unit, trip: Trip, appState: WayPlannerAppState) {
    AppTheme() {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, start = 8.dp, end = 8.dp, bottom = 0.dp)
                .background(Color.Transparent),
            shape = CardShape,
            elevation = 3.dp,
            onClick = { navigate(Routes.TripRoute.createRoute(trip.tripId)) }
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                val (
                    img, bottomBox
                ) = createRefs()

                CustomImageForMain(
                    imageUrl = trip.defaultPhoto,
                    contentDescription = null,
                    modifier = Modifier.height(250.dp)
                        .shadow(1.dp).constrainAs(img) {
                        start.linkTo(parent.start, 0.dp)
                    },
                    shape = CardShape
                )
                Box(modifier = Modifier
                    .background(Color(0, 0, 0, 0x65))
                    .fillMaxWidth()
                    .constrainAs(bottomBox) {
                        bottom.linkTo(parent.bottom, 0.dp)
                        start.linkTo(parent.start, 0.dp)
                        end.linkTo(parent.end, 0.dp)
                    }) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp, bottom = 12.dp, top = 12.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            text = trip.title.cut(20)
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp, bottom = 12.dp, top = 12.dp, end = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            text = "${trip.startDay.dayOfMonth} ${DateFormatSymbols().shortMonths[trip.startDay.monthValue-1].lowercase()}  - ${trip.endDay.dayOfMonth} ${DateFormatSymbols().shortMonths[trip.endDay.monthValue-1].lowercase()}"
                        )
                    }

                }
            }


        }


    }

}






