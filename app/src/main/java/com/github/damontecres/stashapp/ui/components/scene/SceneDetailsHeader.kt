package com.github.damontecres.stashapp.ui.components.scene

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import com.github.damontecres.stashapp.R
import com.github.damontecres.stashapp.api.fragment.FullSceneData
import com.github.damontecres.stashapp.api.fragment.StudioData
import com.github.damontecres.stashapp.playback.PlaybackMode
import com.github.damontecres.stashapp.ui.ComposeUiConfig
import com.github.damontecres.stashapp.ui.compat.isNotTvDevice
import com.github.damontecres.stashapp.ui.compat.isTvDevice
import com.github.damontecres.stashapp.ui.components.DotSeparatedRow
import com.github.damontecres.stashapp.ui.components.ItemOnClicker
import com.github.damontecres.stashapp.ui.components.LongClicker
import com.github.damontecres.stashapp.ui.components.Rating100
import com.github.damontecres.stashapp.ui.components.TitleValueText
import com.github.damontecres.stashapp.ui.components.ratingBarHeight
import com.github.damontecres.stashapp.ui.util.ifElse
import com.github.damontecres.stashapp.ui.util.playOnClickSound
import com.github.damontecres.stashapp.ui.util.playSoundOnFocus
import com.github.damontecres.stashapp.util.StashCoroutineExceptionHandler
import com.github.damontecres.stashapp.util.isNotNullOrBlank
import com.github.damontecres.stashapp.util.listOfNotNullOrBlank
import com.github.damontecres.stashapp.util.resolutionName
import com.github.damontecres.stashapp.util.resume_position
import com.github.damontecres.stashapp.util.titleOrFilename
import com.github.damontecres.stashapp.views.durationToString
import kotlinx.coroutines.launch

@Composable
fun SceneDetailsHeader(
    scene: FullSceneData,
    studio: StudioData?,
    rating100: Int,
    oCount: Int,
    uiConfig: ComposeUiConfig,
    itemOnClick: ItemOnClicker<Any>,
    playOnClick: (position: Long, mode: PlaybackMode) -> Unit,
    editOnClick: () -> Unit,
    moreOnClick: () -> Unit,
    detailsOnClick: () -> Unit,
    oCounterOnClick: () -> Unit,
    oCounterOnLongClick: () -> Unit,
    onRatingChange: (Int) -> Unit,
    focusRequester: FocusRequester,
    bringIntoViewRequester: BringIntoViewRequester,
    removeLongClicker: LongClicker<Any>,
    showEditButton: Boolean,
    alwaysStartFromBeginning: Boolean,
    modifier: Modifier = Modifier,
    showRatingBar: Boolean = true,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isNotTvDevice = isNotTvDevice

    if (isTvDevice) {
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .height(460.dp)
                    .bringIntoViewRequester(bringIntoViewRequester),
        ) {
            if (scene.paths.screenshot.isNotNullOrBlank()) {
                val gradientColor = MaterialTheme.colorScheme.background
                AsyncImage(
                    model = scene.paths.screenshot,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopEnd,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, gradientColor),
                                        startY = 500f,
                                    ),
                                )
                                drawRect(
                                    Brush.horizontalGradient(
                                        colors = listOf(gradientColor, Color.Transparent),
                                        endX = 400f,
                                        startX = 100f,
                                    ),
                                )
                            },
                )
            }
            Column(modifier = Modifier.fillMaxWidth(0.8f)) {
                Spacer(modifier = Modifier.height(60.dp))
                SceneDetailsHeaderInfo(
                    scene = scene,
                    studio = studio,
                    rating100 = rating100,
                    uiConfig = uiConfig,
                    itemOnClick = itemOnClick,
                    detailsOnClick = detailsOnClick,
                    onRatingChange = onRatingChange,
                    bringIntoViewRequester = bringIntoViewRequester,
                    removeLongClicker = removeLongClicker,
                    modifier = Modifier.padding(start = 16.dp),
                    showRatingBar = showRatingBar,
                )
                // Playback controls
                PlayButtons(
                    resumePosition = scene.resume_position ?: 0,
                    oCount = oCount,
                    playOnClick = playOnClick,
                    editOnClick = editOnClick,
                    moreOnClick = moreOnClick,
                    oCounterOnClick = oCounterOnClick,
                    oCounterOnLongClick = oCounterOnLongClick,
                    focusRequester = focusRequester,
                    buttonOnFocusChanged = {
                        if (it.isFocused) {
                            scope.launch(StashCoroutineExceptionHandler()) { bringIntoViewRequester.bringIntoView() }
                        }
                    },
                    alwaysStartFromBeginning = alwaysStartFromBeginning,
                    showEditButton = showEditButton,
                    sfwMode = uiConfig.sfwMode,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) {
            SceneDetailsHeaderInfo(
                scene = scene,
                studio = studio,
                rating100 = rating100,
                uiConfig = uiConfig,
                itemOnClick = itemOnClick,
                detailsOnClick = detailsOnClick,
                onRatingChange = onRatingChange,
                bringIntoViewRequester = bringIntoViewRequester,
                removeLongClicker = removeLongClicker,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                showRatingBar = showRatingBar,
            )
            MobileActionRow(
                sfwMode = uiConfig.sfwMode,
                oCount = oCount,
                oCounterOnClick = oCounterOnClick,
                oCounterOnLongClick = oCounterOnLongClick,
                editOnClick = editOnClick,
                moreOnClick = moreOnClick,
                playOnClick = {
                    playOnClick.invoke(
                        scene.resume_position ?: 0,
                        PlaybackMode.Choose
                    )
                },
                showEditButton = showEditButton,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
fun MobileActionRow(
    sfwMode: Boolean,
    oCount: Int,
    oCounterOnClick: () -> Unit,
    oCounterOnLongClick: () -> Unit,
    editOnClick: () -> Unit,
    moreOnClick: () -> Unit,
    playOnClick: () -> Unit,
    showEditButton: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Play button for mobile
        com.github.damontecres.stashapp.ui.compat.Button(
            onClick = playOnClick,
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.play),
                style = MaterialTheme.typography.titleSmall,
            )
        }

        // O-Counter
        com.github.damontecres.stashapp.ui.components.OCounterButton(
            sfwMode = sfwMode,
            oCount = oCount,
            onClick = oCounterOnClick,
            onLongClick = oCounterOnLongClick,
            enabled = showEditButton,
        )

        if (showEditButton) {
            com.github.damontecres.stashapp.ui.components.EditButton(
                onClick = editOnClick,
            )
        }

        com.github.damontecres.stashapp.ui.compat.Button(
            onClick = moreOnClick,
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
            )
            androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.more),
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
fun SceneDetailsHeaderInfo(
    scene: FullSceneData,
    studio: StudioData?,
    rating100: Int,
    uiConfig: ComposeUiConfig,
    itemOnClick: ItemOnClicker<Any>,
    detailsOnClick: () -> Unit,
    onRatingChange: (Int) -> Unit,
    bringIntoViewRequester: BringIntoViewRequester,
    removeLongClicker: LongClicker<Any>,
    modifier: Modifier = Modifier,
    showRatingBar: Boolean = true,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isNotTvDevice = isNotTvDevice
    Column(
        modifier = modifier,
    ) {
        // Title
        Text(
            text = scene.titleOrFilename ?: "",
            color = MaterialTheme.colorScheme.onSurface,
            style =
                (if (isNotTvDevice) MaterialTheme.typography.displaySmall else MaterialTheme.typography.displayMedium).copy(
                    shadow =
                        Shadow(
                            color = Color.DarkGray,
                            offset = Offset(5f, 2f),
                            blurRadius = 2f,
                        ),
                ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Column(
            modifier = Modifier.alpha(0.75f),
        ) {
            // Rating
            if (showRatingBar) {
                Rating100(
                    rating100 = rating100,
                    uiConfig = uiConfig,
                    onRatingChange = onRatingChange,
                    enabled = true,
                    modifier =
                        Modifier
                            .height(ratingBarHeight)
                            .padding(start = 12.dp),
                )
            }
            // Quick info
            val file = scene.files.firstOrNull()?.videoFile
            DotSeparatedRow(
                modifier = Modifier.padding(top = 6.dp, start = 8.dp),
                textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                texts =
                    listOfNotNullOrBlank(
                        scene.date,
                        file?.let { durationToString(it.duration) },
                        file?.resolutionName(),
                    ),
            )
            // Description
            if (scene.details.isNotNullOrBlank()) {
                val interactionSource = remember { MutableInteractionSource() }
                val isFocused = interactionSource.collectIsFocusedAsState().value
                val bgColor =
                    if (isFocused) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = .75f)
                    } else {
                        Color.Unspecified
                    }
                Box(
                    modifier =
                        Modifier
                            .background(bgColor, shape = RoundedCornerShape(8.dp))
                            .onFocusChanged {
                                if (it.isFocused) {
                                    scope.launch(StashCoroutineExceptionHandler()) { bringIntoViewRequester.bringIntoView() }
                                }
                            }.playSoundOnFocus(uiConfig.playSoundOnFocus)
                            .clickable(
                                enabled = true,
                                interactionSource = interactionSource,
                                indication = LocalIndication.current,
                            ) {
                                if (uiConfig.playSoundOnFocus) playOnClickSound(context)
                                detailsOnClick.invoke()
                            },
                ) {
                    Text(
                        text = scene.details,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(8.dp),
                    )
                }
            }
            // Key-Values
            Column(
                modifier =
                    Modifier
                        .padding(top = 8.dp, start = 16.dp)
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // Row 1: Studio and Scene Code
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    if (studio != null) {
                        TitleValueText(
                            stringResource(R.string.stashapp_studio),
                            studio.name,
                            playSoundOnFocus = uiConfig.playSoundOnFocus,
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .onFocusChanged {
                                        if (it.isFocused) {
                                            scope.launch(StashCoroutineExceptionHandler()) { bringIntoViewRequester.bringIntoView() }
                                        }
                                    },
                            onClick = {
                                itemOnClick.onClick(studio, null)
                            },
                            onLongClick = {
                                removeLongClicker.longClick(studio, null)
                            },
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    if (scene.code.isNotNullOrBlank()) {
                        TitleValueText(
                            stringResource(R.string.stashapp_scene_code),
                            scene.code,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                // Row 2: Director and Play Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                    if (scene.director.isNotNullOrBlank()) {
                        TitleValueText(
                            stringResource(R.string.stashapp_director),
                            scene.director,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    TitleValueText(
                        stringResource(R.string.stashapp_play_count),
                        (scene.play_count ?: 0).toString(),
                        modifier = Modifier.weight(1f),
                    )
                }
                // Row 3: Play Duration (separate or as needed)
                TitleValueText(
                    stringResource(R.string.stashapp_play_duration),
                    durationToString(scene.play_duration ?: 0.0),
                )
            }
        }
    }
}
