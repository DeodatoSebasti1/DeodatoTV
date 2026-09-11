package tv.own.owntv.features.live

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.core.model.MediaType
import tv.own.owntv.ui.components.FocusableSurface
import tv.own.owntv.ui.components.OwnTVButton
import tv.own.owntv.ui.components.OwnTVButtonStyle
import tv.own.owntv.ui.components.dialogPanel
import tv.own.owntv.ui.components.modalScrim
import tv.own.owntv.ui.components.trapAllFocusExit
import tv.own.owntv.ui.theme.OwnTVTheme

@Composable
fun M3uCategoryOverrideDialog(
    info: M3uCategoryOverrideInfo,
    onApply: (MediaType?) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = OwnTVTheme.colors
    var pendingTarget by remember { mutableStateOf<MediaType?>(null) }
    var confirmingAuto by remember { mutableStateOf(false) }
    val firstActionFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { firstActionFocus.requestFocus() } }
    BackHandler { if (pendingTarget != null || confirmingAuto) { pendingTarget = null; confirmingAuto = false } else onDismiss() }

    val target = pendingTarget
    Box(
        modifier = Modifier
            .fillMaxSize()
            .modalScrim()
            .trapAllFocusExit()
            .focusGroup(),
        contentAlignment = Alignment.Center,
    ) {
        Column(Modifier.dialogPanel(width = 560.dp, padding = 28.dp)) {
            Text(
                text = stringResource(R.string.content_m3u_category_classification_title),
                style = MaterialTheme.typography.titleLarge,
                color = colors.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = info.categoryName,
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
            Spacer(Modifier.height(18.dp))
            when {
                target != null -> {
                    Text(
                        text = pluralStringResource(
                            R.plurals.content_m3u_category_move_confirm,
                            info.itemCount,
                            info.itemCount,
                            target.displayName(),
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurface,
                    )
                    Spacer(Modifier.height(22.dp))
                    ConfirmButtons(
                        onCancel = { pendingTarget = null },
                        onConfirm = { onApply(target) },
                    )
                }
                confirmingAuto -> {
                    Text(
                        text = stringResource(R.string.content_m3u_category_auto_confirm),
                        style = MaterialTheme.typography.bodyLarge,
                        color = colors.onSurface,
                    )
                    Spacer(Modifier.height(22.dp))
                    ConfirmButtons(
                        onCancel = { confirmingAuto = false },
                        onConfirm = { onApply(null) },
                    )
                }
                else -> {
                    M3uCategoryAction(
                        label = stringResource(R.string.content_m3u_category_move_live),
                        modifier = Modifier.focusRequester(firstActionFocus),
                    ) { pendingTarget = MediaType.LIVE }
                    M3uCategoryAction(stringResource(R.string.content_m3u_category_move_movies)) {
                        pendingTarget = MediaType.MOVIE
                    }
                    M3uCategoryAction(stringResource(R.string.content_m3u_category_move_series)) {
                        pendingTarget = MediaType.SERIES
                    }
                    M3uCategoryAction(stringResource(R.string.content_m3u_category_use_auto)) {
                        confirmingAuto = true
                    }
                }
            }
        }
    }
}

@Composable
private fun M3uCategoryAction(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = OwnTVTheme.colors
    FocusableSurface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurface,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun ConfirmButtons(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        OwnTVButton(
            label = stringResource(R.string.common_cancel),
            onClick = onCancel,
            style = OwnTVButtonStyle.SECONDARY,
        )
        OwnTVButton(
            label = stringResource(R.string.common_ok),
            onClick = onConfirm,
            style = OwnTVButtonStyle.PRIMARY,
        )
    }
}

@Composable
private fun MediaType.displayName(): String = when (this) {
    MediaType.LIVE -> stringResource(R.string.common_nav_live_tv)
    MediaType.MOVIE -> stringResource(R.string.common_nav_movies)
    MediaType.SERIES, MediaType.EPISODE -> stringResource(R.string.common_nav_series)
}
