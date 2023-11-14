package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoViewModel
import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class TechnicalInfoViewModelTest {
    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    lateinit var technicalInfoViewModel: TechnicalInfoViewModel

    val looper = mockk<Looper> {
        every { thread } returns Thread.currentThread()
    }

    @Mock
    lateinit var repositoryMock: ABCJobsRepository

    @Before
    fun setUp() {
        mockkStatic(Looper::class)

        every { Looper.getMainLooper() } returns looper

        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        technicalInfoViewModel =
            TechnicalInfoViewModel(repositoryMock)
    }

    @Test
    fun testOnTokenUpdated() {
        technicalInfoViewModel.onTokenUpdated(token)

        Assert.assertNotNull(technicalInfoViewModel.token.value)
        Assert.assertEquals(token, technicalInfoViewModel.token.value)
    }

    @Test
    fun testLoadTechnicalItemsInfoWithoutToken() {
        runTest {
            launch(Dispatchers.Main) {
                technicalInfoViewModel.loadTechnicalItemsInfo()

                Assert.assertNull(technicalInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getTechnicalInfo(
                        ArgumentMatchers.any(String::class.java) ?: ""
                    )
            }
        }
    }

    @Test
    fun testLoadTechnicalInfoWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                technicalInfoViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getTechnicalInfo(token)
                ).thenReturn(
                    Result.success(
                        TechnicalInfoResponse(true, listOf())
                    )
                )

                technicalInfoViewModel.loadTechnicalItemsInfo()

                Assert.assertNotNull(technicalInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTechnicalInfo(
                        token
                    )

                Assert.assertEquals(0, technicalInfoViewModel.technicalInfoList.value?.size)
            }
        }
    }

    @Test
    fun testFailLoadTechnicalInfoWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                technicalInfoViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getTechnicalInfo(token)
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )

                technicalInfoViewModel.loadTechnicalItemsInfo()

                Assert.assertNotNull(technicalInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTechnicalInfo(
                        token
                    )
            }
        }
    }
}