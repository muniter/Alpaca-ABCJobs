import com.example.abc_jobs_alpaca.utils.Validators

import org.junit.Test

import org.junit.Assert.*

class RegisterUnitTest {
    @Test
    fun testValidateName() {
        val validator = Validators()
        assertTrue(validator.validateName("Maria"))
        assertFalse(validator.validateName(""))
        assertFalse(validator.validateName("J"))
        assertFalse(validator.validateName("Este es un nombre largo que excede el límite máximo de 100 caracteres"))
    }

    @Test
    fun testValidateLastName() {
        val validator = Validators()
        assertTrue(validator.validateLastName("Revert"))
        assertFalse(validator.validateLastName(""))
        assertFalse(validator.validateLastName("D"))
        assertFalse(validator.validateLastName("Este es un apellido largo que excede el límite máximo de 100 caracteres"))
    }

    @Test
    fun testIsValidEmail() {
        val validator = Validators()
        assertFalse(validator.isValidEmail("email"))
        assertTrue(validator.isValidEmail("email@email.com"))
    }

    @Test
    fun testIsPasswordValid() {
        val validator = Validators()
        assertTrue(validator.isPasswordValid("validPass"))
        assertFalse(validator.isPasswordValid("short"))

    }

    @Test
    fun testAreStringsEqual() {
        val validator = Validators()
        assertTrue(validator.areStringsEqual("abc", "abc"))
        assertFalse(validator.areStringsEqual("abc", "def"))
    }
}