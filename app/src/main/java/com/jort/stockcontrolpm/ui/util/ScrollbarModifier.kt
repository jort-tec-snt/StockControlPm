package com.jort.stockcontrolpm.ui.util

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Dibuja una barra de desplazamiento vertical sutil sobre el contenido.
 * Funciona con ScrollState (Column con verticalScroll).
 */
fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    width: Dp = 3.dp,
    color: Color = Color(0xFF0F5132).copy(alpha = 0.35f),
    minThumbHeight: Dp = 40.dp
): Modifier = this.drawWithContent {
    drawContent()

    val viewportHeight  = size.height
    val totalHeight     = scrollState.maxValue.toFloat() + viewportHeight
    if (totalHeight <= viewportHeight) return@drawWithContent   // no hay nada que scrollear

    val thumbRatio   = viewportHeight / totalHeight
    val thumbHeight  = maxOf(thumbRatio * viewportHeight, minThumbHeight.toPx())
    val scrollRatio  = if (scrollState.maxValue == 0) 0f
                       else scrollState.value.toFloat() / scrollState.maxValue
    val thumbTop     = scrollRatio * (viewportHeight - thumbHeight)

    drawRoundRect(
        color       = color,
        topLeft     = Offset(size.width - width.toPx() - 2.dp.toPx(), thumbTop),
        size        = Size(width.toPx(), thumbHeight),
        cornerRadius = CornerRadius(width.toPx() / 2)
    )
}

/**
 * Dibuja una barra de desplazamiento vertical sobre LazyColumn/LazyRow.
 */
fun Modifier.verticalScrollbar(
    lazyState: LazyListState,
    width: Dp = 3.dp,
    color: Color = Color(0xFF0F5132).copy(alpha = 0.35f),
    minThumbHeight: Dp = 40.dp
): Modifier = this.drawWithContent {
    drawContent()

    val layoutInfo      = lazyState.layoutInfo
    val totalItems      = layoutInfo.totalItemsCount
    if (totalItems == 0) return@drawWithContent

    val visibleItems    = layoutInfo.visibleItemsInfo
    if (visibleItems.isEmpty()) return@drawWithContent

    val viewportHeight  = layoutInfo.viewportEndOffset.toFloat()
    val avgItemHeight   = visibleItems.sumOf { it.size } / visibleItems.size.toFloat()
    val totalHeight     = totalItems * avgItemHeight

    if (totalHeight <= viewportHeight) return@drawWithContent

    val thumbRatio      = viewportHeight / totalHeight
    val thumbHeight     = maxOf(thumbRatio * viewportHeight, minThumbHeight.toPx())
    val scrolled        = lazyState.firstVisibleItemIndex * avgItemHeight +
                          lazyState.firstVisibleItemScrollOffset
    val scrollRatio     = scrolled / (totalHeight - viewportHeight)
    val thumbTop        = scrollRatio * (viewportHeight - thumbHeight)

    drawRoundRect(
        color        = color,
        topLeft      = Offset(size.width - width.toPx() - 2.dp.toPx(), thumbTop),
        size         = Size(width.toPx(), thumbHeight),
        cornerRadius = CornerRadius(width.toPx() / 2)
    )
}
