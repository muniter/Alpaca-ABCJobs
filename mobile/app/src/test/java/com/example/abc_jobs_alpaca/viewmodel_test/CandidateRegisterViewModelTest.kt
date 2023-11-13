package com.example.abc_jobs_alpaca.viewmodel_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.volley.NetworkError
import com.example.abc_jobs_alpaca.model.models.CandidatoData
import com.example.abc_jobs_alpaca.model.models.UserDataResponse
import com.example.abc_jobs_alpaca.model.models.UserRegisterRequest
import com.example.abc_jobs_alpaca.model.models.UserRegisterResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import com.example.abc_jobs_alpaca.viewmodel.CandidateRegisterViewModel
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class CandidateRegisterViewModelTest {
    private val faker = Faker()
    private val testDispatcher = newSingleThreadContext("ThreadRegister")
    lateinit var candidateRegisterViewModel: CandidateRegisterViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var repositoryMock: ABCJobsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        Dispatchers.setMain(testDispatcher)

        candidateRegisterViewModel =
            CandidateRegisterViewModel(repositoryMock)
    }

    @Test
    fun testSuccessPostCandidate() {
        runTest {
            launch(Dispatchers.Main) {

                val name = faker.name.firstName()
                val lastName = faker.name.lastName()
                val email = faker.internet.email()
                val pass = faker.random.randomString(8)
                val token = faker.random.randomString(28)

                val newCandidate = UserRegisterRequest(
                    name,
                    lastName,
                    email,
                    pass
                )

                val newCandidateResponse = UserRegisterResponse(
                    true,
                    UserDataResponse(
                        CandidatoData(
                            1,
                            name,
                            lastName,
                            email,
                        ),
                        token
                    )
                )

                Mockito.`when`(repositoryMock.postCandidate(newCandidate)).thenReturn(
                    Result.success(newCandidateResponse)
                )
                delay(400)

                candidateRegisterViewModel.postCandidate(
                    newCandidate
                )
                delay(400)

                Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)

                Assert.assertEquals(
                    MessageType.SUCCESS,
                    (candidateRegisterViewModel.getMessageLiveData().value as MessageEvent).type
                )
                Assert.assertEquals(
                    newCandidateResponse.data,
                    (candidateRegisterViewModel.getMessageLiveData().value as MessageEvent).content
                )
            }
        }
    }

    @Test
    fun testNetworkErrorPostCandidate() {
        runTest {
            launch(Dispatchers.Main) {
                val name = faker.name.firstName()
                val lastName = faker.name.lastName()
                val email = faker.internet.email()
                val pass = faker.random.randomString(8)

                val newCandidate = UserRegisterRequest(
                    name,
                    lastName,
                    email,
                    pass
                )

                Mockito.`when`(repositoryMock.postCandidate(newCandidate)).thenReturn(
                    Result.failure(
                        NetworkError()
                    )
                )
                delay(400)

                candidateRegisterViewModel.postCandidate(
                    newCandidate
                )

                Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)
                delay(400)

                Assert.assertEquals(
                    MessageType.ERROR,
                    (candidateRegisterViewModel.getMessageLiveData().value as MessageEvent).type
                )
            }
        }
    }

    @Test
    fun testExceptionPostCandidate() {
        runTest {
            launch(Dispatchers.Main) {
                val errorMessage = "mocked error"

                val name = faker.name.firstName()
                val lastName = faker.name.lastName()
                val email = faker.internet.email()
                val pass = faker.random.randomString(9)

                val newCandidate = UserRegisterRequest(
                    name,
                    lastName,
                    email,
                    pass
                )


                Mockito.`when`(repositoryMock.postCandidate(newCandidate)).thenReturn(
                    Result.failure(
                        Exception(errorMessage)
                    )
                )
                delay(400)

                candidateRegisterViewModel.postCandidate(
                    newCandidate
                )

                Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)
                delay(400)

                Assert.assertEquals(
                    MessageType.ERROR,
                    (candidateRegisterViewModel.getMessageLiveData().value as MessageEvent).type
                )
            }
        }
    }

    @Test
    fun testExceptionWithoutMessagePostCandidate() {
        runTest {
            launch(Dispatchers.Main) {
                val name = faker.name.firstName()
                val lastName = faker.name.lastName()
                val email = faker.internet.email()
                val pass = faker.random.randomString(8)

                val newCandidate = UserRegisterRequest(
                    name,
                    lastName,
                    email,
                    pass
                )


                Mockito.`when`(repositoryMock.postCandidate(newCandidate)).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(400)

                candidateRegisterViewModel.postCandidate(
                    newCandidate
                )

                Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)
                delay(400)

                Assert.assertEquals(
                    MessageType.ERROR,
                    (candidateRegisterViewModel.getMessageLiveData().value as MessageEvent).type
                )
                Assert.assertEquals(
                    "",
                    (candidateRegisterViewModel.getMessageLiveData().value as MessageEvent).content
                )
            }
        }
    }

    @Test
    fun testGetMessageLiveData() {
        val candidateRegisterViewModel =
            CandidateRegisterViewModel(repositoryMock)

        val liveDataResponse = candidateRegisterViewModel.getMessageLiveData()

        Assert.assertNotNull(liveDataResponse)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        testDispatcher.close()
    }
}