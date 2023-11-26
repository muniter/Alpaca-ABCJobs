package com.example.abc_jobs_alpaca.viewmodel_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.abc_jobs_alpaca.model.models.Team
import com.example.abc_jobs_alpaca.model.models.TeamsResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.TeamListViewModel
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
class TeamListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: TeamListViewModel
    private lateinit var repositoryMock: ABCJobsRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repositoryMock = mockk()
        viewModel = TeamListViewModel(repositoryMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `loadTeams with valid token should update teamList`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        val mockResponse = TeamsResponse(success = true, data = listOf(mockk<Team>()))
        coEvery { repositoryMock.getAllTeams(mockToken) } returns Result.success(mockResponse)

        // Act
        viewModel.onTokenUpdated(mockToken)
        viewModel.loadTeams()

        // Assert
        assert(viewModel.token.value == mockToken)
        assert(viewModel.teamList.value == mockResponse.data)
    }

    @Test
    fun `loadTeams with null token should not update teamList`() = runBlockingTest {
        // Arrange
        coEvery { repositoryMock.getAllTeams(any()) } returns Result.success(TeamsResponse(false, null))

        // Act
        viewModel.onTokenUpdated(null)
        viewModel.loadTeams()

        // Assert
        assert(viewModel.token.value == null)
        assert(viewModel.teamList.value == null)
    }

    @Test
    fun `loadTeams with failure should not update teamList`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        coEvery { repositoryMock.getAllTeams(mockToken) } returns Result.failure(Exception())

        // Act
        viewModel.onTokenUpdated(mockToken)
        viewModel.loadTeams()

        // Assert
        assert(viewModel.token.value == mockToken)
        assert(viewModel.teamList.value == null)
    }
}
