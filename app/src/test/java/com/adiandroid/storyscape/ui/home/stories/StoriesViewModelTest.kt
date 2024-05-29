package com.adiandroid.storyscape.ui.home.stories
import org.junit.Assert.*

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.adiandroid.storyscape.data.datapaging.StoryRepository
import com.adiandroid.storyscape.data.model.response.ListStoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import com.adiandroid.storyscape.DataDummy
import com.adiandroid.storyscape.MainDispatcherRule
import com.adiandroid.storyscape.utils.getOrAwaitValue
import com.adiandroid.storyscape.adapter.StoryAdapter
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.mockStatic
import android.util.Log


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `when Get List Story Should Not Null and Return Data`() = runBlockingTest {
        mockStatic(Log::class.java).use { logMock ->
            logMock.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }
                .thenReturn(false)

            val dummyStoryItems = DataDummy.generateDummyStoryItems()
            val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyStoryItems)
            val expectedStories = MutableLiveData<PagingData<ListStoryItem>>()
            expectedStories.value = data

            Mockito.`when`(storyRepository.getListStories()).thenReturn(expectedStories)

            val storiesViewModel = StoriesViewModel(storyRepository)
            val actualStories: PagingData<ListStoryItem> =
                storiesViewModel.getListStory.getOrAwaitValue()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualStories)

            Assert.assertNotNull(differ.snapshot())
            Assert.assertEquals(dummyStoryItems.size, differ.snapshot().size)
            Assert.assertEquals(dummyStoryItems[0], differ.snapshot()[0])
        }
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runBlockingTest {
        mockStatic(Log::class.java).use { logMock ->
            logMock.`when`<Boolean> { Log.isLoggable(Mockito.anyString(), Mockito.anyInt()) }
                .thenReturn(false)

            val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
            val expectedQuote = MutableLiveData<PagingData<ListStoryItem>>()
            expectedQuote.value = data

            Mockito.`when`(storyRepository.getListStories()).thenReturn(expectedQuote)

            val storiesViewModel = StoriesViewModel(storyRepository)
            val actualQuote: PagingData<ListStoryItem> =
                storiesViewModel.getListStory.getOrAwaitValue()

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,
                workerDispatcher = Dispatchers.Main
            )
            differ.submitData(actualQuote)
            // Revisi untuk mendapatkan data yang didapat yaitu 0
            assertEquals(0, differ.snapshot().size)
        }
    }

    class StoryPagingSource(private val stories: List<ListStoryItem>) :
        PagingSource<Int, ListStoryItem>() {
        companion object {
            fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
            return state.anchorPosition
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
            return LoadResult.Page(
                data = stories,
                prevKey = null,
                nextKey = null
            )
        }
    }

    val noopListUpdateCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }
}