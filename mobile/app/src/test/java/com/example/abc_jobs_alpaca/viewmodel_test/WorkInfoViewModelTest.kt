package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.WorkInfoResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.WorkInfoViewModel
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

class WorkInfoViewModelTest {
    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    lateinit var workInfoViewModel: WorkInfoViewModel

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
        workInfoViewModel = WorkInfoViewModel(repositoryMock)
    }

    @Test
    fun testOnTokenUpdated() {
        workInfoViewModel.onTokenUpdated(token)

        Assert.assertNotNull(workInfoViewModel.token.value)
        Assert.assertEquals(token, workInfoViewModel.token.value)
    }

    @Test
    fun testLoadWorkInfoWithoutToken() {
        runTest {
            launch(Dispatchers.Main) {
                workInfoViewModel.loadWorkItemsInfo()

                Assert.assertNull(workInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getWorkInfo(
                        ArgumentMatchers.any(String::class.java) ?: ""
                    )
            }
        }
    }

    @Test
    fun testLoadWorkInfoWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                workInfoViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getWorkInfo(token)
                ).thenReturn(
                    Result.success(
                        WorkInfoResponse(true, listOf())
                    )
                )

                workInfoViewModel.loadWorkItemsInfo()

                Assert.assertNotNull(workInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getWorkInfo(
                        token
                    )

                Assert.assertEquals(0, workInfoViewModel.workInfoList.value?.size)
            }
        }
    }

    @Test
    fun testFailLoadWorkInfoWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                workInfoViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getWorkInfo(token)
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )

                workInfoViewModel.loadWorkItemsInfo()

                Assert.assertNotNull(workInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getWorkInfo(
                        token
                    )
            }
        }
    }
}