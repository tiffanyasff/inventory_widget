package com.univalle.inventorywidget.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.inventorywidget.model.UserRequest
import com.univalle.inventorywidget.model.UserResponse
import com.univalle.inventorywidget.repository.LoginRepository
import com.univalle.inventorywidget.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: LoginRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setup() {
        repository = mock(LoginRepository::class.java)
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun `loginUser calls callback with result`() = runTest {
        // Given
        val email = "test@test.com"
        val pass = "123456"
        `when`(repository.loginUser(email, pass)).thenReturn(true)

        var resultCaptured = false

        // When
        viewModel.loginUser(email, pass) { success ->
            resultCaptured = success
        }

        // Then
        verify(repository).loginUser(email, pass)
        assertEquals(true, resultCaptured)
    }

    @Test
    fun `registerUser updates isRegister LiveData`() = runTest {
        // Given
        val userReq = UserRequest("new@test.com", "pass123")
        val expectedResponse = UserResponse(isRegister = true, message = "Success", email = "new@test.com")
        `when`(repository.registerUser(userReq.email, userReq.password)).thenReturn(expectedResponse)

        // When
        viewModel.registerUser(userReq)

        // Then
        verify(repository).registerUser(userReq.email, userReq.password)
        assertEquals(expectedResponse, viewModel.isRegister.value)
    }
}