package com.example.abc_jobs_alpaca.viewmodel_test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.abc_jobs_alpaca.model.models.InterviewItem
import com.example.abc_jobs_alpaca.model.models.InterviewsResponse
import com.example.abc_jobs_alpaca.model.models.deserializeInterviews
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.InterviewViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class InterviewViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()



    @Mock
    private lateinit var abcJobsRepository: ABCJobsRepository

    @Mock
    private lateinit var interviewsObserver: Observer<List<InterviewItem>?>

    private lateinit var interviewViewModel: InterviewViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        interviewViewModel = InterviewViewModel(abcJobsRepository)
        interviewViewModel.interviewsInfoList.observeForever(interviewsObserver)
    }

    @Test
    fun `loadInterviewsItemsInfo success`() = runBlocking {
        // Arrange
        val fakeToken = "fakeToken"
        interviewViewModel.onTokenUpdated(fakeToken)
        val fakeInterviewList = listOf(
            InterviewItem(1, "Interview 1", "Company 1", "2023-11-19T12:30:00.000Z", true, 42),
            InterviewItem(2, "Interview 2", "Company 2", "2023-11-20T14:00:00.000Z", false, null)
        )
        val fakeResponse = InterviewsResponse(true, fakeInterviewList)
        `when`(abcJobsRepository.getAllInterviews(fakeToken)).thenReturn(Result.success(fakeResponse))

        // Act
        interviewViewModel.loadInterviewsItemsInfo()

        // Assert
        verify(interviewsObserver).onChanged(fakeInterviewList)
    }

    @Test
    fun `loadInterviewsItemsInfo failure`() = runBlocking {
        // Arrange
        val fakeToken = "fakeToken"
        interviewViewModel.onTokenUpdated(fakeToken)
        `when`(abcJobsRepository.getAllInterviews(fakeToken)).thenReturn(Result.failure(Exception()))

        // Act
        interviewViewModel.loadInterviewsItemsInfo()

        // Assert
        verify(interviewsObserver, never()).onChanged(any())
    }

    @Test
    fun `deserializeInterviews success`() {
        // Arrange
        val json = JSONObject()
        json.put("success", true)

        val dataArray = JSONArray()
        val item1 = JSONObject()
        item1.put("id_vacancy", 1)
        item1.put("name", "Interview 1")
        item1.put("company", "Company 1")
        item1.put("interview_date", "2023-11-19T12:30:00.000Z")
        item1.put("completed", true)
        item1.put("result", 42)
        dataArray.put(item1)

        val item2 = JSONObject()
        item2.put("id_vacancy", 2)
        item2.put("name", "Interview 2")
        item2.put("company", "Company 2")
        item2.put("interview_date", "2023-11-20T14:00:00.000Z")
        item2.put("completed", false)
        dataArray.put(item2)

        json.put("data", dataArray)

        // Act
        val result = deserializeInterviews(json)

        // Assert
        assert(result.success)
        assert(result.data.size == 2)
        assert(result.data[0].id_vacancy == 1)
        assert(result.data[0].name == "Interview 1")
        assert(result.data[0].completed)
        assert(result.data[0].result == 42)
        assert(result.data[1].id_vacancy == 2)
        assert(result.data[1].name == "Interview 2")
        assert(!result.data[1].completed)
        assert(result.data[1].result == null)
    }
}
