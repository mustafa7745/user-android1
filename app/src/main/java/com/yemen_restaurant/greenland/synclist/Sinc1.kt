package com.yemen_restaurant.greenland.synclist

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yemen_restaurant.greenland.R
import com.yemen_restaurant.greenland.activities.CustomImageView
import com.yemen_restaurant.greenland.application.MyApplication
import com.yemen_restaurant.greenland.models.CategoryModel
import com.yemen_restaurant.greenland.models.ProductModel
import com.yemen_restaurant.greenland.shared.RequestServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun LazyListState.findFirstFullyVisibleItemIndex(): Int = findFullyVisibleItemIndex(reversed = false)

fun LazyListState.findLastFullyVisibleItemIndex(): Int = findFullyVisibleItemIndex(reversed = true)

fun LazyListState.findFullyVisibleItemIndex(reversed: Boolean): Int {
    layoutInfo.visibleItemsInfo.run { if (reversed) reversed() else this }.forEach { itemInfo ->
        val itemStartOffset = itemInfo.offset
        val itemEndOffset = itemInfo.offset + itemInfo.size
        val viewportStartOffset = layoutInfo.viewportStartOffset
        val viewportEndOffset = layoutInfo.viewportEndOffset
        if (itemStartOffset >= viewportStartOffset && itemEndOffset <= viewportEndOffset) {
            return itemInfo.index
        }
    }
    return -1
}

@Stable
class TabSyncState(
    var selectedTabIndex: Int,
    var lazyListState: LazyListState,
    private var coroutineScope: CoroutineScope,
    private var syncedIndices: List<Int>,
    private var smoothScroll: Boolean,
) {
    operator fun component1(): Int = selectedTabIndex
    operator fun component2(): (Int) -> Unit = {
        require(it <= syncedIndices.size - 1) {
            "The selected tab's index is out of the bounds of the syncedIndices list provided. " +
                    "Either add an index to syncedIndices that corresponds to an item to your lazy list, " +
                    "or remove your excessive tab"
        }



        selectedTabIndex = it

        coroutineScope.launch {
            if (smoothScroll) {
                lazyListState.animateScrollToItem(syncedIndices[selectedTabIndex])
            } else {
                lazyListState.scrollToItem(syncedIndices[selectedTabIndex])
            }
        }
    }

    operator fun component3(): LazyListState = lazyListState
}

@Composable
fun lazyListTabSync(
    syncedIndices: List<Int>,
    lazyListState: LazyListState = rememberLazyListState(),
    tabsCount: Int? = null,
    smoothScroll: Boolean = false
): TabSyncState {
    require(syncedIndices.isNotEmpty()) {
        "You can't use the mediator without providing at least one index in the syncedIndices array"
    }

    if (tabsCount != null) {
        require(tabsCount <= syncedIndices.size) {
            "The tabs count is out of the bounds of the syncedIndices list provided. " +
                    "Either add an index to syncedIndices that corresponds to an item to your lazy list, " +
                    "or remove your excessive tab"
        }
    }

    var selectedTabIndex by remember { mutableStateOf(0) }

    // This effect will only trigger when the layout info changes
    LaunchedEffect(lazyListState) {
        // Avoid too frequent updates using debounce-like behavior
        val debounceTime = 100L // Adjust time based on your needs
        var lastUpdateTime = System.currentTimeMillis()

        snapshotFlow { lazyListState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastUpdateTime < debounceTime) {
                    return@collect // Skip updates if they occur too quickly
                }
                lastUpdateTime = currentTime

//                Log.d("LazyList", visibleItems.firstOrNull().toString())
//                if (visibleItems.) {
//                    Log.d("LazyList", "First item is visible")
//                } else {
//                    Log.d("LazyList", "First item is not visible")
//                }

                // Find the first fully visible item
                val itemPosition = visibleItems.firstOrNull()?.index ?: -1
                if (itemPosition != -1) {
                    // Check if the item is within the synced indices and update the selected tab
                    if (syncedIndices.contains(itemPosition) && itemPosition != syncedIndices[selectedTabIndex]) {
                        selectedTabIndex = syncedIndices.indexOf(itemPosition)
                    }
                }
            }
    }

    return TabSyncState(
        selectedTabIndex,
        lazyListState,
        rememberCoroutineScope(),
        syncedIndices,
        smoothScroll
    )
}

@Composable
fun MyTabBar(
    categories: List<Category>,
    selectedTabIndex: Int,
    requestServer: RequestServer? = null,
    onTabClicked: (index: Int, category: Category) -> Unit,

) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,

    ) {
        categories.forEachIndexed { index, category ->
            // Using remember to avoid recomposing static tab content
            val tabName = remember(category) { category.category.name }

            Tab(
                modifier= Modifier.padding(3.dp).height(80.dp),
                selected = index == selectedTabIndex,
                onClick = { onTabClicked(index, category) },
                icon =
                {
                    if (requestServer != null)
                    CustomImageView(
                        context = MyApplication.AppContext,
                        imageUrl = category.category.category_image_path +  category.category.image,
                        modifier = Modifier.size(50.dp).border(
                            1.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(
                                16.dp
                            )
                        )
                            .clip(
                                RoundedCornerShape(
                                    16.dp
                                )
                            ),
                        okHttpClient = requestServer.createOkHttpClientWithCustomCert()
                    )
                },
                text = {
                    Text(
                    modifier = Modifier.padding(3.dp),
                    textAlign = TextAlign.Start,
                    text = tabName,
                    fontFamily = FontFamily(
                        Font(R.font.bukra_bold)
                    ),
                    fontSize = 12.sp,
                    color = if (index == selectedTabIndex) MaterialTheme.colorScheme.primary else Color.Black
                )

                }
            )
        }
    }
}


fun convertToCategoryStructure(categories: List<CategoryModel>, products: List<ProductModel>): List<Category> {
    // Create a map of categoryId to CategoryModel for quick lookup
    val categoryMap = categories.associateBy { it.id }

    // Group products by categoryId
    val groupedProducts = products.groupBy { it.categoryId }

    return categoryMap.mapNotNull { (categoryId, categoryModel) ->

        // Get products for this category, or return null if none
        val productsInCategory = groupedProducts[categoryId] ?: return@mapNotNull null

        // Sort the products as needed (you can modify the sorting logic here)
        val sortedProducts = productsInCategory.sortedBy { it.id }  // Example: sort by product ID
        Category(categoryModel, sortedProducts)
    }.sortedBy { it.category.order }  // Sort the categories by their order
}
data class Category(
    val category: CategoryModel,
    val listOfProducts: List<ProductModel> // This is the list of items (products) for this category
)