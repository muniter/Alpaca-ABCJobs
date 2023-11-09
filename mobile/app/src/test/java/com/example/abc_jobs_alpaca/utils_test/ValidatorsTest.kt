package com.example.abc_jobs_alpaca.utils_test

import com.example.abc_jobs_alpaca.utils.Validators
import io.github.serpro69.kfaker.Faker
import org.junit.Test

import org.junit.Assert.*

class ValidatorsTest {
    private val faker = Faker()

    @Test
    fun testValidateName() {
        assertTrue(Validators.validateName(faker.name.firstName()))
        assertFalse(Validators.validateName(""))
        assertFalse(Validators.validateName(faker.random.randomString(1)))
        assertFalse(Validators.validateName(faker.random.randomString(101)))
    }

    @Test
    fun testValidateLastName() {
        assertTrue(Validators.validateLastName(faker.name.lastName()))
        assertFalse(Validators.validateLastName(""))
        assertFalse(Validators.validateLastName(faker.random.randomString(1)))
        assertFalse(Validators.validateLastName(faker.random.randomString(101)))
    }

    @Test
    fun testIsValidEmail() {
        assertFalse(Validators.isValidEmail(faker.name.lastName()))
        assertTrue(Validators.isValidEmail(faker.internet.safeEmail()))
    }

    @Test
    fun testIsPasswordValid() {
        assertTrue(Validators.isPasswordValid(faker.random.randomString(8)))
        assertFalse(Validators.isPasswordValid(faker.random.randomString(7)))
        assertFalse(Validators.isPasswordValid(faker.random.randomString(256)))
    }

    @Test
    fun testAreStringsEqual(){
        val str1 = faker.howToTrainYourDragon.unique.dragons();
        val str2 = faker.howToTrainYourDragon.unique.dragons();

        assertTrue(Validators.areStringsEqual(str1, str1));
        assertFalse(Validators.areStringsEqual(str1, str2));
    }

    @Test
    fun testIsNonEmpty() {
        assertTrue(Validators.isNotEmpty(faker.random.randomString(8)))
        assertFalse(Validators.isNotEmpty(""))
    }

    @Test
    fun testCompareTwoNumbers() {
        assertTrue(Validators.compareTwoNumbers(1,2))
        assertFalse(Validators.compareTwoNumbers(11,2))
        assertFalse(Validators.compareTwoNumbers(2,2))
        assertFalse(Validators.compareTwoNumbers(null,2))
    }

    @Test
    fun testIsValidCity() {
        assertTrue(Validators.isValidCity(""))
        assertFalse(Validators.isValidCity(faker.random.randomString(4)))
        assertTrue(Validators.isValidCity(faker.random.randomString(5)))
        assertTrue(Validators.isValidCity(faker.random.randomString(255)))
        assertFalse(Validators.isValidCity(faker.random.randomString(256)))
    }

    @Test
    fun testIsValidAddress() {
        assertTrue(Validators.isValidAddress(""))
        assertFalse(Validators.isValidAddress(faker.random.randomString(4)))
        assertTrue(Validators.isValidAddress(faker.random.randomString(5)))
        assertTrue(Validators.isValidAddress(faker.random.randomString(255)))
        assertFalse(Validators.isValidAddress(faker.random.randomString(256)))
    }

    @Test
    fun testIsValidPhone() {
        assertTrue(Validators.isValidPhone(""))
        assertFalse(Validators.isValidPhone(faker.random.randomString(1)))
        assertTrue(Validators.isValidPhone(faker.random.randomString(2)))
        assertTrue(Validators.isValidPhone(faker.random.randomString(15)))
        assertFalse(Validators.isValidPhone(faker.random.randomString(16)))
    }

    @Test
    fun testIsValidBio() {
        assertTrue(Validators.isValidBio(""))
        assertFalse(Validators.isValidBio(faker.random.randomString(9)))
        assertTrue(Validators.isValidBio(faker.random.randomString(10)))
        assertTrue(Validators.isValidBio(faker.random.randomString(255)))
        assertFalse(Validators.isValidBio(faker.random.randomString(256)))
    }

    @Test
    fun testIsValidBirthDate() {
        assertFalse(Validators.isValidBirthDate("11/11/202321"))
        assertTrue(Validators.isValidBirthDate("11/11/2021"))
        assertTrue(Validators.isValidBirthDate("2011/11/21"))
        assertFalse(Validators.isValidBirthDate("11/2021"))
    }
}
