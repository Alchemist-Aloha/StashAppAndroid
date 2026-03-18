package com.github.damontecres.stashapp.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.MaterialTheme
import com.github.damontecres.stashapp.R
import com.github.damontecres.stashapp.data.DataType
import com.github.damontecres.stashapp.navigation.Destination
import com.github.damontecres.stashapp.navigation.NavigationManagerCompose
import com.github.damontecres.stashapp.ui.ComposeUiConfig
import com.github.damontecres.stashapp.ui.FontAwesome
import com.github.damontecres.stashapp.ui.components.ItemOnClicker
import com.github.damontecres.stashapp.ui.components.LongClicker
import com.github.damontecres.stashapp.ui.util.ScreenSize
import com.github.damontecres.stashapp.ui.util.screenSize
import com.github.damontecres.stashapp.util.StashServer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun NavScaffold(
    server: StashServer,
    navigationManager: NavigationManagerCompose,
    composeUiConfig: ComposeUiConfig,
    destination: Destination,
    selectedScreen: DrawerPage?,
    pages: List<DrawerPage>,
    itemOnClick: ItemOnClicker<Any>,
    longClicker: LongClicker<Any>,
    onSelectScreen: (DrawerPage) -> Unit,
    onChangeTheme: (String?) -> Unit,
    onSwitchServer: (StashServer) -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by remember { mutableStateOf<AnnotatedString?>(null) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomNavItems =
        remember(pages) {
            pages
                .filter { page ->
                    page is DrawerPage.HomePage ||
                        page is DrawerPage.SearchPage ||
                        (page is DrawerPage.DataTypePage && (page.dataType == DataType.SCENE || page.dataType == DataType.IMAGE))
                }.sortedBy {
                    when (it) {
                        is DrawerPage.HomePage -> 0
                        is DrawerPage.DataTypePage -> if (it.dataType == DataType.SCENE) 1 else 2
                        is DrawerPage.SearchPage -> 3
                        else -> 4
                    }
                }
        }

    val isBottomNavItem =
        remember(bottomNavItems, selectedScreen) {
            bottomNavItems.contains(selectedScreen)
        }

    val titleStyle =
        when (screenSize()) {
            ScreenSize.COMPACT -> androidx.compose.material3.MaterialTheme.typography.headlineSmall
            ScreenSize.MEDIUM -> androidx.compose.material3.MaterialTheme.typography.headlineMedium
            ScreenSize.EXPANDED -> androidx.compose.material3.MaterialTheme.typography.headlineLarge
        }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    pages.forEach { page ->
                        NavigationDrawerItem(
                            label = { Text(stringResource(page.name)) },
                            icon = {
                                if (page !is DrawerPage.SettingPage) {
                                    Text(
                                        text = stringResource(page.iconString),
                                        fontFamily = FontAwesome,
                                        fontSize = 20.sp,
                                    )
                                } else {
                                    Icon(
                                        painter = painterResource(id = R.drawable.vector_settings),
                                        contentDescription = null,
                                    )
                                }
                            },
                            selected = selectedScreen == page,
                            onClick = {
                                scope.launch { drawerState.close() }
                                onSelectScreen(page)
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            modifier = modifier,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        val titleText =
                            when (selectedScreen) {
                                is DrawerPage.HomePage,
                                is DrawerPage.SearchPage,
                                -> AnnotatedString(stringResource(selectedScreen.name))

                                else -> title
                            }
                        titleText?.let {
                            val updatedTitle =
                                if (destination is Destination.Item && destination.dataType == DataType.PERFORMER) {
                                    // TODO hack to adjust font sizes
                                    val newStyles =
                                        if (it.spanStyles.size == 2) {
                                            listOf(
                                                it.spanStyles[0].let {
                                                    it.copy(item = it.item.copy(fontSize = titleStyle.fontSize))
                                                },
                                                it.spanStyles[1].let {
                                                    it.copy(item = it.item.copy(fontSize = titleStyle.fontSize * .75f))
                                                },
                                            )
                                        } else if (it.spanStyles.size == 1) {
                                            listOf(
                                                it.spanStyles[0].let {
                                                    it.copy(item = it.item.copy(fontSize = titleStyle.fontSize))
                                                },
                                            )
                                        } else {
                                            listOf()
                                        }

                                    AnnotatedString(it.text, newStyles, it.paragraphStyles)
                                } else {
                                    it
                                }
                            Text(
                                text = updatedTitle,
                                style = titleStyle,
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (!isBottomNavItem) {
                                navigationManager.goBack()
                            } else {
                                scope.launch { drawerState.open() }
                            }
                        }) {
                            Icon(
                                imageVector =
                                    if (!isBottomNavItem) {
                                        Icons.AutoMirrored.Filled.ArrowBack
                                    } else {
                                        Icons.Default.Menu
                                    },
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            navigationManager.navigate(
                                Destination.ManageServers(false),
                            )
                        }) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    },
                )
            },
            bottomBar = {
                if (bottomNavItems.isNotEmpty()) {
                    NavigationBar {
                        bottomNavItems.forEach { page ->
                            NavigationBarItem(
                                selected = selectedScreen == page,
                                onClick = { onSelectScreen(page) },
                                icon = {
                                    if (page !is DrawerPage.SettingPage) {
                                        Text(
                                            text = stringResource(page.iconString),
                                            fontFamily = FontAwesome,
                                            fontSize = 20.sp,
                                        )
                                    } else {
                                        Icon(
                                            painter = painterResource(id = R.drawable.vector_settings),
                                            contentDescription = null,
                                        )
                                    }
                                },
                                label = { Text(stringResource(page.name)) },
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Column(
                modifier =
                    Modifier
                        .padding(innerPadding)
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.background),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                DestinationContent(
                    navManager = navigationManager,
                    server = server,
                    destination = destination,
                    composeUiConfig = composeUiConfig,
                    itemOnClick = itemOnClick,
                    longClicker = longClicker,
                    onChangeTheme = onChangeTheme,
                    onSwitchServer = onSwitchServer,
                    modifier =
                        Modifier
                            .fillMaxSize(),
                    onUpdateTitle = { title = it },
                )
            }
        }
    }
}
