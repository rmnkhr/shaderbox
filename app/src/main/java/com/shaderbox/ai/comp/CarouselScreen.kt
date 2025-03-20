package com.shaderbox.ai.comp

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shaderbox.ai.comp.shaders.CardShader
import com.shaderbox.ai.comp.shaders.DreamscapeChromaticAberrationShader
import com.shaderbox.ai.comp.shaders.DreamscapeShader
import com.shaderbox.ai.comp.shaders.GentleRainbowShader
import com.shaderbox.ai.comp.shaders.NorthernLightsShader
import com.shaderbox.ai.comp.shaders.OrganicMotionShader
import com.shaderbox.ai.comp.shaders.RainbowShader
import com.shaderbox.ai.comp.shaders.SmokeShader
import kotlin.math.absoluteValue


@Composable
fun CarouselScreen() {

    val shadersScreens = listOf(
        SmokeShader(),
        NorthernLightsShader(),
        DreamscapeChromaticAberrationShader(),
        RainbowShader(),
        OrganicMotionShader(),
        GentleRainbowShader(),
        DreamscapeShader(),
    )

    val pagerState = rememberPagerState(
        initialPage = 0, initialPageOffsetFraction = 0f, pageCount = {
            shadersScreens.size
        })


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101010)),
    ) {

        var screenName by remember { mutableStateOf("") }
        screenName = shadersScreens.getOrNull(pagerState.currentPage)?.name ?: ""

        AnimatedContent(
            targetState = screenName,
            transitionSpec = {
                // Compare the incoming number with the previous number.
                if (targetState > initialState) {
                    // If the target number is larger, it slides up and fades in
                    // while the initial (smaller) number slides up and fades out.
                    slideInVertically { height -> height } + fadeIn() togetherWith
                            slideOutVertically { height -> -height } + fadeOut()
                } else {
                    // If the target number is smaller, it slides down and fades in
                    // while the initial number slides down and fades out.
                    slideInVertically { height -> -height } + fadeIn() togetherWith
                            slideOutVertically { height -> height } + fadeOut()
                }.using(
                    // Disable clipping since the faded slide-in/out should
                    // be displayed out of bounds.
                    SizeTransform(clip = false)
                )
            }, label = "animated content",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 160.dp)
        ) { targetCount ->
            Text(
                text = targetCount,
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }


        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 48.dp),
            pageSpacing = 16.dp,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val outerParallax = pageOffset * 50f
            val innerParallax = pageOffset * 80f

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = outerParallax
                        val scale = 1f - (0.1f * pageOffset.absoluteValue)
                        scaleX = scale
                        scaleY = scale
                        rotationY = pageOffset * 5f
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Gray)
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
            ) {
                Box(
                    modifier = Modifier.Companion
                        .fillMaxSize()
                        .graphicsLayer {
                            translationX = innerParallax
                            scaleX = 1f + (0.7f * pageOffset.absoluteValue)
                            scaleY = 1f + (0.7f * pageOffset.absoluteValue)
                        },
                ) {
                    shadersScreens.getOrNull(page)?.let { shadersScreens ->
                        CardShader(shadersScreens.shader, shadersScreens.speed)
                    }
                }
            }
        }

        // Dots Indicator
        DotsIndicator(
            totalDots = shadersScreens.size,
            selectedIndex = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 200.dp)
        )
    }
}

@Composable
fun DotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = Color.White,
    unSelectedColor: Color = Color.Gray,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .wrapContentWidth()
            .padding(8.dp)
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (index == selectedIndex) selectedColor else unSelectedColor)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CarouselScreenPreview() {
    CarouselScreen()
}