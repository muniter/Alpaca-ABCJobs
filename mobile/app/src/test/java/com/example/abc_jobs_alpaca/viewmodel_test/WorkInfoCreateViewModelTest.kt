package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.SkillInfoType
import com.example.abc_jobs_alpaca.model.models.SkillInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoType
import com.example.abc_jobs_alpaca.model.models.TechnicalInfoTypeResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoItem
import com.example.abc_jobs_alpaca.model.models.WorkInfoItemResponse
import com.example.abc_jobs_alpaca.model.models.WorkInfoRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.WorkInfoCreateViewModel
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

class WorkInfoCreateViewModelTest {

    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    lateinit var workInfoCreateViewModel: WorkInfoCreateViewModel

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
        workInfoCreateViewModel =
            WorkInfoCreateViewModel(repositoryMock)
    }

    @Test
    fun testOnTokenUpdated() {
        workInfoCreateViewModel.onTokenUpdated(token)

        Assert.assertNotNull(workInfoCreateViewModel.token.value)
        Assert.assertEquals(token, workInfoCreateViewModel.token.value)
    }

    @Test
    fun testGetEnabledElementsLiveData() {
        val liveDataResponse = workInfoCreateViewModel.getEnabledElementsLiveData()

        Assert.assertNotNull(liveDataResponse)
    }

    @Test
    fun testSaveWorkInfoSuccess() {
        val workInfoRequest = WorkInfoRequest("test", "","", listOf(),1,1)

        runTest {
            launch(Dispatchers.Main) {
                workInfoCreateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.postWorkInfo(
                        token,
                        workInfoRequest
                    )
                ).thenReturn(
                    Result.success(
                        WorkInfoItemResponse(
                            true,
                            WorkInfoItem("", "", "", listOf(), 1, 1, 1, 1)
                        )
                    )
                )
                delay(300)
                workInfoCreateViewModel.saveWorkInfoItem(workInfoRequest)

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .postWorkInfo(
                        ArgumentMatchers.any(String::class.java) ?: "",
                        eq(workInfoRequest)
                    )
            }
        }
    }

    @Test
    fun testSaveWorkInfoFail() {
        val workInfoRequest = WorkInfoRequest("test", "","", listOf(),1,1)

        runTest {
            launch(Dispatchers.Main) {
                workInfoCreateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.postWorkInfo(
                        token,
                        workInfoRequest
                    )
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                workInfoCreateViewModel.saveWorkInfoItem(workInfoRequest)

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .postWorkInfo(
                        ArgumentMatchers.any(String::class.java) ?: "",
                        eq(workInfoRequest)
                    )
            }
        }
    }

    @Test
    fun testGetTypesSkills() {
        runTest {
            launch(Dispatchers.Main) {
                workInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(workInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTypesSkill(
                        token
                    )
                ).thenReturn(
                    Result.success(
                        SkillInfoTypeResponse(true, listOf())
                    )
                )
                delay(300)

                workInfoCreateViewModel.getTypesSkills()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTypesSkill(
                        token
                    )
            }
        }
    }

    @Test
    fun testGetTypesSkillsFailure() {
        runTest {
            launch(Dispatchers.Main) {
                workInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(workInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTypesSkill(
                        token
                    )
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                workInfoCreateViewModel.getTypesSkills()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTypesSkill(
                        token
                    )
            }
        }
    }

    @Test
    fun testGetTypesSkillsNullToken() {
        runTest {
            launch(Dispatchers.Main) {
                Assert.assertNull(workInfoCreateViewModel.token.value)

                workInfoCreateViewModel.getTypesSkills()

                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getTypesSkill(
                        ArgumentMatchers.any(String::class.java) ?: ""
                    )
            }
        }
    }

    @Test
    fun testGetIdTypesSkills() {
        runTest {
            launch(Dispatchers.Main) {
                workInfoCreateViewModel.onTokenUpdated(token)
                Assert.assertNotNull(workInfoCreateViewModel.token.value)

                Mockito.`when`(
                    repositoryMock.getTypesSkill(
                        token
                    )
                ).thenReturn(
                    Result.success(
                        SkillInfoTypeResponse(
                            true, listOf(
                                SkillInfoType(1, "a"),
                                SkillInfoType(2, "b"),
                                SkillInfoType(3, "c"),
                            )
                        )
                    )
                )
                delay(300)

                workInfoCreateViewModel.getTypesSkills()

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getTypesSkill(
                        token
                    )

                var id = workInfoCreateViewModel.getIdTypeSkill("c")

                Assert.assertEquals(3, id)
            }
        }
    }
}