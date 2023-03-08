package cz.cvut.fel.lushnalv.components

import android.Manifest
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.rememberWayPlannerAppState
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModel
import cz.cvut.fel.lushnalv.ui.daypoint.DayPointViewModelFactory
import cz.cvut.fel.lushnalv.ui.daypoint.LoadingDocumentState
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.CardShape


@Composable
fun FileCard(url: String, viewModel: DayPointViewModel, dayPointId: Long, isOwner: Boolean) {

    val context = LocalContext.current.applicationContext
    val documentList by viewModel.stateDocumentList.collectAsState()

    val state = documentList[url] ?: LoadingDocumentState.IDLE
    val openDialog = remember { mutableStateOf(false) }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Text(text = stringResource(R.string.delete_document_confirm))
            },
            text = {
                Text("")
            },
            confirmButton = {
                androidx.compose.material3.Button(
                    onClick = {
                        viewModel.deleteDocument(url, dayPointId = dayPointId)
                        openDialog.value = false
                    },
                ) {
                    Text(
                        modifier = Modifier.padding(start = 12.dp, end = 12.dp),
                        text = stringResource(R.string.confirm)
                    )
                }
            },
            dismissButton = {
                androidx.compose.material3.OutlinedButton(onClick = {
                    openDialog.value = false
                }) {
                    Text(text = stringResource(R.string.cancel))
                }
            }
        )
    }


        val text =
            url.replaceBefore("%2F", "").replaceAfter("-firebase-id-", "").replace("-firebase-id-", "").replace("%2F", "")


        val launcherWrite = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.downloadDocument(url, text)
                Log.d("FileCard", "PERMISSION GRANTED")

            } else {
                // Permission Denied: Do something
                Log.d("FileCard", "PERMISSION DENIED")
            }
        }

        fun openDocument() {
            if (state.status == LoadingDocumentState.Status.SUCCESS) {
                Log.d("FileCard", "PERMISSION GRANTED")
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.setDataAndType(Uri.parse(state.msg), "application/pdf")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(context, intent, null)
                } catch (e: ActivityNotFoundException) {

                }
            }
        }

        val launcherRead = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                openDocument()
            } else {
                // Permission Denied: Do something
                Log.d("FileCard", "PERMISSION DENIED")
            }
        }

        AppTheme {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
                    .background(Color.Transparent)
                    .clickable { },
                shape = CardShape,
                elevation = 3.dp,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {


                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(8.dp)
                                .width(40.dp)
                                .height(40.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            ConstraintLayout(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.secondaryContainer)
                            ) {
                                val (
                                    icon, progress,
                                ) = createRefs()
                                if (state.status == LoadingDocumentState.Status.RUNNING) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .constrainAs(progress) {
                                                top.linkTo(parent.top, 4.dp)
                                                end.linkTo(parent.end, 4.dp)
                                                bottom.linkTo(parent.bottom, 4.dp)
                                            },
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    if (state.status == LoadingDocumentState.Status.IDLE || state.status == LoadingDocumentState.Status.FAILED) {
                                        if (state.status == LoadingDocumentState.Status.FAILED) {
                                            viewModel.changeDocumentList(
                                                url,
                                                LoadingDocumentState.IDLE
                                            )
                                            Toast.makeText(
                                                LocalContext.current.applicationContext,
                                                state.msg ?: "Error",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        Icon(
                                            modifier = Modifier
                                                .height(46.dp)
                                                .width(46.dp)
                                                .clickable(
                                                    onClick = {
                                                        when (PackageManager.PERMISSION_GRANTED) {

                                                            ContextCompat.checkSelfPermission(
                                                                context,
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                            ) -> {
                                                                viewModel.downloadDocument(
                                                                    url,
                                                                    text
                                                                )
                                                                Log.d(
                                                                    "ExampleScreen",
                                                                    "Code requires permission"
                                                                )
                                                            }
                                                            else -> {
                                                                // Asking for permission
                                                                launcherWrite.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                            }
                                                        }

                                                    }
                                                )
                                                .constrainAs(icon) {
                                                    top.linkTo(parent.top, 4.dp)
                                                    end.linkTo(parent.end, 4.dp)
                                                    bottom.linkTo(parent.bottom, 4.dp)
                                                },
                                            tint = MaterialTheme.colorScheme.primary,
                                            painter = painterResource(R.drawable.download_file),
                                            contentDescription = null
                                        )
                                    }
                                    if (state.status == LoadingDocumentState.Status.SUCCESS) {
                                        Icon(
                                            modifier = Modifier
                                                .height(46.dp)
                                                .width(46.dp)
                                                .clickable(
                                                    onClick = {
                                                        when (PackageManager.PERMISSION_GRANTED) {

                                                            ContextCompat.checkSelfPermission(
                                                                context,
                                                                Manifest.permission.READ_EXTERNAL_STORAGE
                                                            ) -> {
                                                                openDocument()
                                                                Log.d(
                                                                    "FileCard",
                                                                    "Code requires permission"
                                                                )
                                                            }
                                                            else -> {
                                                                launcherRead.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                                            }
                                                        }

                                                    }
                                                )
                                                .constrainAs(icon) {
                                                    top.linkTo(parent.top, 4.dp)
                                                    end.linkTo(parent.end, 4.dp)
                                                    bottom.linkTo(parent.bottom, 4.dp)
                                                },
                                            tint = MaterialTheme.colorScheme.primary,
                                            painter = painterResource(R.drawable.file__1_),
                                            contentDescription = null
                                        )
                                    }
                                }

                            }
                        }

                        Column(modifier = Modifier.padding(start = 8.dp)) {
                            Text(
                                text = text,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                    }
                    if(isOwner) {
                        IconButton(onClick = { openDialog.value = true }) {
                            Icon(
                                tint = MaterialTheme.colorScheme.primary,
                                imageVector = Icons.Default.Delete,
                                contentDescription = null
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
fun FileCardPreview() {
    AppTheme {
        val appState = rememberWayPlannerAppState()
        val owner = LocalViewModelStoreOwner.current
        owner?.let {

            val dayPointViewModel: DayPointViewModel = viewModel(
                it,
                "DayPointViewModel",
                DayPointViewModelFactory(
                    LocalContext.current.applicationContext
                            as Application, appState
                )
            )

        }

    }
}