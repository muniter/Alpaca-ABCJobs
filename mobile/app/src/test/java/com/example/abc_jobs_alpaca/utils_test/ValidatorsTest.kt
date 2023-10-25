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
    }

    @Test
    fun testAreStringsEqual(){
        val str1 = faker.howToTrainYourDragon.unique.dragons();
        val str2 = faker.howToTrainYourDragon.unique.dragons();

        assertTrue(Validators.areStringsEqual(str1, str1));
        assertFalse(Validators.areStringsEqual(str1, str2));
    }
}
