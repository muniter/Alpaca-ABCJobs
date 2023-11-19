package com.example.abc_jobs_alpaca.viewmodel_test

import com.example.abc_jobs_alpaca.model.models.SkillInfoType
import com.example.abc_jobs_alpaca.viewmodel.ExamListViewModel


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.abc_jobs_alpaca.model.models.ExamItem
import com.example.abc_jobs_alpaca.model.models.ExamItemExtend
import com.example.abc_jobs_alpaca.model.models.ExamsExtendResponse
import com.example.abc_jobs_alpaca.model.models.ExamsResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ExamListViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepository: ABCJobsRepository

    private lateinit var viewModel: ExamListViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        viewModel = ExamListViewModel(mockRepository)
    }

    @Test
    fun `loadExams should update exams LiveData when repository call is successful`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        val mockExamsResponse = ExamsResponse(success = true, data = listOf(
            ExamItem(1, SkillInfoType(1, "Skill 1"), true, 10),
            ExamItem(2, SkillInfoType(2, "Skill 2"), false, 15)
        ))

        `when`(mockRepository.getAllExams(mockToken)).thenReturn(Result.success(mockExamsResponse))

        viewModel.onTokenUpdated(mockToken)
        viewModel.loadExams()

        assertEquals(mockExamsResponse.data, viewModel.exams.value)
    }

    @Test
    fun `loadExamsResult should update examsResult LiveData when repository call is successful`() =
        runBlockingTest {
            val mockToken = "mockToken"
            val mockExamsExtendResponse = ExamsExtendResponse(success = true, data = listOf(
                ExamItemExtend(1, ExamItem(1, SkillInfoType(1, "Skill 1"), true, 10), 1, 90, true),
                ExamItemExtend(2, ExamItem(2, SkillInfoType(2, "Skill 2"), false, 15), 2, 80, true)
            ))

            `when`(mockRepository.getAllExamsResult(mockToken)).thenReturn(Result.success(mockExamsExtendResponse))

            viewModel.onTokenUpdated(mockToken)
            viewModel.loadExamsResult()

            assertEquals(mockExamsExtendResponse.data, viewModel.examsResult.value)
        }


    @Test
    fun `loadExams should handle repository call failure`() = runBlockingTest {
        val mockToken = "mockToken"

        `when`(mockRepository.getAllExams(mockToken)).thenReturn(Result.failure(Exception("Repository error")))

        viewModel.onTokenUpdated(mockToken)
        viewModel.loadExams()

        assertNull(viewModel.exams.value)
    }

    @Test
    fun `loadExamsResult should handle repository call failure`() = runBlockingTest {
        val mockToken = "mockToken"

        `when`(mockRepository.getAllExamsResult(mockToken)).thenReturn(Result.failure(Exception("Repository error")))

        viewModel.onTokenUpdated(mockToken)
        viewModel.loadExamsResult()

        assertNull(viewModel.examsResult.value)
    }

    @Test
    fun `allExamsResult should handle null values`() {
        // Arrange
        val exams = MutableLiveData<List<ExamItem>?>()
        val examsResult = MutableLiveData<List<ExamItemExtend>?>()

        // Act
        viewModel.allExamsResult(exams, examsResult)

        // Assert
        assertEquals(emptyList<ExamItemExtend>(), viewModel.examsResultMapped.value)
    }


}
