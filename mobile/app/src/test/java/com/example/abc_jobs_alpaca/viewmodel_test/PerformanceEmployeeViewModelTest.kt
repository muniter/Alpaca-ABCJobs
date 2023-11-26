package com.example.abc_jobs_alpaca.viewmodel_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.abc_jobs_alpaca.model.models.Employee
import com.example.abc_jobs_alpaca.model.models.EmployeePerformance
import com.example.abc_jobs_alpaca.model.models.EmployeeResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import com.example.abc_jobs_alpaca.viewmodel.PerformanceEmployeeViewModel
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
import java.util.Date

@ExperimentalCoroutinesApi
class PerformanceEmployeeViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: PerformanceEmployeeViewModel
    private lateinit var repositoryMock: ABCJobsRepository
    val mockEmployee = mockk<Employee>()


    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repositoryMock = mockk()
        viewModel = PerformanceEmployeeViewModel(repositoryMock, 1, "John Doe", 80)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `loadEmployeeData should update employeeItem`() = runBlockingTest {
        // Arrange
        val expectedEmployee = EmployeePerformance(1, "John Doe", 80)

        // Act
        viewModel.loadEmployeeData()

        // Assert
        assert(viewModel.employeeItem.value == expectedEmployee)
    }

    @Test
    fun `savePerformanceEmployee with valid data should update messageLiveData and isSaved`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        val mockResponse = EmployeeResponse(success = true, data = mockEmployee)
        coEvery { repositoryMock.postEvaluateEmployee(any(), any(), any()) } returns Result.success(mockResponse)

        // Act
        viewModel.savePerformanceEmployee(mockToken, 1, 90)

        // Assert
        assert(viewModel.messageLiveData.value == MessageEvent(MessageType.SUCCESS, "Ok"))
        assert(viewModel.isSaved.value == true)
    }

    @Test
    fun `savePerformanceEmployee with failure should not update isSaved`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        coEvery { repositoryMock.postEvaluateEmployee(any(), any(), any()) } returns Result.failure(Exception())

        // Act
        viewModel.savePerformanceEmployee(mockToken, 1, 90)

        // Assert
        assert(viewModel.isSaved.value == false)
    }
}
