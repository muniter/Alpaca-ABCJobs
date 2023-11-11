package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.CountriesResponse
import com.example.abc_jobs_alpaca.model.models.Country
import com.example.abc_jobs_alpaca.model.models.PersonalInfo
import com.example.abc_jobs_alpaca.model.models.PersonalInfoRequest
import com.example.abc_jobs_alpaca.model.models.PersonalInfoResponse
import com.example.abc_jobs_alpaca.model.models.UserLoginRequest
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.PersonalInfoViewModel
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
import java.time.LocalDate

class PersonalInfoViewModelTest {
    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    lateinit var personalInfoViewModel: PersonalInfoViewModel

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
        personalInfoViewModel =
            PersonalInfoViewModel(token, "dd/MM/yyyy", repositoryMock)
    }

    @Test
    fun testGetEnabledElementsLiveData() {
        val liveDataResponse = personalInfoViewModel.getEnabledElementsLiveData()

        Assert.assertNotNull(liveDataResponse)
    }

    @Test
    fun testGetCountriesSuccess() {
        runTest {
            launch(Dispatchers.Main) {
                Mockito.`when`(
                    repositoryMock.getCountries()
                ).thenReturn(
                    Result.success(
                        CountriesResponse(
                            true,
                            listOf(
                                Country(1, "CO", "CO", "COLOMBIA", "COLOMBIANO"),
                                Country(2, "EC", "EC", "ECUADOR", "ECUATORIANO"),
                            )
                        )
                    )
                )
                delay(300)

                personalInfoViewModel.loadData()
                delay(300)

                Assert.assertEquals(3, personalInfoViewModel.countrys.value?.size)
            }
        }
    }

    @Test
    fun testGetCountriesFail() {
        runTest {
            launch(Dispatchers.Main) {
                Mockito.`when`(
                    repositoryMock.getCountries()
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                personalInfoViewModel.loadData()
                delay(300)

                Assert.assertEquals(1, personalInfoViewModel.countrys.value?.size)

            }
        }
    }

    @Test
    fun testGetPersonalInfoSuccess() {
        runTest {
            val personalInfo = PersonalInfo("", "", "", null, null, null, null, null, null, null, null, null)

            launch(Dispatchers.Main) {
                Mockito.`when`(
                    repositoryMock.getPersonalInfo(token)
                ).thenReturn(
                    Result.success(
                        PersonalInfoResponse(
                            true,
                            personalInfo
                        )
                    )
                )
                delay(300)

                personalInfoViewModel.loadData()
                delay(300)

                Assert.assertEquals(personalInfo, personalInfoViewModel.personalInfo.value)
                Assert.assertFalse(personalInfoViewModel.showForm.value!!)
            }
        }
    }

    @Test
    fun testGetPersonalInfoSuccessShowForm() {
        runTest {
            val personalInfo = PersonalInfo("", "", "", null, LocalDate.of(2022,3,3), 123, null, null, null, null, null, null)

            launch(Dispatchers.Main) {
                Mockito.`when`(
                    repositoryMock.getPersonalInfo(token)
                ).thenReturn(
                    Result.success(
                        PersonalInfoResponse(
                            true,
                            personalInfo
                        )
                    )
                )
                delay(300)

                personalInfoViewModel.loadData()
                delay(300)

                Assert.assertEquals(personalInfo, personalInfoViewModel.personalInfo.value)
                Assert.assertTrue(personalInfoViewModel.showForm.value!!)
                Assert.assertEquals("03/03/2022", personalInfoViewModel.parsedDate.value)
            }
        }
    }

    @Test
    fun testGetPersonalInfoFail() {
        runTest {
            launch(Dispatchers.Main) {
                Mockito.`when`(
                    repositoryMock.getPersonalInfo(token)
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                personalInfoViewModel.loadData()
                delay(300)

                Assert.assertNull(personalInfoViewModel.personalInfo.value)

            }
        }
    }

    @Test
    fun testSavePersonalInfo() {
        runTest {
            val personalInfo = PersonalInfo("", "", "", null, null, null, null, null, null, null, null, null)
            val personalInfoRequest = PersonalInfoRequest(null, null, null, null, null, null, null)
            launch(Dispatchers.Main) {

                Mockito.`when`(
                    repositoryMock.postPersonalInfo(token, personalInfoRequest)
                ).thenReturn(
                    Result.success(
                        PersonalInfoResponse(true, personalInfo)
                    )
                )
                delay(300)

                personalInfoViewModel.savePersonalInfo(personalInfoRequest);
                delay(300)

                Mockito.verify(repositoryMock, Mockito.times(1)).postPersonalInfo(
                    token,
                    personalInfoRequest
                )
            }
        }
    }
}
