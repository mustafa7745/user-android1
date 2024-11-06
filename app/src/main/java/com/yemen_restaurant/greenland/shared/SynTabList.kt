package com.yemen_restaurant.greenland.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yemen_restaurant.greenland.models.CategoryModel
import com.yemen_restaurant.greenland.models.ProductModel

class SynTabList {

//    @Composable
//    fun MyTabBar(
//        categories: List<CategoryModel>,
//        selectedTabIndex: Int,
//        onTabClicked: (index: Int, category: CategoryModel) -> Unit
//    ) {
//        ScrollableTabRow(
//            selectedTabIndex = selectedTabIndex,
//            edgePadding = 0.dp
//        ) {
//            categories.forEachIndexed { index, category ->
//                Tab(
//                    selected = index == selectedTabIndex,
//                    onClick = { onTabClicked(index, category) },
//                    text = { Text(category.name)) }
//                )
//            }
//        }
//    }
//
//    @Composable
//    fun SynchronizedTabWithLibrary() {
//        // Sample data for tabs
//        val tabs = listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5")
//
//        // Track the selected tab (Shared state for tab and content)
//        var selectedTab by remember { mutableStateOf(0) }
//
//        // LazyListState for LazyRow (Tabs)
//        val lazyRowState = rememberLazyListState()
//
//        // LazyListState for LazyColumn (Content)
//        val lazyColumnState = rememberLazyListState()
//
//        // TabSync to synchronize LazyRow and LazyColumn
//        TabSync(
//            tabCount = tabs.size,
//            selectedTab = selectedTab,
//            onTabSelected = { newTabIndex ->
//                selectedTab = newTabIndex
//                lazyRowState.scrollToItem(newTabIndex)  // Scroll LazyRow to selected tab
//                lazyColumnState.scrollToItem(newTabIndex)  // Scroll LazyColumn to content
//            }
//        ) {
//
//            Column(modifier = Modifier.fillMaxSize()) {
//
//                // LazyRow for tab selection
//                LazyRow(
//                    state = lazyRowState,
//                    modifier = Modifier.fillMaxWidth().padding(8.dp)
//                ) {
//                    items(tabs) { tab ->
//                        TabItem(tab = tab, isSelected = tabs.indexOf(tab) == selectedTab)
//                    }
//                }
//
//                Spacer(modifier = Modifier.height(16.dp))
//
//                // LazyColumn for content
//                LazyColumn(
//                    state = lazyColumnState,
//                    modifier = Modifier.fillMaxSize().padding(8.dp)
//                ) {
//                    items(tabs) { tab ->
//                        ContentForTab(tab)
//                    }
//                }
//            }
//        }
//    }
//
//    @Composable
//    fun MyComposable(items: List<ProductModel>) {
//
//        val (selectedTabIndex, setSelectedTabIndex, syncedListState) = lazyListTabSync(items.indices.toList())
//
//    }
////    @Composable
////    fun MyLazyList(
////        categories: List<Category>,
////        listState: LazyListState = rememberLazyListState(),
////    ) {
////        LazyColumn(
////            state = listState,
////            verticalArrangement = Arrangement.spacedBy(16.dp)
////        ) {
////            itemsIndexed(categories) { _, category ->
////                ItemCategory(category)
////            }
////        }
////    }
}