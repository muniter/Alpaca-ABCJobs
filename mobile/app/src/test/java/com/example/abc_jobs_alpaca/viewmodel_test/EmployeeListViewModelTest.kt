package com.example.abc_jobs_alpaca.viewmodel_test

import com.example.abc_jobs_alpaca.model.models.Employee
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.EmployeeListViewModel
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
import org.junit.Test
import com.example.abc_jobs_alpaca.model.models.EmployeesResponse

@ExperimentalCoroutinesApi
class EmployeeListViewModelTest {

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: EmployeeListViewModel
    private lateinit var repositoryMock: ABCJobsRepository
    val mockEmployee = mockk<Employee>()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repositoryMock = mockk()
        viewModel = EmployeeListViewModel(repositoryMock)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `loadEmployeeItems with valid token should update employeeList`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        val mockResponse = EmployeesResponse(success = true, data = listOf(mockEmployee))
        coEvery { repositoryMock.getHiredEmployees(mockToken) } returns Result.success(mockResponse)

        // Act
        viewModel.onTokenUpdated(mockToken)
        viewModel.loadEmployeeItems()

        // Assert
        assert(viewModel.token.value == mockToken)
        assert(viewModel.employeeList.value == mockResponse.data)
    }

    @Test
    fun `loadEmployeeItems with null token should not update employeeList`() = runBlockingTest {
        // Arrange
        coEvery { repositoryMock.getHiredEmployees(any()) } returns Result.success(EmployeesResponse(false, null))

        // Act
        viewModel.onTokenUpdated(null)
        viewModel.loadEmployeeItems()

        // Assert
        assert(viewModel.token.value == null)
        assert(viewModel.employeeList.value == null)
    }

    @Test
    fun `loadEmployeeItems with failure should not update employeeList`() = runBlockingTest {
        // Arrange
        val mockToken = "mockToken"
        coEvery { repositoryMock.getHiredEmployees(mockToken) } returns Result.failure(Exception())

        // Act
        viewModel.onTokenUpdated(mockToken)
        viewModel.loadEmployeeItems()

        // Assert
        assert(viewModel.token.value == mockToken)
        assert(viewModel.employeeList.value == null)
    }
}
