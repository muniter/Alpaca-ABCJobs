package com.example.abc_jobs_alpaca.viewmodel_test

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
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
import org.mockito.MockitoAnnotations

class PreferencesViewModelTest {
    private val faker = Faker()
    private val testDispatcher = newSingleThreadContext("ThreadPreferencesVM")

    lateinit var preferencesViewModel: PreferencesViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        preferencesViewModel = PreferencesViewModel()
    }

    @Test
    fun testSuccessVMCreation() {
        runTest {
            launch(Dispatchers.Main) {
                Assert.assertNotNull(preferencesViewModel)
            }
        }
    }
}

