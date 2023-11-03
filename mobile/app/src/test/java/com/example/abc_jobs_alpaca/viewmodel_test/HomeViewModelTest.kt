package com.example.abc_jobs_alpaca.viewmodel_test

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.abc_jobs_alpaca.viewmodel.HomeViewModel
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
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class HomeViewModelTest {
    private val faker = Faker()
    private val testDispatcher = newSingleThreadContext("homeVM")

    lateinit var homeViewModel: HomeViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        homeViewModel = HomeViewModel(Mockito.mock(Application::class.java))
    }

    @Test
    fun testSuccessVMCreation() {
        runTest {
            launch(Dispatchers.Main) {
                Assert.assertNotNull(homeViewModel)
            }
        }
    }
}