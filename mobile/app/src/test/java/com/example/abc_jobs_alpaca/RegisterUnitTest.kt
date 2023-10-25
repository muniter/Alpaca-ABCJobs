import com.example.abc_jobs_alpaca.CandidatoRegisterFragment

import org.junit.Test

import org.junit.Assert.*

class RegisterUnitTest {
    @Test
    fun testValidateName() {
        val candidatoFragment = CandidatoRegisterFragment()
        assertTrue(candidatoFragment.validateName("Maria"))
        assertFalse(candidatoFragment.validateName(""))
        assertFalse(candidatoFragment.validateName("J"))
        assertFalse(candidatoFragment.validateName("Este es un nombre largo que excede el límite máximo de 100 caracteres"))
    }

    @Test
    fun testValidateLastName() {
        val candidatoFragment = CandidatoRegisterFragment()
        assertTrue(candidatoFragment.validateLastName("Revert"))
        assertFalse(candidatoFragment.validateLastName(""))
        assertFalse(candidatoFragment.validateLastName("D"))
        assertFalse(candidatoFragment.validateLastName("Este es un apellido largo que excede el límite máximo de 100 caracteres"))
    }

    @Test
    fun testIsValidEmail() {
        val candidatoFragment = CandidatoRegisterFragment()
        assertFalse(candidatoFragment.isValidEmail("email"))
        assertTrue(candidatoFragment.isValidEmail("email@email.com"))
    }

    @Test
    fun testIsPasswordValid() {
        val candidatoFragment = CandidatoRegisterFragment()
        assertTrue(candidatoFragment.isPasswordValid("validPass"))
        assertFalse(candidatoFragment.isPasswordValid("short"))

    }

    @Test
    fun testAreStringsEqual() {
        val candidatoFragment = CandidatoRegisterFragment()
        assertTrue(candidatoFragment.areStringsEqual("abc", "abc"))
        assertFalse(candidatoFragment.areStringsEqual("abc", "def"))
    }
}