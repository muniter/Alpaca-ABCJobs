package com.example.abc_jobs_alpaca.viewmodel_test

import android.app.Application
import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.android.volley.NetworkError
import com.example.abc_jobs_alpaca.model.models.ConfigData
import com.example.abc_jobs_alpaca.model.models.User
import com.example.abc_jobs_alpaca.model.models.UserData
import com.example.abc_jobs_alpaca.model.models.UserDateFormat
import com.example.abc_jobs_alpaca.model.models.UserLanguageApp
import com.example.abc_jobs_alpaca.model.models.UserLoginRequest
import com.example.abc_jobs_alpaca.model.models.UserLoginResponse
import com.example.abc_jobs_alpaca.model.models.UserTimeFormat
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.utils.MessageEvent
import com.example.abc_jobs_alpaca.utils.MessageType
import com.example.abc_jobs_alpaca.viewmodel.LoginViewModel
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
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
import java.text.DateFormat

class LoginViewModelTest {
    private val faker = Faker()
    private val testDispatcher = newSingleThreadContext("ThreadLogin")

    lateinit var loginViewModel: LoginViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var repositoryMock: ABCJobsRepository

    @Mock
    lateinit var applicationMock: Application

    @Mock
    lateinit var sharedPrefs: SharedPreferences

    @Mock
    lateinit var editor: SharedPreferences.Editor

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        Mockito.`when`(sharedPrefs.edit()).thenReturn(editor)

        Mockito.`when`(
            applicationMock.getSharedPreferences("AppPreferences", 0)
        ).thenReturn(
            sharedPrefs
        )

        loginViewModel =
            LoginViewModel(applicationMock, repositoryMock)
    }

    @Test
    fun testSuccessLogin() {
        runTest {
            launch(Dispatchers.Main) {
                val email = faker.internet.email()
                val pass = faker.random.randomString(8)
                val token = faker.random.randomString(28)

                val userData = UserData(
                    User(1, email, 1),
                    token
                )

                Mockito.`when`(
                    repositoryMock.getConfig(token)
                ).thenReturn(
                    Result.success(
                        ConfigData(UserLanguageApp.EN, UserTimeFormat.FORMATOHORAS12, UserDateFormat.AAAAMMDDSLASH)
                    )
                )
                delay(300)

                Mockito.`when`(
                    repositoryMock.postLoginUser(
                        ArgumentMatchers.any(UserLoginRequest::class.java)
                            ?: UserLoginRequest(email, pass)
                    )
                ).thenReturn(
                    Result.success(
                        UserLoginResponse(
                            true,
                            userData
                        )
                    )
                )
                delay(300)

                loginViewModel.login(email, pass)

                Mockito.verify(repositoryMock, Mockito.times(1)).getConfig(token)
                delay(300)

                Mockito.verify(repositoryMock, Mockito.times(1)).postLoginUser(
                    ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(
                        email,
                        pass
                    )
                )
                delay(300)

                Assert.assertEquals(
                    MessageType.SUCCESS,
                    (loginViewModel.getMessageLiveData().value as MessageEvent).type
                )
                Assert.assertEquals(
                    userData,
                    (loginViewModel.getMessageLiveData().value as MessageEvent).content
                )
            }
        }
    }

    @Test
    fun testNetworkErrorLogin() {
        runTest {
            launch(Dispatchers.Main) {
                val email = faker.internet.email()
                val pass = faker.random.randomString(8)

                Mockito.`when`(
                    repositoryMock.postLoginUser(
                        ArgumentMatchers.any(UserLoginRequest::class.java)
                            ?: UserLoginRequest(email, pass)
                    )
                ).thenReturn(
                    Result.failure(
                        NetworkError()
                    )
                )
                delay(300)

                loginViewModel.login(email, pass)

                Mockito.verify(repositoryMock, Mockito.times(1)).postLoginUser(
                    ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(
                        email,
                        pass
                    )
                )
                delay(300)

                Assert.assertEquals(
                    MessageType.ERROR,
                    (loginViewModel.getMessageLiveData().value as MessageEvent).type
                )

            }
        }
    }

    @Test
    fun testExceptionLogin() {
        runTest {
            launch(Dispatchers.Main) {
                val errorMessage = "mocked error"
                val email = faker.internet.email()
                val pass = faker.random.randomString(8)

                Mockito.`when`(
                    repositoryMock.postLoginUser(
                        ArgumentMatchers.any(UserLoginRequest::class.java)
                            ?: UserLoginRequest(email, pass)
                    )
                ).thenReturn(
                    Result.failure(
                        Exception(errorMessage)
                    )
                )
                delay(300)

                loginViewModel.login(email, pass)

                Mockito.verify(repositoryMock, Mockito.times(1)).postLoginUser(
                    ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(
                        email,
                        pass
                    )
                )
                delay(300)

                Assert.assertEquals(
                    MessageType.ERROR,
                    (loginViewModel.getMessageLiveData().value as MessageEvent).type
                )
                Assert.assertEquals(
                    errorMessage,
                    (loginViewModel.getMessageLiveData().value as MessageEvent).content
                )
            }
        }
    }

    @Test
    fun testExceptionWithoutMessageLogin() {
        runTest {
            launch(Dispatchers.Main) {
                val email = faker.internet.email()
                val pass = faker.random.randomString(8)

                Mockito.`when`(
                    repositoryMock.postLoginUser(
                        ArgumentMatchers.any(UserLoginRequest::class.java)
                            ?: UserLoginRequest(email, pass)
                    )
                ).thenReturn(
                    Result.failure(
                        Exception()
                    )
                )
                delay(300)

                loginViewModel.login(email, pass)

                Mockito.verify(repositoryMock, Mockito.times(1)).postLoginUser(
                    ArgumentMatchers.any(UserLoginRequest::class.java) ?: UserLoginRequest(
                        email,
                        pass
                    )
                )
                delay(300)

                Assert.assertEquals(
                    MessageType.ERROR,
                    (loginViewModel.getMessageLiveData().value as MessageEvent).type
                )
                Assert.assertEquals(
                    "",
                    (loginViewModel.getMessageLiveData().value as MessageEvent).content
                )
            }
        }
    }

    @Test
    fun testGetMessageLiveData() {
        val liveDataResponse = loginViewModel.getMessageLiveData()

        Assert.assertNotNull(liveDataResponse)
    }
}