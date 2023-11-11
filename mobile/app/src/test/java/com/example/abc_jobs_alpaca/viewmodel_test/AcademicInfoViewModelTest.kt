package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.AcademicInfoItem
import com.example.abc_jobs_alpaca.model.models.AcademicInfoResponse
import com.example.abc_jobs_alpaca.model.models.PersonalInfoResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.AcademicInfoViewModel
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

class AcademicInfoViewModelTest {

    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    lateinit var academicInfoViewModel: AcademicInfoViewModel

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
        academicInfoViewModel =
            AcademicInfoViewModel(repositoryMock)
    }

    @Test
    fun testOnTokenUpdated() {
        academicInfoViewModel.onTokenUpdated(token)

        Assert.assertNotNull(academicInfoViewModel.token.value)
        Assert.assertEquals(token, academicInfoViewModel.token.value)
    }

    @Test
    fun testLoadAcademicInfoWithoutToken() {
        runTest {
            launch(Dispatchers.Main) {
                academicInfoViewModel.loadAcademicItemsInfo()

                Assert.assertNull(academicInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getAcademicInfo(
                        ArgumentMatchers.any(String::class.java) ?: ""
                    )
            }
        }
    }

    @Test
    fun testLoadAcademicInfoWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                academicInfoViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getAcademicInfo(token)
                ).thenReturn(
                    Result.success(
                        AcademicInfoResponse(true, listOf())
                    )
                )

                academicInfoViewModel.loadAcademicItemsInfo()

                Assert.assertNotNull(academicInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getAcademicInfo(
                        token
                    )

                Assert.assertEquals(0, academicInfoViewModel.academicInfoList.value?.size)
            }
        }
    }

    @Test
    fun testFailLoadAcademicInfoWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                academicInfoViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getAcademicInfo(token)
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )

                academicInfoViewModel.loadAcademicItemsInfo()

                Assert.assertNotNull(academicInfoViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getAcademicInfo(
                        token
                    )
            }
        }
    }
}