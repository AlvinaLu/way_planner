package cz.cvut.fel.lushnalv.ui.theme.authorization

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import cz.cvut.fel.lushnalv.R
import cz.cvut.fel.lushnalv.ui.theme.AppTheme
import cz.cvut.fel.lushnalv.ui.theme.MainSheetShape

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    AuthContent(viewModel)
}

@Composable
fun AuthContent(viewModel: AuthViewModel) {
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp.dp

    val loadingState by viewModel.loadingState.collectAsState()


    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                viewModel.signWithCredential(account)
            } catch (e: ApiException) {
                Log.e("GOOGLE_AUTH", e.message, e)
            }
        }


    AppTheme {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            if(screenHeight.value > 600F){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier.padding(start = 8.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.onPrimary)
                            .width(100.dp)
                            .height(100.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        androidx.compose.material3.Icon(
                            modifier = Modifier
                                .width(100.dp)
                                .height(100.dp),
                            tint = MaterialTheme.colorScheme.primary,
                            painter = painterResource(R.drawable.logo), contentDescription = null
                        )
                    }
                }
            }
            }else{}
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (screenHeight.value < 600F) {
                            10.dp
                        } else {
                            200.dp
                        }
                    ),
                shape = MainSheetShape,
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 3.dp,
                tonalElevation = 3.dp
            ) {

                if (loadingState.status == LoadingState.Status.RUNNING) {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .testTag("login_loading"), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    mainContent(viewModel, launcher)
                    if (loadingState.status == LoadingState.Status.FAILED) {
                        viewModel.changeStatus(LoadingState.IDLE)
                        Toast.makeText(
                            LocalContext.current,
                            loadingState.msg ?: stringResource(R.string.error),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            }
        }
    }
}

@Composable
fun mainContent(
    viewModel: AuthViewModel,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>
) {

    val selectedIndex by viewModel.tabState.collectAsState()
    when (selectedIndex) {
        TabAuth.LOGIN -> Login(viewModel = viewModel, launcher = launcher)
        TabAuth.CREATE -> CreateAccount(viewModel = viewModel, launcher = launcher)
        TabAuth.FORGOT -> ForgotPassword(viewModel = viewModel)
        TabAuth.UPDATE -> UpdatePassword(viewModel = viewModel)

        }
    }


@Composable
fun CreateAccount(
    viewModel: AuthViewModel,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    val emailFocusRequest = remember { FocusRequester() }
    val passwordFocusRequest = remember { FocusRequester() }
    val repeatPasswordFocusRequest = remember { FocusRequester() }
    val nameState by remember { mutableStateOf(NameState()) }
    val emailState by remember { mutableStateOf(EmailState()) }
    val passwordState = remember { PasswordState() }

    fun repeatPasswordChange(password: String): Boolean {
        return !(password != passwordState.text)
    }

    val repeatPasswordState = remember { RepeatPasswordState(::repeatPasswordChange) }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            androidx.compose.material3.IconButton(onClick = { viewModel.changeTabState(TabAuth.LOGIN) }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .weight(3f)
                    .padding(top = 8.dp),
                text = stringResource(id = R.string.create_account),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Name(nameState, onImeAction = {emailFocusRequest.requestFocus()})
        Spacer(modifier = Modifier.height(4.dp))
        Email(emailState, onImeAction = { passwordFocusRequest.requestFocus() }, modifier = Modifier.focusRequester(emailFocusRequest))
        Spacer(modifier = Modifier.height(4.dp))
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            onImeAction = { },
            modifier = Modifier.focusRequester(passwordFocusRequest)
        )
        Spacer(modifier = Modifier.height(4.dp))
        RepeatPassword(
            label = stringResource(id = R.string.repeat_password),
            passwordState = repeatPasswordState,
            imeAction = ImeAction.Next,
            onImeAction = { },
            modifier = Modifier.focusRequester(repeatPasswordFocusRequest)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            androidx.compose.material3.ElevatedButton(
                onClick = {
                    viewModel.signUpInWithEmailAndPassword(
                        name = nameState.text,
                        email = emailState.text,
                        password = passwordState.text
                    )
                },
                enabled = nameState.isValid && emailState.isValid && passwordState.isValid && (passwordState.text == repeatPasswordState.text),
                colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.create),
                        color = MaterialTheme.colorScheme.background,
                        textAlign = TextAlign.Center,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(R.string.or),
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall
            )
        }
        val context = LocalContext.current
        val token = stringResource(R.string.default_web_client_auth_id)
        Spacer(modifier = Modifier.height(16.dp))
        GoogleButton(
            buttonText = stringResource(R.string.continue_with_google),
            backgroundColor = MaterialTheme.colorScheme.background,
            fontColor = MaterialTheme.colorScheme.outline,
            onClick = {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            }
        )
    }
}

@Composable
fun UpdatePassword(
    viewModel: AuthViewModel,
) {
    val passwordFocusRequest = remember { FocusRequester() }
    val repeatPasswordFocusRequest = remember { FocusRequester() }
    val codeFocusRequest = remember { FocusRequester() }
    val emailState = remember { EmailState() }
    val codeState = remember { CodeState() }
    val passwordState = remember { PasswordState() }

    fun repeatPasswordChange(password: String): Boolean {
        return !(password != passwordState.text)
    }

    val repeatPasswordState = remember { RepeatPasswordState(::repeatPasswordChange) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            androidx.compose.material3.IconButton(onClick = { viewModel.changeTabState(TabAuth.LOGIN) }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .weight(3f)
                    .padding(top = 8.dp),
                text = stringResource(R.string.update_password),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Email(
            emailState,
            imeAction = ImeAction.Next,
            onImeAction = { codeFocusRequest.requestFocus() })
        Spacer(modifier = Modifier.height(4.dp))
        Code(
            codeState = codeState,
            imeAction = ImeAction.Next,
            onImeAction = { passwordFocusRequest.requestFocus() },
            modifier = Modifier.focusRequester(codeFocusRequest)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            onImeAction = { repeatPasswordFocusRequest.requestFocus() },
            modifier = Modifier.focusRequester(passwordFocusRequest)
        )
        Spacer(modifier = Modifier.height(4.dp))
        RepeatPassword(
            label = stringResource(id = R.string.repeat_password),
            passwordState = repeatPasswordState,
            imeAction = ImeAction.Next,
            onImeAction = { },
            modifier = Modifier.focusRequester(repeatPasswordFocusRequest)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            androidx.compose.material3.ElevatedButton(
                onClick = {
                    viewModel.updatePassword(emailState.text, passwordState.text, codeState.text)
                },
                enabled = emailState.isValid && passwordState.isValid && (passwordState.text == repeatPasswordState.text) && codeState.isValid,
                colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.update),
                        color = MaterialTheme.colorScheme.background,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun Login(
    viewModel: AuthViewModel,
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
) {
    val passwordFocusRequest = remember { FocusRequester() }
    val emailState by remember { mutableStateOf(EmailState())}
    val passwordState = remember { PasswordState() }

    Column(
        modifier = Modifier .testTag("login_screen_tag")
            .fillMaxSize()
            .padding(top = 25.dp, start = 8.dp, end = 8.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.weight(3f),
                text = stringResource(R.string.log_in),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Email(emailState, onImeAction = { passwordFocusRequest.requestFocus() })
        Spacer(modifier = Modifier.height(4.dp))
        Password(
            label = stringResource(id = R.string.password),
            passwordState = passwordState,
            imeAction = ImeAction.Next,
            onImeAction = { },
            modifier = Modifier.focusRequester(passwordFocusRequest)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { viewModel.changeTabState(TabAuth.FORGOT) },
                text = stringResource(R.string.forgot_password),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            androidx.compose.material3.ElevatedButton(
                onClick = {
                    viewModel.changeTabState(TabAuth.CREATE)
                },
                modifier = Modifier.weight(1f),
            ) {
                androidx.compose.material3.Text(
                    text = stringResource(R.string.new_user),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            androidx.compose.material3.ElevatedButton(
                onClick = {
                    viewModel.logInWithEmailAndPassword(
                        email = emailState.text,
                        password = passwordState.text
                    )
                },
                enabled = emailState.isValid && passwordState.isValid,
                colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(1f).testTag("login_button_tag"),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterStart)
                    ) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(id = R.string.login),
                        color = MaterialTheme.colorScheme.background,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = stringResource(id = R.string.or),
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
        }
        val context = LocalContext.current
        val token = stringResource(R.string.default_web_client_auth_id)
        Spacer(modifier = Modifier.height(16.dp))
        GoogleButton(
            buttonText = stringResource(id = R.string.continue_with_google),
            backgroundColor = MaterialTheme.colorScheme.background,
            fontColor = MaterialTheme.colorScheme.outline,
            onClick = {
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()

                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                launcher.launch(googleSignInClient.signInIntent)
            }
        )
    }
}

@Composable
fun ForgotPassword(
    viewModel: AuthViewModel,) {
    val passwordFocusRequest = remember { FocusRequester() }
    val emailState by remember { mutableStateOf(EmailState())}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp, start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            androidx.compose.material3.IconButton(onClick = { viewModel.changeTabState(TabAuth.LOGIN) }) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .weight(3f)
                    .padding(top = 8.dp),
                text = stringResource(id = R.string.forgot_password),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Email(emailState, onImeAction = { passwordFocusRequest.requestFocus() })
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            androidx.compose.material3.ElevatedButton(
                onClick = {
                    viewModel.resetPassword(emailState.text)
                },
                enabled = emailState.isValid,
                colors = ButtonDefaults.elevatedButtonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.weight(1f),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = stringResource(R.string.send_code),
                        color = MaterialTheme.colorScheme.background,
                        textAlign = TextAlign.Center,
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

}

@Composable
fun Name(
    nameState: TextFieldState = remember { NameState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = nameState.text,
        onValueChange = { nameState.text = it },
        label = {
                androidx.compose.material.Text(
                    text = stringResource(R.string.name)
                )
        },
        leadingIcon = {
            Icon(
                tint = MaterialTheme.colorScheme.outline,
                imageVector = Icons.Filled.Person,
                contentDescription = null
            )
        },
        isError = nameState.showErrors(),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                nameState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    nameState.enableShowErrors()
                }
            },
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            disabledTextColor = MaterialTheme.colorScheme.outline,
            placeholderColor = MaterialTheme.colorScheme.outline
        )
    )

    nameState.getError()?.let { error -> TextFieldError(textError = error) }
}

@Composable
fun Email(
    emailState: TextFieldState = remember { EmailState() },
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = emailState.text,
        onValueChange = { emailState.text = it },
        label = { androidx.compose.material.Text(
                    text = stringResource(R.string.email))
        },
        leadingIcon = {
            Icon(
                tint = MaterialTheme.colorScheme.outline,
                imageVector = Icons.Filled.Email,
                contentDescription = null
            )
        },
        isError = emailState.showErrors(),
        modifier = modifier
            .testTag("login_email_tag")
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                emailState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    emailState.enableShowErrors()
                }
            },
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Email
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            disabledTextColor = MaterialTheme.colorScheme.outline,
            placeholderColor = MaterialTheme.colorScheme.outline
        ),
    )

    emailState.getError()?.let { error -> TextFieldError(textError = error) }
}

@Composable
fun Code(
    codeState: TextFieldState = remember { CodeState() },
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = codeState.text,
        onValueChange = { codeState.text = it },
        label = {
                androidx.compose.material.Text(
                    text = stringResource(R.string.code)
                )
        },
        isError = codeState.showErrors(),
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                codeState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    codeState.enableShowErrors()
                }
            },
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            disabledTextColor = MaterialTheme.colorScheme.outline,
            placeholderColor = MaterialTheme.colorScheme.outline
        )
    )

    codeState.getError()?.let { error -> TextFieldError(textError = error) }
}


@Composable
fun Password(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {

    val showPassword = rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier
            .testTag("login_password_tag")
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                passwordState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    passwordState.enableShowErrors()
                }
            },
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        label = {
            androidx.compose.material.Text(
                text = stringResource(R.string.password)
            )
        },
        leadingIcon = {
            Icon(
                tint = MaterialTheme.colorScheme.outline,
                painter = painterResource(R.drawable.lock),
                contentDescription = null
            )
        },
        trailingIcon = {
            val image =
                if (showPassword.value) R.drawable.eye else R.drawable.eye_vizability_off
            val description =
                if (showPassword.value) "Hide password" else "Show password"

            IconButton(onClick = { showPassword.value = !showPassword.value }) {
                Icon(
                    tint = MaterialTheme.colorScheme.outline,
                    painter = painterResource(image),
                    contentDescription = description
                )
            }
        },
        isError = passwordState.showErrors(),
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            disabledTextColor = MaterialTheme.colorScheme.outline,
            placeholderColor = MaterialTheme.colorScheme.outline
        )
    )

    passwordState.getError()?.let { error -> TextFieldError(textError = error) }


}

@Composable
fun RepeatPassword(
    label: String,
    passwordState: TextFieldState,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeAction: () -> Unit = {},
) {

    val showPassword = rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        visualTransformation = if (showPassword.value) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                passwordState.onFocusChange(focusState.isFocused)
                if (!focusState.isFocused) {
                    passwordState.enableShowErrors()
                }
            },
        value = passwordState.text,
        onValueChange = {
            passwordState.text = it
            passwordState.enableShowErrors()
        },
        label = {
            androidx.compose.material.Text(
                text = label
            )
        },
        leadingIcon = {
            Icon(
                tint = MaterialTheme.colorScheme.outline,
                painter = painterResource(R.drawable.lock),
                contentDescription = null
            )
        },
        trailingIcon = {
            val image =
                if (showPassword.value) R.drawable.eye else R.drawable.eye_vizability_off
            val description =
                if (showPassword.value) "Hide password" else "Show password"

            IconButton(onClick = { showPassword.value = !showPassword.value }) {
                Icon(
                    tint = MaterialTheme.colorScheme.outline,
                    painter = painterResource(image),
                    contentDescription = description
                )
            }
        },
        isError = passwordState.showErrors(),
        singleLine = true,
        maxLines = 1,
        textStyle = MaterialTheme.typography.bodyMedium,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = imeAction,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onImeAction()
            }
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            textColor = MaterialTheme.colorScheme.onBackground,
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            cursorColor = MaterialTheme.colorScheme.tertiary,
            disabledTextColor = MaterialTheme.colorScheme.outline,
            placeholderColor = MaterialTheme.colorScheme.outline
        )
    )

    passwordState.getError()?.let { error -> TextFieldError(textError = error) }


}


@Composable
fun TextFieldError(textError: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(16.dp))
        androidx.compose.material.Text(
            text = textError,
            modifier = Modifier.fillMaxWidth(),
            style = LocalTextStyle.current.copy(color = androidx.compose.material.MaterialTheme.colors.error)
        )
    }
}

@Composable
fun GoogleButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    onClick: (isEnabled: Boolean) -> Unit = {},
    enable: Boolean = true,
    backgroundColor: Color,
    fontColor: Color,
) {
    androidx.compose.material3.OutlinedButton(
        onClick = { onClick(enable) },
        modifier = modifier
            .fillMaxWidth()
            .shadow(0.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.google),
                    modifier = Modifier
                        .height(24.dp)
                        .width(24.dp),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = buttonText,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium
            )
        }

    }
}

