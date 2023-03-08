package cz.cvut.fel.lushnalv.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.data.local.AppPreferences
import cz.cvut.fel.lushnalv.models.User
import cz.cvut.fel.lushnalv.ui.createNewTrip.NewFriendEmailField
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.createNewTrip.NewEmail


@Composable
fun CustomChipsField(
    listMember: List<Long>,
    listFriends: List<User>,
    listNewEmails: List<String>,
    checkedChange: (Long) -> Unit,
    label: @Composable() (() -> Unit)? = null,
    placeholder: @Composable() (() -> Unit)? = null,
    leadingIcon: @Composable() (() -> Unit)? = null,
    newEmail: NewEmail,
    onValueEmailChange: (String) ->  Unit,
    addNewEmail: () -> Unit,
    deleteEmail: (String) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val itemView = @Composable { text: String ->
        Text(
            text,
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.outline, shape = CircleShape)
                .padding(vertical = 3.dp, horizontal = 5.dp)
        )
    }

    val interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
    var expanded by remember { mutableStateOf(false) }

    fun changeExpanded(){
        expanded = !expanded
    }

    val appPreferences = AppPreferences.create(LocalContext.current.applicationContext)
    Box(
        modifier = Modifier
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.background)
            .focusable(true, interactionSource = interactionSource)
    ) {
        Box(
            modifier = Modifier
                .zIndex(1f)
                .offset(10.dp, -8.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = stringResource(R.string.add_friend_optional),
                style = androidx.compose.material.MaterialTheme.typography.caption,
                color = if(expanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.background,
            border = if (!expanded) BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline
            ) else BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(5.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth().background(MaterialTheme.colorScheme.background)
                    .padding(top = 8.dp, bottom = 4.dp, end = 4.dp, start = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                        ChipVerticalGrid(
                            spacing = 8.dp,
                            moreItemsView = {  },
                            modifier = Modifier.weight(1f)
                        ) {
                            val you  = listFriends.filter { user -> appPreferences.userDetails?.id == user.userId }.firstOrNull()
                            if(you!=null){
                                ChipForCreateTrip(user = you, onClick = checkedChange, showDelete = true)
                            }
                            var value = 6
                            if(screenHeight.value < 600F){
                                value = 2
                            }
                            listFriends.take(value).forEach { user ->
                                if (listMember.contains(user.userId) ) {
                                    if(appPreferences.userDetails?.id == user.userId){

                                    }else{
                                    ChipForCreateTrip(user = user, onClick = checkedChange)
                                    }
                                }
                            }
                            listNewEmails.take(value).forEach { it ->
                                    ChipForCreateTrip(user = User(Long.MAX_VALUE, it, it),  onClick = {}, onDelete = deleteEmail, true)
                            }

                        }
                    IconButton(
                        onClick = { expanded = !expanded }, modifier = Modifier
                            .weight(0.1f)
                            .padding(top = 8.dp)
                    ) {
                        if (expanded) {
                            Icon(
                                tint = MaterialTheme.colorScheme.outline,
                                imageVector = Icons.Filled.KeyboardArrowUp,
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                tint = MaterialTheme.colorScheme.outline,
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }

                    }
                }
            }
            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.background)
                    .fillMaxWidth(1.0f)
                    .fillMaxHeight(0.5f),
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
            ) {
                NewFriendEmailField(
                    modifier= Modifier,
                    email = newEmail.email,
                    onValueChange = onValueEmailChange,
                    addNewEmail = addNewEmail,
                    isValid = newEmail.valid,
                    isFocusedDirty = newEmail.emailFocusedDirty,
                    ::changeExpanded
                )
                listFriends.forEachIndexed { index, selectionOption ->
                    if (appPreferences.userDetails?.id != selectionOption.userId) {
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            content = {
                                CheckBoxUser(
                                    user = selectionOption,
                                    listMember = listMember,
                                    checkedChange,
                                )
                            },
                            onClick = {
                                expanded = false
                            },
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
fun CustomChipsFieldPreview() {
    AppTheme {

    }
}