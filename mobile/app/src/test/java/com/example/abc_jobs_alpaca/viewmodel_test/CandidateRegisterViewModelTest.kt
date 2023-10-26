package com.example.abc_jobs_alpaca.viewmodel_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class CandidateRegisterViewModelTest {
    private val faker = Faker()
    private val myScope = GlobalScope
    private val testDispatcher = TestCoroutineDispatcher()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var repositoryMock: ABCJobsRepository

    @Mock
    lateinit var mutableLiveDataMock: MutableLiveData<MessageEvent>

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        Dispatchers.setMain(testDispatcher)

    }

    @Test
    fun testExceptionPostCandidate() = runTest {
        val errorMessage = "mocked error"

        val candidateRegisterViewModel =
            CandidateRegisterViewModel(repositoryMock, mutableLiveDataMock)
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

        candidateRegisterViewModel.postCandidate(
            newCandidate
        )

        Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)
        Mockito.verify(mutableLiveDataMock, times(1))
            .postValue(MessageEvent(MessageType.ERROR, errorMessage))
    }

    @Test
    fun testSuccessPostCandidate() = runTest {

        val candidateRegisterViewModel =
            CandidateRegisterViewModel(repositoryMock, mutableLiveDataMock)
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

        Mockito.`when`(repositoryMock.postCandidate(newCandidate)).thenReturn(
            Result.success(
                UserRegisterResponse(
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
            )
        )

        candidateRegisterViewModel.postCandidate(
            newCandidate
        )

        Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)
        Mockito.verify(mutableLiveDataMock, times(1)).postValue(
            MessageEvent(
                MessageType.SUCCESS,
                ArgumentMatchers.any(Object::class.java) ?: Object()
            )
        )
    }

    @Test
    fun testNetworkErrorPostCandidate() = runTest {

        val candidateRegisterViewModel =
            CandidateRegisterViewModel(repositoryMock, mutableLiveDataMock)
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

        candidateRegisterViewModel.postCandidate(
            newCandidate
        )

        Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)
        Mockito.verify(mutableLiveDataMock, times(1)).postValue(
            MessageEvent(
                MessageType.ERROR,
                ArgumentMatchers.any(Object::class.java) ?: Object()
            )
        )
    }

    @Test
    fun testExceptionWithoutMessagePostCandidate() = runTest {

        val candidateRegisterViewModel =
            CandidateRegisterViewModel(repositoryMock, mutableLiveDataMock)
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

        candidateRegisterViewModel.postCandidate(
            newCandidate
        )

        Mockito.verify(repositoryMock, times(1)).postCandidate(newCandidate)
        Mockito.verify(mutableLiveDataMock, times(1))
            .postValue(MessageEvent(MessageType.ERROR, ""))
    }

    @Test
    fun testGetMessageLiveData() {
        val candidateRegisterViewModel =
            CandidateRegisterViewModel(repositoryMock, mutableLiveDataMock)

        val liveDataResponse = candidateRegisterViewModel.getMessageLiveData()

        Assert.assertEquals(mutableLiveDataMock, liveDataResponse)
    }

    @After
    fun after() {
        Dispatchers.resetMain()
        Mockito.reset(mutableLiveDataMock)
        Mockito.reset(repositoryMock)
    }
}