package com.example.abc_jobs_alpaca.viewmodel_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.abc_jobs_alpaca.model.models.CandidatoData
import com.example.abc_jobs_alpaca.model.models.User
import com.example.abc_jobs_alpaca.model.models.UserData
import com.example.abc_jobs_alpaca.model.models.UserDataResponse
import com.example.abc_jobs_alpaca.model.models.UserLoginRequest
import com.example.abc_jobs_alpaca.model.models.UserLoginResponse
import com.example.abc_jobs_alpaca.model.models.UserRegisterRequest
import com.example.abc_jobs_alpaca.model.models.UserRegisterResponse
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import com.example.abc_jobs_alpaca.viewmodel.CandidateRegisterViewModel
import com.example.abc_jobs_alpaca.viewmodel.LoginViewModel
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class LoginViewModelTest {
    private val faker = Faker()
    private val myScope = GlobalScope
    private val testDispatcher = TestCoroutineDispatcher()

    lateinit var loginViewModel: LoginViewModel

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

        loginViewModel =
            LoginViewModel(repositoryMock, mutableLiveDataMock)
    }

    @Test
    fun testSuccessLogin() = runTest {
            val email = faker.internet.email()
            val pass = faker.random.randomString(8)
            val token = faker.random.randomString(28)

            Mockito.`when`(repositoryMock.postLoginUser(ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(email, pass))).thenReturn(
                Result.success(
                    UserLoginResponse(
                        true,
                        UserData(
                            User(1, email, 1),
                            token
                        )
                    )
                )
            )

            loginViewModel.login(email, pass)

            Mockito.verify(repositoryMock, Mockito.times(1)).postLoginUser(ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(email, pass))
            Mockito.verify(mutableLiveDataMock, Mockito.times(1)).postValue(
                MessageEvent(
                    MessageType.SUCCESS,
                    ArgumentMatchers.any(Object::class.java) ?: Object()
                )
            );

    }

    @Test
    fun testSuccessLogin2() = runTest {
        val email = faker.internet.email()
        val pass = faker.random.randomString(8)
        val token = faker.random.randomString(28)

        Mockito.`when`(repositoryMock.postLoginUser(ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(email, pass))).thenReturn(
            Result.success(
                UserLoginResponse(
                    true,
                    UserData(
                        User(1, email, 1),
                        token
                    )
                )
            )
        )

        loginViewModel.login(email, pass)

        Mockito.verify(repositoryMock, Mockito.times(1)).postLoginUser(ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(email, pass))
        Mockito.verify(mutableLiveDataMock, Mockito.times(1)).postValue(
            MessageEvent(
                MessageType.SUCCESS,
                ArgumentMatchers.any(Object::class.java) ?: Object()
            )
        );

    }

    @Test
    fun testGetMessageLiveData() {
        val liveDataResponse = loginViewModel.getMessageLiveData();

        Assert.assertEquals(mutableLiveDataMock, liveDataResponse)
    }
}