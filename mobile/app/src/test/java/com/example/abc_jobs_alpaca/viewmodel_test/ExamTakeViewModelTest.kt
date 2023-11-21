import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.abc_jobs_alpaca.model.models.Answer
import com.example.abc_jobs_alpaca.model.models.AnswerQuestionResponse
import com.example.abc_jobs_alpaca.model.models.ExamStartData
import com.example.abc_jobs_alpaca.model.models.ExamStartResponse
import com.example.abc_jobs_alpaca.model.models.Question
import com.example.abc_jobs_alpaca.model.models.deserializerAnswers
import com.example.abc_jobs_alpaca.model.repository.ABCJobsRepository
import com.example.abc_jobs_alpaca.viewmodel.ExamTakeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ExamTakeViewModelTest {

    private val testDispatcher = newSingleThreadContext("ThreadRegister")

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockRepository: ABCJobsRepository

    private lateinit var viewModel: ExamTakeViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ExamTakeViewModel(mockRepository)
    }

    @Test
    fun `postStartExam should update question, idResult, and answers LiveData when repository call is successful`() {
        runTest {
            launch(Dispatchers.Main) {
                val mockToken = "mockToken"
                val mockIdExam = 1
                val mockResponse = createMockExamStartResponse()

                `when`(
                    mockRepository.postExamStart(
                        mockToken,
                        mockIdExam
                    )
                ).thenReturn(Result.success(mockResponse))

                viewModel.onTokenUpdated(mockToken)
                viewModel.postStartExam(mockIdExam)

                assertEquals(mockResponse.data.next_question, viewModel.question.value)
                assertEquals(mockResponse.data.id_result, viewModel.idResult.value)
                assertEquals(
                    mockResponse.data.next_question?.answers?.let { deserializerAnswers(it) },
                    viewModel.answers.value
                )
            }
        }
    }

    @Test
    fun `postStartExam should handle repository call failure`() {
        runTest {
            launch(Dispatchers.Main) {
                val mockToken = "mockToken"
                val mockIdExam = 1

                `when`(
                    mockRepository.postExamStart(
                        mockToken,
                        mockIdExam
                    )
                ).thenReturn(Result.failure(Exception("Repository error")))

                viewModel.onTokenUpdated(mockToken)
                viewModel.postStartExam(mockIdExam)

                assertNull(viewModel.question.value)
                assertNull(viewModel.idResult.value)
                assertNull(viewModel.answers.value)
            }
        }
    }

    @Test
    fun `submitAnswer should update question and answers LiveData when repository call is successful`() {
        runTest {
            launch(Dispatchers.Main) {
                // Arrange
                val mockToken = "mockToken"
                val mockIdAnswer = 1
                val mockResponse = createMockAnswerQuestionResponse()
                val mockAnswers = createMockAnswers();

                `when`(
                    mockRepository.postAnswerQuestion(
                        mockToken,
                        mockResponse.data.id_result,
                        mockAnswers[0]
                    )
                )
                    .thenReturn(Result.success(mockResponse))

                viewModel.onTokenUpdated(mockToken)
                viewModel.idResult.value = mockResponse.data.id_result
                viewModel.answers.value = mockAnswers

                viewModel.submitAnswer(mockIdAnswer)

                assertEquals(mockResponse.data.next_question, viewModel.question.value)
                assertEquals(
                    mockResponse.data.next_question?.answers?.let { deserializerAnswers(it) },
                    viewModel.answers.value
                )
            }

        }
    }


    @Test
    fun `submitAnswer should handle repository call failure`() {
        runTest {
            launch(Dispatchers.Main) {
                val mockToken = "mockToken"
                val mockIdAnswer = 1
                val mockAnswers = createMockAnswers()

                `when`(mockRepository.postAnswerQuestion(mockToken, 1, mockAnswers[0]))
                    .thenReturn(Result.failure(Exception("Repository error")))

                viewModel.onTokenUpdated(mockToken)
                viewModel.idResult.value = 1
                viewModel.answers.value = mockAnswers

                try{
                    viewModel.submitAnswer(mockIdAnswer)
                } catch (e: Exception){
                    Assert.fail("should not throw exception")
                }
            }
        }
    }

    private fun createMockExamStartResponse(): ExamStartResponse {
        return ExamStartResponse(
            success = true,
            data = ExamStartData(
                id_result = 1,
                id_exam = 1,
                next_question = Question(
                    id = 1,
                    id_exam = 1,
                    question = "Mock question",
                    difficulty = 1,
                    answers = null
                ),
                result = null
            )
        )
    }

    private fun createMockAnswerQuestionResponse(): AnswerQuestionResponse {
        return AnswerQuestionResponse(
            success = true,
            data = ExamStartData(
                id_result = 2,
                id_exam = 1,
                next_question = Question(
                    id = 2,
                    id_exam = 1,
                    question = "Another mock question",
                    difficulty = 1,
                    answers = null
                ),
                result = null
            )
        )
    }

    private fun createMockAnswer(): Answer {
        return Answer(id = 1, id_question = 1, answer = "Mock answer")
    }

    private fun createMockAnswers(): List<Answer> {
        return listOf(
            Answer(id = 1, id_question = 1, answer = "Mock answer 1"),
            Answer(id = 2, id_question = 1, answer = "Mock answer 2")
        )
    }
}
