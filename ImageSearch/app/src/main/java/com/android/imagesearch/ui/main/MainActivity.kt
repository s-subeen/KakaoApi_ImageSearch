package com.android.imagesearch.ui.main

import android.annotation.SuppressLint
import android.graphics.BlurMaskFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.imagesearch.ui.util.Constants.Companion.SEARCH
import com.android.imagesearch.ui.util.Constants.Companion.STORAGE
import com.android.imagesearch.R
import com.android.imagesearch.ui.search.SearchListScreen
import com.android.imagesearch.ui.search.SearchViewModel
import com.android.imagesearch.ui.storage.StorageScreen
import com.android.imagesearch.ui.storage.StorageViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreenView()
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenView() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationUI(navController = navController) }
    ) {
        NavigationGraph(navHostController = navController)
    }
}

@Composable
fun BottomNavigationUI(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Search,
        BottomNavItem.Storage,
    )

    Surface(
        shadowElevation = 0.dp,
        modifier = Modifier
            .padding(12.dp)
            .height(60.dp)
            .shadow(
                color = colorResource(id = R.color.empty),
                borderRadius = 32.dp,
                blurRadius = 32.dp,
                offsetY = 12.dp,
                offsetX = 12.dp,
                spread = 1f.dp
                ),
        color = Color.Transparent
    ) {
        BottomNavigation(
            backgroundColor = Color.White,
            contentColor = Color(0xFF3F414E),
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = stringResource(id = item.title),
                            modifier = Modifier
                                .width(26.dp)
                                .height(26.dp),
                            tint = if (currentRoute == item.screenRoute) {
                                colorResource(id = R.color.color_1)
                            } else {
                                colorResource(id = R.color.empty)
                            }
                        )
                    },
                    label = {
                        Text(
                            stringResource(id = item.title),
                            fontSize = 9.sp,
                            color = if (currentRoute == item.screenRoute) {
                                colorResource(id = R.color.color_1)
                            } else {
                                colorResource(id = R.color.empty)
                            }
                        )
                    },
                    selected = currentRoute == item.screenRoute,
                    alwaysShowLabel = true,
                    onClick = {
                        navController.navigate(item.screenRoute) {
                            navController.graph.startDestinationRoute?.let {
                                popUpTo(it) { saveState = true }
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

sealed class BottomNavItem(
    val title: Int,
    val icon: Int,
    val screenRoute: String
) {
    data object Search : BottomNavItem(R.string.fragment_search, R.drawable.ic_tab_search, SEARCH)
    data object Storage :
        BottomNavItem(R.string.fragment_favorite, R.drawable.ic_tab_storage, STORAGE)
}

@Composable
fun NavigationGraph(navHostController: NavHostController) {
    val searchViewModel: SearchViewModel = viewModel()
    val storageViewModel: StorageViewModel = viewModel()

    NavHost(navController = navHostController, startDestination = BottomNavItem.Search.screenRoute) {
        composable(BottomNavItem.Search.screenRoute) {
            SearchListScreen(searchViewModel)
        }
        composable(BottomNavItem.Storage.screenRoute) {
            StorageScreen(storageViewModel)
        }
    }
}

fun Modifier.shadow(
    color: Color,
    borderRadius: Dp,
    blurRadius: Dp,
    offsetY: Dp,
    offsetX: Dp,
    spread: Dp,
    modifier: Modifier = Modifier
) = this.then(
    modifier.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel = (this.size.height + spreadPixel)
            if (blurRadius != 0.dp) {
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }
            frameworkPaint.color = color.toArgb()
            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
)
