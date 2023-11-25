package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.TeamItem
import com.example.abc_jobs_alpaca.model.models.VacanciesResponse
import com.example.abc_jobs_alpaca.model.models.VacancyItem
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.VacancyViewModel
import io.github.serpro69.kfaker.Faker
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

@ExperimentalCoroutinesApi
class VacancyViewModelTest {
    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var vacancyViewModel: VacancyViewModel

    private val looper = mockk<Looper> {
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
        vacancyViewModel = VacancyViewModel(repositoryMock)
    }

    @Test
    fun testOnTokenUpdated() {
        vacancyViewModel.onTokenUpdated(token)

        Assert.assertNotNull(vacancyViewModel.token.value)
        Assert.assertEquals(token, vacancyViewModel.token.value)
    }

    @Test
    fun testLoadVacancyItemsWithoutToken() {
        runTest {
            launch(Dispatchers.Main) {
                vacancyViewModel.loadVacancyItems()

                Assert.assertNull(vacancyViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getVacancies(
                        ArgumentMatchers.any(String::class.java) ?: ""
                    )
            }
        }
    }

    @Test
    fun testLoadVacancyItemsWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                val mockVacanciesResponse = VacanciesResponse(true,
                    listOf(
                        VacancyItem(
                            faker.random.nextInt(),
                            faker.company.profession(),
                            true,
                            faker.company.department(),
                            TeamItem(faker.random.nextInt(), faker.team.name()),
                            listOf()
                        ),
                        VacancyItem(
                            faker.random.nextInt(),
                            faker.company.profession(),
                            true,
                            faker.company.department(),
                            TeamItem(faker.random.nextInt(), faker.team.name()),
                            listOf()
                        )
                    )
                )
                vacancyViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getVacancies(token)
                ).thenReturn(
                    Result.success(mockVacanciesResponse)
                )

                vacancyViewModel.loadVacancyItems()

                Assert.assertNotNull(vacancyViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getVacancies(token)

                Assert.assertEquals(2, vacancyViewModel.vacancyList.value?.size)
                Assert.assertEquals(mockVacanciesResponse.data, vacancyViewModel.vacancyList.value)
            }
        }
    }

    @Test
    fun testFailLoadVacancyItemsWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                vacancyViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getVacancies(token)
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )

                vacancyViewModel.loadVacancyItems()

                Assert.assertNotNull(vacancyViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getVacancies(token)
            }
        }
    }

}