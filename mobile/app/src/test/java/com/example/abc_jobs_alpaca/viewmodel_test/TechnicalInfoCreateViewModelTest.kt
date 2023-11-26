package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItem
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoRequest
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoType
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoTypeResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.TechnicalInfoCreateViewModel
import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
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
import org.mockito.kotlin.eq

class TechnicalInfoCreateViewModelTest {

    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    lateinit var technicalInfoCreateViewModel: TechnicalInfoCreateViewModel

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
        technicalInfoCreateViewModel =
            TechnicalInfoCreateViewModel(repositoryMock)
    }

    @Test
    fun testOnTokenUpdated() {
        technicalInfoCreateViewModel.onTokenUpdated(token)

        Assert.assertNotNull(technicalInfoCreateViewModel.token.value)
        Assert.assertEquals(token, technicalInfoCreateViewModel.token.value)
    }

    @Test
    fun testGetEnabledElementsLiveData() {
        val liveDataResponse = technicalInfoCreateViewModel.getEnabledElementsLiveData()

        Assert.assertNotNull(liveDataResponse)
    }

    @Test
    fun testSaveTechnicalInfoSuccess() {
        val technicalInfoRequest = TechnicalInfoRequest("test", 1)

        runTest {
            launch(Dispatchers.Main) {
                technicalInfoCreateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.postTechnicalInfo(
                        token,
                        technicalInfoRequest
                    )
                ).thenReturn(
                    Result.success(
                        TechnicalInfoItemResponse(
                            true,
                            TechnicalInfoItem("", 1, 1, TechnicalInfoType(1, "test"), 1)
                        )
                    )
                )
                delay(300)
                technicalInfoCreateViewModel.saveTechnicalInfoItem(technicalInfoRequest)

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .postTechnicalInfo(
                        ArgumentMatchers.any(String::class.java) ?: "",
                        eq(technicalInfoRequest)
                    )
            }
        }
    }

    @Test
    fun testSaveTechnicalInfoFail() {
        val technicalInfoRequest = TechnicalInfoRequest("test", 1)

        runTest {
            launch(Dispatchers.Main) {
                technicalInfoCreateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.postTechnicalInfo(
                        token,
                        technicalInfoRequest
                    )
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                technicalInfoCreateViewModel.saveTechnicalInfoItem(technicalInfoRequest)

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .postTechnicalInfo(
                        ArgumentMatchers.any(String::class.java) ?: "",
                        eq(technicalInfoRequest)
                    )
            }
        }
    }

    @Test
    fun testGetTypesTechnicalItems() {
        runTest {
            launch(Dispatchers.Main) {
                technicalInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(technicalInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTechnicalInfoTypes(
                        token
                    )
                ).thenReturn(
                    Result.success(
                        TechnicalInfoTypeResponse(true, listOf())
                    )
                )
                delay(300)

                technicalInfoCreateViewModel.getTypesTechnicalItems()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTechnicalInfoTypes(
                        token
                    )
            }
        }
    }

    @Test
    fun testGetTypesTechnicalItemsFailure() {
        runTest {
            launch(Dispatchers.Main) {
                technicalInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(technicalInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTechnicalInfoTypes(
                        token
                    )
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                technicalInfoCreateViewModel.getTypesTechnicalItems()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTechnicalInfoTypes(
                        token
                    )
            }
        }
    }

    @Test
    fun testGetTypesTechnicalItemsNullToken() {
        runTest {
            launch(Dispatchers.Main) {
                Assert.assertNull(technicalInfoCreateViewModel.token.value)

                technicalInfoCreateViewModel.getTypesTechnicalItems()

                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getTechnicalInfoTypes(
                        ArgumentMatchers.any(String::class.java) ?: ""
                    )
            }
        }
    }

    @Test
    fun testGetIdTypesTechnicalItems() {
        runTest {
            launch(Dispatchers.Main) {
                technicalInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(technicalInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTechnicalInfoTypes(
                        token
                    )
                ).thenReturn(
                    Result.success(
                        TechnicalInfoTypeResponse(
                            true, listOf(
                                TechnicalInfoType(1, "a"),
                                TechnicalInfoType(2, "b"),
                                TechnicalInfoType(3, "c"),
                            )
                        )
                    )
                )
                delay(300)

                technicalInfoCreateViewModel.getTypesTechnicalItems()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTechnicalInfoTypes(
                        token
                    )

                var id = technicalInfoCreateViewModel.getIdTypeTechnicalItem("c")

                Assert.assertEquals(3, id)
            }
        }
    }
}