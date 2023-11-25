package com.example.abc_jobs_alpaca.viewmodel_test

import android.os.Looper
import com.example.abc_jobs_alpaca.model.models.TeamItem
import com.example.abc_jobs_alpaca.model.models.TechnicalProofRequest
import com.example.abc_jobs_alpaca.model.models.VacancyItem
import com.example.abc_jobs_alpaca.model.models.VacancyResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.TechnicalProofViewModel
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
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class TechnicalProofViewModelTest {
    private val faker = Faker()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var technicalProofViewModel: TechnicalProofViewModel
    private val looper = mockk<Looper> {
        every { thread } returns Thread.currentThread()
    }
    @Mock
    lateinit var repositoryMock: ABCJobsRepository
    private val token = faker.random.nextUUID()
    private val vacancyId = faker.random.nextInt()
    private val fullName = faker.name.name()
    private val country = faker.address.country()
    private val city = faker.address.city()
    private val result = faker.random.nextInt(1,100)
    private val candidateId = faker.random.nextInt()
    private val newResult = faker.random.nextInt(1,100)

    @Before
    fun setUp() {
        mockkStatic(Looper::class)
        every { Looper.getMainLooper() } returns looper
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        technicalProofViewModel = TechnicalProofViewModel(
            repositoryMock, fullName, country, city, result
        )
    }

    @Test
    fun testLoadTechnicalProofData() {
        runTest {
            launch(Dispatchers.Main) {
                technicalProofViewModel.loadTechnicalProofData()

                Assert.assertEquals(
                    result,
                    technicalProofViewModel.shortlistedCandidateItem.value!!.result
                )
            }
        }
    }

    @Test
    fun testSaveTechnicalProofResult() {
        runTest {
            launch(Dispatchers.Main) {
                val request = ArrayList<TechnicalProofRequest>()
                request.add(TechnicalProofRequest(candidateId,newResult))
                val mockVacancyResponse = VacancyResponse(
                    true,
                    VacancyItem(
                        vacancyId,
                        faker.company.profession(),
                        faker.random.nextBoolean(),
                        faker.company.department(),
                        TeamItem(faker.random.nextInt(), faker.team.name()),
                        listOf()
                    )
                )

                Mockito.`when`(
                    repositoryMock.postTestResult(token, vacancyId, request)
                ).thenReturn(
                    Result.success(mockVacancyResponse)
                )

                technicalProofViewModel.saveTechnicalProofResult(
                    token, vacancyId, request
                )

                Mockito.verify(repositoryMock, Mockito.times(1))
                    .postTestResult(token, vacancyId, request)

                Assert.assertEquals(
                    mockVacancyResponse.data?.id,
                    vacancyId
                )
            }
        }
    }
}