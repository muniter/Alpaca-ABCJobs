package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.ShortlistedCandidateItem
import com.example.abc_jobs_alpaca.model.models.TeamItem
import com.example.abc_jobs_alpaca.model.models.VacancyItem
import com.example.abc_jobs_alpaca.model.models.VacancyResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ShortlistedCandidateViewModel
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
class ShortlistedCandidateViewModelTest {
    private val token = "123123abc"

    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var shortlistedCandidateViewModel: ShortlistedCandidateViewModel

    private val looper = mockk<Looper> {
        every { thread } returns Thread.currentThread()
    }

    @Mock
    lateinit var repositoryMock: ABCJobsRepository
    private val vacancyId = faker.random.nextInt()

    @Before
    fun setUp() {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns looper
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        shortlistedCandidateViewModel = ShortlistedCandidateViewModel(repositoryMock, vacancyId)
    }

    @Test
    fun testOnTokenUpdated() {
        shortlistedCandidateViewModel.onTokenUpdated(token)

        Assert.assertNotNull(shortlistedCandidateViewModel.token.value)
        Assert.assertEquals(token, shortlistedCandidateViewModel.token.value)
    }

    @Test
    fun testLoadShortlistedCandidatesWithoutToken() {
        runTest {
            launch(Dispatchers.Main) {
                shortlistedCandidateViewModel.loadShortlistedCandidateItems()

                Assert.assertNull(shortlistedCandidateViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(0))
                    .getVacancy(
                        ArgumentMatchers.any(String::class.java) ?: "",
                        ArgumentMatchers.any(Int::class.java) ?: 0
                    )
            }
        }
    }

    @Test
    fun testLoadShortlistedCandidatesWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                val mockVacancyResponse = VacancyResponse(true,
                    VacancyItem(
                        vacancyId,
                        faker.company.profession(),
                        faker.random.nextBoolean(),
                        faker.company.department(),
                        TeamItem(faker.random.nextInt(), faker.team.name()),
                        listOf(
                            ShortlistedCandidateItem(
                                faker.random.nextInt(),
                                faker.random.nextInt(),
                                faker.name.firstName(),
                                faker.name.lastName(),
                                faker.name.name(),
                                faker.internet.email(),
                                "",
                                faker.random.nextInt(),
                                faker.address.country(),
                                faker.address.city(),
                                faker.address.fullAddress(),
                                faker.phoneNumber.phoneNumber(),
                                faker.name.neutralFirstName(),
                                listOf(),
                                faker.random.nextInt(0,100)
                            ),
                            ShortlistedCandidateItem(
                                faker.random.nextInt(),
                                faker.random.nextInt(),
                                faker.name.firstName(),
                                faker.name.lastName(),
                                faker.name.name(),
                                faker.internet.email(),
                                "",
                                faker.random.nextInt(),
                                faker.address.country(),
                                faker.address.city(),
                                faker.address.fullAddress(),
                                faker.phoneNumber.phoneNumber(),
                                faker.name.neutralFirstName(),
                                listOf(),
                                faker.random.nextInt(0,100)
                            )
                        )
                    )
                )
                shortlistedCandidateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getVacancy(token, vacancyId)
                ).thenReturn(
                    Result.success(mockVacancyResponse)
                )

                shortlistedCandidateViewModel.loadShortlistedCandidateItems()

                Assert.assertNotNull(shortlistedCandidateViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getVacancy(token, vacancyId)

                Assert.assertEquals(
                    2,
                    shortlistedCandidateViewModel.shortlistedCandidateList.value?.size
                )
                Assert.assertEquals(
                    mockVacancyResponse.data!!.preselection,
                    shortlistedCandidateViewModel.shortlistedCandidateList.value
                )
            }
        }
    }

    @Test
    fun testFailLoadShortlistedCandidatesWithToken() {
        runTest {
            launch(Dispatchers.Main) {
                shortlistedCandidateViewModel.onTokenUpdated(token)

                Mockito.`when`(
                    repositoryMock.getVacancy(token, vacancyId)
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )

                shortlistedCandidateViewModel.loadShortlistedCandidateItems()

                Assert.assertNotNull(shortlistedCandidateViewModel.token.value)
                Mockito.verify(repositoryMock, Mockito.times(1))
                    .getVacancy(token, vacancyId)
            }
        }
    }
}