package com.example.project_shelf

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.project_shelf.adapter.dto.room.ProductDto
import com.example.project_shelf.framework.room.ShelfDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductsLoaderTest {
    @Test
    fun loadsProducts() = runBlocking {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val room = ShelfDatabase.getInstance(appContext)

        room.database.productDao().insert(
            ProductDto(name = "Test", uid = 0, price = "1234", count = 1)
        )
    }
}