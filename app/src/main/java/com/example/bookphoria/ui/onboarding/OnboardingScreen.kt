package com.example.bookphoria.ui.onboarding

import android.graphics.Color
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.bookphoria.ui.theme.BookPhoriaTheme
import com.example.bookphoria.ui.theme.DeepBlue
import com.example.bookphoria.ui.theme.LightBlue
import com.example.bookphoria.ui.theme.MediumBlue
import com.example.bookphoria.ui.theme.PrimaryOrange
import com.example.bookphoria.ui.theme.SoftOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { onboardingItems.size }
    )
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage
    val item = onboardingItems.getOrNull(currentPage) ?: onboardingItems.first()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(item.backgroundColor)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 24.dp)
            ) { page ->
                val onboardingItem = onboardingItems[page]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = onboardingItem.imageRes),
                        contentDescription = onboardingItem.title,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(392.dp)
                            .padding(bottom = 24.dp)
                    )

                    Text(
                        text = onboardingItem.title,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = onboardingItem.description,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pager Indicator
            HorizontalPagerIndicator(
                pagerState = pagerState,
                activeColor = item.pagerActiveColor,
                inactiveColor = item.pagerInactiveColor,
                indicatorWidth = 12.dp,
                indicatorHeight = 12.dp,
                spacing = 10.dp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (currentPage > 0) {
                    TextButton(
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(currentPage - 1) }
                        }
                    ) {
                        Text(
                            text = "Kembali",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SoftOrange
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(80.dp))
                }

                Button(
                    onClick = {
                        if (currentPage < onboardingItems.lastIndex) {
                            scope.launch { pagerState.animateScrollToPage(onboardingItems.lastIndex) }
                        } else {
                            onFinished()
                        }
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = item.buttonColor
                    )
                ) {
                    Text(
                        text = item.buttonText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = item.buttonTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun HorizontalPagerIndicator(
    pagerState: PagerState,
    activeColor: androidx.compose.ui.graphics.Color,
    inactiveColor: androidx.compose.ui.graphics.Color,
    indicatorWidth: Dp,
    indicatorHeight: Dp,
    spacing: Dp,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(pagerState.pageCount) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = spacing / 2)
                    .size(width = indicatorWidth, height = indicatorHeight)
                    .clip(CircleShape)
                    .background(
                        if (pagerState.currentPage == index) activeColor else inactiveColor
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    BookPhoriaTheme {
        OnboardingScreen(
            onFinished = {}
        )
    }
}