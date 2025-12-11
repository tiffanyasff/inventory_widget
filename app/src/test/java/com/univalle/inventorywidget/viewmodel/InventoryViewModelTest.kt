package com.univalle.inventorywidget.viewmodel

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.univalle.inventorywidget.model.Inventory
import com.univalle.inventorywidget.model.Product
import com.univalle.inventorywidget.repository.InventoryRepository
import com.univalle.inventorywidget.repository.LoginRepository
import com.univalle.inventorywidget.utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any

@ExperimentalCoroutinesApi
class InventoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var inventoryRepository: InventoryRepository
    private lateinit var loginRepository: LoginRepository
    private lateinit var viewModel: InventoryViewModel
    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setup() {
        mockedLog = mockStatic(Log::class.java)
        inventoryRepository = mock(InventoryRepository::class.java)
        loginRepository = mock(LoginRepository::class.java)
        viewModel = InventoryViewModel(inventoryRepository, loginRepository)
    }

    @After
    fun tearDown() {
        mockedLog.close()
    }

    @Test
    fun `totalProducto calculates correctly`() {
        val price = 100
        val qty = 5
        val expected = 500.0

        val result = viewModel.totalProducto(price, qty)

        assertEquals(expected, result, 0.0)
    }

    @Test
    fun `getListInventory updates listInventory LiveData on success`() = runTest {
        // Given
        val mockList = mutableListOf(
            Inventory(id = 1, name = "Item 1", price = 100, quantity = 2),
            Inventory(id = 2, name = "Item 2", price = 200, quantity = 1)
        )
        `when`(inventoryRepository.getListInventory()).thenReturn(mockList)

        // When
        viewModel.getListInventory()

        // Then
        // Wait for coroutine if needed, but UnconfinedTestDispatcher usually runs immediately
        verify(inventoryRepository).getListInventory()
        assertEquals(mockList, viewModel.listInventory.value)
        assertFalse(viewModel.progresState.value ?: true)
    }

    @Test
    fun `saveInventory calls repository and updates progress`() = runTest {
        // Given
        val newItem = Inventory(name = "New Item", price = 50, quantity = 10)

        // When
        viewModel.saveInventory(newItem)

        // Then
        verify(inventoryRepository).saveInventory(newItem)
        assertFalse(viewModel.progresState.value ?: true)
    }

    @Test
    fun `deleteProduct success calls repository and reloads products`() = runTest {
        // Given
        val product = Product(id = "prod123", name = "Prod 1", price = 100, quantity = 5)
        `when`(inventoryRepository.deleteProduct("prod123")).thenReturn(true)
        // Mock getProducts call which happens after delete
        `when`(inventoryRepository.getProducts()).thenReturn(mutableListOf())

        // When
        viewModel.deleteProduct(product)

        // Then
        verify(inventoryRepository).deleteProduct("prod123")
        verify(inventoryRepository).getProducts() // Should reload
    }

    @Test
    fun `deleteProduct with empty ID does not call repository`() = runTest {
        // Given
        val product = Product(id = "", name = "Invalid", price = 0, quantity = 0)

        // When
        viewModel.deleteProduct(product)

        // Then
        verify(inventoryRepository, org.mockito.Mockito.never()).deleteProduct(any())
        assertFalse(viewModel.progresState.value ?: true)
    }
}