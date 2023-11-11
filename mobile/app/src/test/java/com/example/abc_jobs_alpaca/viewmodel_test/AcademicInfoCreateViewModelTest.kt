package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItem
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.AcademicInfoRequest
import com.example.abc_jobs_alpaca.model.models.AcademicInfoType
import com.example.abc_jobs_alpaca.model.models.AcademicInfoTypeResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoCreateViewModel
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

class AcademicInfoCreateViewModelTest {

    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    lateinit var academicInfoCreateViewModel: AcademicInfoCreateViewModel

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
        academicInfoCreateViewModel =
            AcademicInfoCreateViewModel(repositoryMock)
    }

    @Test
    fun testOnTokenUpdated() {
        academicInfoCreateViewModel.onTokenUpdated(token)

        Assert.assertNotNull(academicInfoCreateViewModel.token.value)
        Assert.assertEquals(token, academicInfoCreateViewModel.token.value)
    }

    @Test
    fun testGetEnabledElementsLiveData() {
        val liveDataResponse = academicInfoCreateViewModel.getEnabledElementsLiveData()

        Assert.assertNotNull(liveDataResponse)
    }

    @Test
    fun testGetYears() {
        val years = academicInfoCreateViewModel.getYears()

        Assert.assertNotNull(years)
        Assert.assertEquals(25, years.size)
    }

    @Test
    fun testSaveAcademicInfoSuccess() {
        val academicInfoRequest = AcademicInfoRequest("", "", 11, 11, "", 1)

        runTest {
            launch(Dispatchers.Main) {
                academicInfoCreateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.postAcademicInfo(
                        token,
                        academicInfoRequest
                    )
                ).thenReturn(
                    Result.success(
                        AcademicInfoItemResponse(
                            true,
                            AcademicInfoItem("", "", 11, 11, "", 1, 1, AcademicInfoType(1, ""))
                        )
                    )
                )
                delay(300)
                academicInfoCreateViewModel.saveAcademicInfoItem(academicInfoRequest)

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .postAcademicInfo(
                        ArgumentMatchers.any(String::class.java) ?: "",
                        eq(academicInfoRequest)
                    )
            }
        }
    }

    @Test
    fun testSaveAcademicInfoFail() {
        val academicInfoRequest = AcademicInfoRequest("", "", 11, 11, "", 1)

        runTest {
            launch(Dispatchers.Main) {
                academicInfoCreateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.postAcademicInfo(
                        token,
                        academicInfoRequest
                    )
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                academicInfoCreateViewModel.saveAcademicInfoItem(academicInfoRequest)

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .postAcademicInfo(
                        ArgumentMatchers.any(String::class.java) ?: "",
                        eq(academicInfoRequest)
                    )
            }
        }
    }

    @Test
    fun testGetTypesDegree() {
        runTest {
            launch(Dispatchers.Main) {
                academicInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(academicInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTypeTitles(
                        token
                    )
                ).thenReturn(
                    Result.success(
                        AcademicInfoTypeResponse(true, listOf())
                    )
                )
                delay(300)

                academicInfoCreateViewModel.getTypesDegree()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTypeTitles(
                        token
                    )
            }
        }
    }

    @Test
    fun testGetTypesDegreeFailure() {
        runTest {
            launch(Dispatchers.Main) {
                academicInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(academicInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTypeTitles(
                        token
                    )
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                academicInfoCreateViewModel.getTypesDegree()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTypeTitles(
                        token
                    )
            }
        }
    }

    @Test
    fun testGetTypesDegreeNullToken() {
        runTest {
            launch(Dispatchers.Main) {
                Assert.assertNull(academicInfoCreateViewModel.token.value)

                academicInfoCreateViewModel.getTypesDegree()

                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getTypeTitles(
                        ArgumentMatchers.any(String::class.java) ?: ""
                    )
            }
        }
    }

    @Test
    fun testGetIdTypeDegree() {
        runTest {
            launch(Dispatchers.Main) {
                academicInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(academicInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTypeTitles(
                        token
                    )
                ).thenReturn(
                    Result.success(
                        AcademicInfoTypeResponse(true, listOf(
                            AcademicInfoType(1,"a"),
                            AcademicInfoType(2,"b"),
                            AcademicInfoType(3,"c"),
                        ))
                    )
                )
                delay(300)

                academicInfoCreateViewModel.getTypesDegree()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTypeTitles(
                        token
                    )

                var id = academicInfoCreateViewModel.getIdTypeDegree("c")

                Assert.assertEquals(3, id)
            }
        }
    }
}