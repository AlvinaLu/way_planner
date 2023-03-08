package cz.cvut.fel.lushnalv.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.utils.checkForInternet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapAppBar(
    title: String,
    navigationIcon: @Composable (() -> Unit)? = null,
    background: Color = MaterialTheme.colorScheme.primary,
    navigate: () -> Unit,
) {
    AppTheme() {
        val configuration = LocalConfiguration.current

        Surface(modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Scaffold(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .height(200.dp),
                topBar = {
                    SmallTopAppBar(
                        modifier = Modifier.zIndex(1F),
                        navigationIcon = {
                            IconButton(onClick = { navigate()}) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        actions = {
                            if(!checkForInternet(LocalContext.current.applicationContext)) {
                                Icon(
                                    modifier = Modifier.height(24.dp).width(24.dp),
                                    painter = painterResource(id = R.drawable.internet),
                                    tint = MaterialTheme.colorScheme.error,
                                    contentDescription = "No internet connection"
                                )
                            }
                        },
                        title = {
                            Text(text = "", color = MaterialTheme.colorScheme.onPrimary)
                        },
                        colors = TopAppBarDefaults.smallTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    )
                },
            ) {

                    Surface(
                        modifier = Modifier
                            .zIndex(3F)
                            .offset((configuration.screenWidthDp.dp) / 2 - 45.dp, (-55).dp),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Image(
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary).height(90.dp)
                                .width(90.dp),
                            painter = painterResource(id = R.drawable.road_sign),
                            contentDescription = null
                        )
                    }


                Surface(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .offset(0.dp, -1.dp),
                    shape = MenuShape(),
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary)
                            .height(130.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = title,
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleLarge
                            )

                        }
                    }

                }

            }


        }
    }

}