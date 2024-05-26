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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.imagesearch.ui.util.Constants.Companion.SEARCH
import com.android.imagesearch.ui.util.Constants.Companion.STORAGE
import com.android.imagesearch.R
import com.android.imagesearch.ui.search.SearchListScreen
import com.android.imagesearch.ui.storage.StorageScreen
import com.android.imagesearch.ui.theme.ImageSearchTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageSearchTheme {
                MainScreenView()
            }
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
    // 리스트로 바텀 네비게이션 아이템들을 정의합니다.
    val items = listOf(
        BottomNavItem.Search,
        BottomNavItem.Storage,
    )

    // 현재 선택된 라우트를 추적하기 위한 MutableState를 선언합니다.
    val selectedRoute = remember { mutableStateOf(BottomNavItem.Search.screenRoute) }

    // Surface를 만들어서 바텀 네비게이션을 감싸줍니다.
    Surface(
        shadowElevation = 0.dp,
        modifier = Modifier
            .padding(12.dp)
            .height(60.dp)
            .shadow(
                color = colorResource(id = R.color.empty), // 그림자 색상을 설정합니다.
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
            // 네비게이션의 현재 상태를 추적
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            // 모든 바텀 네비게이션 아이템을 반복하여 추가
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon), // 아이콘을 설정
                            contentDescription = stringResource(id = item.title), // 아이콘 설명을 설정
                            modifier = Modifier
                                .width(26.dp)
                                .height(26.dp),
                            tint = if (currentRoute == item.screenRoute) {
                                colorResource(id = R.color.color_1) // 선택된 아이템의 색상을 설정
                            } else {
                                colorResource(id = R.color.empty) // 선택되지 않은 아이템의 색상을 설정
                            }
                        )
                    },
                    label = {
                        Text(
                            stringResource(id = item.title), // 라벨을 설정
                            fontSize = 9.sp,
                            color = if (currentRoute == item.screenRoute) {
                                colorResource(id = R.color.color_1) // 선택된 아이템의 색상을 설정
                            } else {
                                colorResource(id = R.color.empty) // 선택되지 않은 아이템의 색상을 설정
                            }
                        )
                    },
                    selected = currentRoute == item.screenRoute, // 현재 선택된 아이템을 설정
                    alwaysShowLabel = true,
                    onClick = {
                        // 클릭된 아이템의 화면 라우트를 선택된 라우트로 설정
                        selectedRoute.value = item.screenRoute

                        // 네비게이션 그래프에서 시작(destination) 목적지의 경로(route)를 찾음
                        val startDestination = navController.graph.findStartDestination().route

                        // 클릭된 아이템의 화면으로 네비게이션을 시작합니다.
                        navController.navigate(item.screenRoute) {
                            // 시작(destination) 경로로 백 스택을 팝(pop)하고, 상태를 저장
                            if (startDestination != null) {
                                popUpTo(startDestination) {
                                    saveState = true
                                }
                            }
                            // 목적지가 백 스택의 맨 위에 있으면 해당 목적지를 다시 시작(destination)하지 않도록 지정
                            launchSingleTop = true
                            // 목적지로 이동할 때 이전 상태를 복원하도록 지정
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
    // 네비게이션의 호스트를 설정
    NavHost(navController = navHostController, startDestination = BottomNavItem.Search.screenRoute) {
        // 바텀 네비게이션 아이템에 따른 화면을 정의
        composable(route = BottomNavItem.Search.screenRoute) {
            SearchListScreen() // 검색 화면을 표시하는 컴포저블을 호출
        }
        composable(route = BottomNavItem.Storage.screenRoute) {
            StorageScreen() // 보관함 화면을 표시하는 컴포저블을 호출
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
