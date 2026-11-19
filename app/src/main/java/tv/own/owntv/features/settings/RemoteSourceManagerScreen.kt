package tv.own.owntv.features.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import tv.own.owntv.R
import tv.own.owntv.core.companion.CompanionLink
import tv.own.owntv.core.companion.CompanionPayload
import tv.own.owntv.core.companion.CompanionServerState
import tv.own.owntv.core.companion.CompanionSourceAction
import tv.own.owntv.ui.components.OwnTVButton
import tv.own.owntv.ui.components.OwnTVButtonStyle
import tv.own.owntv.ui.components.companionLockedText
import tv.own.owntv.ui.components.displayText
import tv.own.owntv.ui.components.roundedPanel
import tv.own.owntv.ui.theme.OwnTVTheme

/** TV half of the PIN-protected source manager served to a browser on the same LAN. */
@Composable
fun RemoteSourceManagerScreen(
    state: CompanionServerState,
    payloads: Flow<CompanionPayload>,
    sourceActions: Flow<CompanionSourceAction>,
    onStartListener: (port: Int) -> Unit,
    onStopListener: () -> Unit,
    onPayloadReceived: (CompanionPayload) -> Unit,
    onSourceAction: (CompanionSourceAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OwnTVTheme.colors
    val actionFocus = remember { FocusRequester() }

    // Opening this explicit screen is the user's consent to make the manager available on the LAN.
    LaunchedEffect(Unit) { onStartListener(CompanionLink.DEFAULT_PORT) }
    LaunchedEffect(state::class) { runCatching { actionFocus.requestFocus() } }
    LaunchedEffect(payloads) { onPayloadReceived(payloads.first()) }
    LaunchedEffect(sourceActions) { sourceActions.collect(onSourceAction) }
    DisposableEffect(Unit) { onDispose(onStopListener) }

    Box(modifier.fillMaxSize().roundedPanel()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 48.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 640.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.companion_source_manager_tv_title), style = MaterialTheme.typography.headlineLarge, color = colors.onSurface)
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.companion_source_manager_tv_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(20.dp))

                when (state) {
                    CompanionServerState.Idle, CompanionServerState.Starting -> {
                        Text(stringResource(R.string.setup_opening), color = colors.onSurfaceVariant)
                    }
                    is CompanionServerState.Listening -> {
                        Text(stringResource(R.string.setup_enter_pin_browser), style = MaterialTheme.typography.bodyMedium, color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(state.pin, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = colors.primary, letterSpacing = 8.sp)
                        Spacer(Modifier.height(14.dp))
                        state.qr?.let { qr ->
                            Image(
                                bitmap = qr.asImageBitmap(),
                                contentDescription = stringResource(R.string.common_qr_code_companion_url),
                                modifier = Modifier.size(188.dp).clip(RoundedCornerShape(14.dp)).background(Color.White).padding(9.dp),
                                contentScale = ContentScale.Fit,
                            )
                            Spacer(Modifier.height(12.dp))
                        }
                        Text(stringResource(R.string.setup_open_url), style = MaterialTheme.typography.bodySmall, color = colors.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        state.urls.forEach { url ->
                            Text(url, style = MaterialTheme.typography.titleMedium, color = colors.onSurface, textAlign = TextAlign.Center)
                        }
                    }
                    is CompanionServerState.Failed -> {
                        Text(state.failure.displayText(), style = MaterialTheme.typography.bodyMedium, color = Color(0xFFEF4444), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(20.dp))
                        OwnTVButton(stringResource(R.string.setup_try_again), onClick = { onStartListener(CompanionLink.DEFAULT_PORT) }, modifier = Modifier.focusRequester(actionFocus))
                    }
                    CompanionServerState.Locked -> {
                        Text(companionLockedText(), style = MaterialTheme.typography.bodyMedium, color = Color(0xFFEF4444), textAlign = TextAlign.Center)
                        Spacer(Modifier.height(20.dp))
                        OwnTVButton(stringResource(R.string.setup_start_again_new_pin), onClick = { onStartListener(CompanionLink.DEFAULT_PORT) }, modifier = Modifier.focusRequester(actionFocus))
                    }
                }
                Spacer(Modifier.height(20.dp))
                OwnTVButton(stringResource(R.string.common_back), onClick = onBack, style = OwnTVButtonStyle.SECONDARY, modifier = Modifier.focusRequester(actionFocus))
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
