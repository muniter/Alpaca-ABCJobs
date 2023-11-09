package com.example.abc_jobs_alpaca.viewmodel_test

import android.app.Application
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.abc_jobs_alpaca.model.models.ConfigData
import com.example.abc_jobs_alpaca.model.models.ConfigRequest
import com.example.abc_jobs_alpaca.model.models.UserDateFormat
import com.example.abc_jobs_alpaca.model.models.UserLanguageApp
import com.example.abc_jobs_alpaca.model.models.UserLoginRequest
import com.example.abc_jobs_alpaca.model.models.UserTimeFormat
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.PreferencesViewModel
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
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
import org.mockito.kotlin.eq

class PreferencesViewModelTest {
    private val faker = Faker()
    private val testDispatcher = newSingleThreadContext("ThreadPreferencesVM")

    lateinit var preferencesViewModel: PreferencesViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var log: Log

    @Mock
    lateinit var applicationMock: Application

    @Mock
    lateinit var repositoryMock: ABCJobsRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        preferencesViewModel = PreferencesViewModel(applicationMock, repositoryMock)
    }

    @Test
    fun testSuccessVMCreation() {
        runTest {
            launch(Dispatchers.Main) {
                Assert.assertNotNull(preferencesViewModel)
            }
        }
    }

    @Test
    fun testSaveAfterOnTokenUpdated() {
        runTest {
            launch(Dispatchers.Main) {
                Mockito.`when`(repositoryMock.postConfig(eq("123456"), ArgumentMatchers.any(ConfigRequest::class.java) ?: ConfigRequest(UserLanguageApp.EN, UserDateFormat.AAAAMMDDSLASH, UserTimeFormat.FORMATOHORAS12))).
                    thenReturn(
                        Result.success(
                            ConfigData(UserLanguageApp.EN, UserTimeFormat.FORMATOHORAS12, UserDateFormat.AAAAMMDDSLASH)
                        )
                    )

                preferencesViewModel.onTokenUpdated("123456")

                preferencesViewModel.save()

                Mockito.verify(repositoryMock, Mockito.times(1)).postConfig(eq("123456"), ArgumentMatchers.any(ConfigRequest::class.java) ?: ConfigRequest(UserLanguageApp.EN, UserDateFormat.AAAAMMDDSLASH, UserTimeFormat.FORMATOHORAS12))
            }
        }
    }

    @Test
    fun testSaveAfterOnTokenUpdatedError() {
        runTest {
            launch(Dispatchers.Main) {
                Mockito.`when`(repositoryMock.postConfig(eq("123456"), ArgumentMatchers.any(ConfigRequest::class.java) ?: ConfigRequest(UserLanguageApp.EN, UserDateFormat.AAAAMMDDSLASH, UserTimeFormat.FORMATOHORAS12))).
                thenReturn(
                    Result.failure(
                        Exception()
                    )
                )

                preferencesViewModel.onTokenUpdated("123456")

                preferencesViewModel.save()

                Mockito.verify(repositoryMock, Mockito.times(1)).postConfig(eq("123456"), ArgumentMatchers.any(ConfigRequest::class.java) ?: ConfigRequest(UserLanguageApp.EN, UserDateFormat.AAAAMMDDSLASH, UserTimeFormat.FORMATOHORAS12))
            }
        }
    }
}

