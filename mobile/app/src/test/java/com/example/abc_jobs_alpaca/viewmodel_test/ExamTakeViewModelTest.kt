package com.example.abc_jobs_alpaca.viewmodel_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.abc_jobs_alpaca.model.models.Answer
import com.example.abc_jobs_alpaca.model.models.AnswerQuestionResponse
import com.example.abc_jobs_alpaca.model.models.ExamStartResponse
import com.example.abc_jobs_alpaca.model.models.ExamsResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ExamTakeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ExamTakeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: ExamTakeViewModel
    private lateinit var repositoryMock: ABCJobsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repositoryMock = mockk()
        viewModel = ExamTakeViewModel(repositoryMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `postStartExam with valid token should update question, answers, and idResult`() =
        runBlockingTest {
            // Arrange
            val mockToken = "mockToken"
            val mockResponse = ExamsResponse(
                success = true,
                data = mockk()
            )
            val mockStartResponse = ExamStartResponse(
                success = true,
                data = mockk()
            )
            coEvery { repositoryMock.postExamStart(any(), any()) } returns Result.success(
                mockStartResponse
            )

            // Act
            viewModel.onTokenUpdated(mockToken)
            viewModel.postStartExam(1)

            // Assert
            assert(viewModel.token.value == mockToken)
        }

    @Test
    fun `postStartExam with failure should not update question, answers, and idResult`() =
        runBlockingTest {
            // Arrange
            val mockToken = "mockToken"
            coEvery {
                repositoryMock.postExamStart(
                    any(),
                    any()
                )
            } returns Result.failure(Exception())

            // Act
            viewModel.onTokenUpdated(mockToken)
            viewModel.postStartExam(1)

            // Assert
            assert(viewModel.token.value == mockToken)
            assert(viewModel.question.value == null)
            assert(viewModel.answers.value == null)
            assert(viewModel.idResult.value == null)
        }

    @Test
    fun `submitAnswer with valid data should update question and answers`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        val mockAnswer = mockk<Answer>()
        viewModel.onTokenUpdated(mockToken)
        viewModel.answers.value = listOf(mockAnswer)
        val mockAnswerQResponse = AnswerQuestionResponse(
            success = true,
            data = mockk()
        )
        coEvery { repositoryMock.postAnswerQuestion(any(), any(), any()) } returns Result.success(
            mockAnswerQResponse
        )

        // Act
        viewModel.submitAnswer(1)

        // Assert
        assert(viewModel.token.value == mockToken)
    }
}