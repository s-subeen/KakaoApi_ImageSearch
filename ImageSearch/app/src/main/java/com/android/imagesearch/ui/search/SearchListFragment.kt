package com.android.imagesearch.ui.search

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.android.imagesearch.ui.util.FormatManager.formatDate
import com.android.imagesearch.R
import com.android.imagesearch.data.SearchListType
import com.android.imagesearch.data.SearchModel
import com.android.imagesearch.ui.storage.GradientProvider

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun SearchListScreen(viewModel: SearchViewModel = hiltViewModel()) {
    val searchState by viewModel.searchResult.collectAsState()

    Scaffold(
        topBar = {
            searchState.keyword?.let {
                SearchTopBar(
                    onSearch = { newQuery ->
                        viewModel.resetPageCount()
                        viewModel.saveStorageSearchWord(newQuery)
                        viewModel.searchCombinedResults(newQuery)
                    },
                    query = it,
                    onQueryChange = { _ -> }
                )
            }
        },
        content = {
            if (searchState.list.isNotEmpty()) {
                SearchList(
                    items = searchState.list,
                    onItemClick = { viewModel.updateStorageItem(it) },
                    onScrollEnd = { viewModel.plusPageCount() }
                )
            } else {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(color = colorResource(id = R.color.color_1))
                }
            }
        }
    )

    val currentKeyword = searchState.keyword

    LaunchedEffect(currentKeyword) {
        viewModel.reloadStorageItems()
        if (currentKeyword != null) {
            viewModel.searchCombinedResults(currentKeyword)
        }
    }
}

@Composable
fun SearchTopBar(
    onSearch: (String) -> Unit, // 검색 실행 콜백 함수
    query: String, // 현재 검색어
    onQueryChange: (String) -> Unit // 검색어 변경 콜백 함수
) {
    /*
     * 현재 텍스트 필드 값
     * 현재의 검색어를 가지고 있는 text 상태를 생성하고 이를 Composable 함수 내에서 관리
     */
    var text by remember { mutableStateOf(TextFieldValue(query)) }

    val gradientLine = listOf(
        colorResource(id = R.color.white),
        colorResource(id = R.color.color_3)
    )

    Column(
        modifier = Modifier.fillMaxWidth() // 수직 컬럼
    ) {
        // 로고 이미지와 검색창
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_symbol),
                contentDescription = null,
                modifier = Modifier
                    .width(100.dp)
                    .padding(start = 16.dp)
            )
            // 검색창
            Row(
                modifier = Modifier
                    .height(48.dp) // 검색창 높이
                    .padding(horizontal = 16.dp)
                    .border(
                        BorderStroke(
                            width = 1.dp, // 테두리 두께
                            brush = Brush.linearGradient(
                                colors = GradientProvider(), // 그라데이션 색상
                                start = Offset.Zero,
                                end = Offset.Infinite
                            )
                        ),
                        shape = RoundedCornerShape(50) // 테두리 모양
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 텍스트 입력 필드
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                        onQueryChange(it.text) // 검색어 변경 콜백 호출
                    },
                    modifier = Modifier
                        .weight(1f) // 가중치 1
                        .padding(start = 8.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = TextStyle.Default
                )

                // 검색 아이콘 버튼
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(end = 12.dp)
                        .clickable {
                            onSearch(text.text)
                        }, // 클릭 가능
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_tab_search),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // 그라데이션 라인
        Box(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .height(4.dp)
                .alpha(0.3f)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                drawRect( // 사각형을 그리는 데 사용되며 그라데이션 라인을 나타냄
                    brush = Brush.verticalGradient( // 그라데이션 효과를 적용
                        colors = gradientLine, // 그라데이션 라인 색상
                        startY = 0f, // 그라데이션의 상단을 의미
                        endY = size.height // 그라데이션의 하단을 의미
                    ),
                    size = Size(size.width, size.height)
                )
            }
        }
    }
}


@Composable
fun SearchList(
    items: List<SearchModel>, // 검색 항목 목록
    onItemClick: (SearchModel) -> Unit, // 항목을 클릭할 때 호출되는 콜백 함수
    onScrollEnd: () -> Unit // 스크롤이 끝에 도달했을 때 호출되는 콜백 함수
) {
    // 스크롤 감시를 추가하여 리스트의 끝에 도달했을 때 onScrollEnd 콜백을 호출
    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        state = listState,
        columns = GridCells.Fixed(2), // 고정된 열 개수
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp), // 아이템 간의 수직 간격
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        items(items.size) { index ->
            SearchItem(
                item = items[index],
                onItemClick = onItemClick,
                modifier = Modifier.fillMaxWidth()
            )
            // 리스트의 끝에 도달했을 때 onScrollEnd 콜백 호출
            if (index == items.size - 1 && listState.isScrollInProgress.not()) {
                onScrollEnd()
            }
        }
    }
}


@Composable
fun SearchItem(
    item: SearchModel, // 검색 항목을 나타내는 데이터 모델
    onItemClick: (SearchModel) -> Unit, // 항목을 클릭했을 때 호출되는 콜백 함수
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(12.dp) // 세로 방향의 컬럼 레이아웃과 패딩
    ) {
        Card(
            modifier = modifier.clickable { onItemClick(item) }, // 카드 전체에 클릭 가능 영역 설정
            shape = RoundedCornerShape(16.dp) // 카드의 모서리를 둥글게 만듦
        ) {
            ConstraintLayout( // 자식 뷰의 위치를 제약 조건으로 지정하는 ConstraintLayout
                modifier = Modifier.fillMaxSize()
            ) {
                val (imageView, heartImageView, itemTypeText, dateTimeText) = createRefs()

                // 비동기 이미지 로딩 컴포넌트
                AsyncImage(
                    model = item.thumbnailUrl ?: "", // 이미지 URL, 기본값은 빈 문자열
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth, // 이미지 콘텐츠의 스케일 타입
                    modifier = Modifier
                        .constrainAs(imageView) {
                            top.linkTo(parent.top) // 상단 가장자리를 부모의 상단 가장자리에 링크
                            start.linkTo(parent.start) // 시작 가장자리를 부모의 시작 가장자리에 링크
                            end.linkTo(parent.end) // 끝 가장자리를 부모의 끝 가장자리에 링크
                        }
                        .fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .constrainAs(itemTypeText) {
                            top.linkTo(imageView.top) // 상단 가장자리를 이미지 뷰의 상단 가장자리에 링크
                            start.linkTo(imageView.start) // 시작 가장자리를 이미지 뷰의 시작 가장자리에 링크
                        }
                        .background( // 선형 그라데이션 배경
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    colorResource(id = R.color.color_1),
                                    colorResource(id = R.color.color_2),
                                    colorResource(id = R.color.color_3)
                                ),
                                start = Offset.Zero,
                                end = Offset.Infinite
                            ),
                            shape = RoundedCornerShape(8.dp) // 배경의 둥근 모서리
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // 텍스트 표시
                    Text(
                        text = item.itemType.name,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp)
                    )
                }

                // 날짜와 시간을 표시하는 텍스트
                Text(
                    text = formatDate(item.datetime), // 형식화된 날짜와 시간 문자열
                    fontSize = 12.sp,
                    color = Color(android.graphics.Color.parseColor("#c8c8c8")),
                    modifier = Modifier
                        .constrainAs(dateTimeText) {
                            start.linkTo(imageView.start) // 시작 가장자리를 이미지 뷰의 시작 가장자리에 링크
                            end.linkTo(imageView.end) // 끝 가장자리를 이미지 뷰의 끝 가장자리에 링크
                            bottom.linkTo(imageView.bottom) // 하단 가장자리를 이미지 뷰의 하단 가장자리에 링크
                        }
                        .fillMaxWidth()
                        .background(
                            // copy 함수를 사용하여 기존의 Color 객체를 변경하지 않고 새로운 Color 객체 생성
                            color = Color.Black.copy(alpha = 0.5f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    textAlign = TextAlign.Center // 텍스트 정렬
                )

                // 항목이 저장되어 있으면 하트 아이콘을 표시하는 이미지 뷰
                if (item.isSaved) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_heart_24),
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .constrainAs(heartImageView) {
                                top.linkTo(
                                    parent.top,
                                    margin = 4.dp
                                ) // 상단 가장자리를 부모의 상단 가장자리에 링크하고 여백 추가
                                end.linkTo(
                                    parent.end,
                                    margin = 8.dp
                                ) // 끝 가장자리를 부모의 끝 가장자리에 링크하고 여백 추가
                            }
                    )
                }
            }
        }

        if (item.itemType == SearchListType.VIDEO) {
            Text(
                text = item.siteName ?: "",
                fontSize = 12.sp,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                maxLines = 2, // 최대 라인 수
                overflow = TextOverflow.Ellipsis // 텍스트가 화면에 표시되는 영역을 넘어가는 경우 말 줄임표를 표시
            )
        }
    }
}