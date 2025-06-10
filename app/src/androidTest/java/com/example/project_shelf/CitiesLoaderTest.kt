package com.example.project_shelf

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.project_shelf.adapter.service_impl.CityServiceImpl
import com.example.project_shelf.framework.room.ShelfDatabase
import com.example.project_shelf.lib.use_case.LoadDefaultCitiesUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CitiesLoaderTest {
    @Test
    fun loadsCities() = runBlocking {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val citiesData = appContext.resources.openRawResource(R.raw.departments_cities)
        val room = ShelfDatabase.getInstance(appContext)

        LoadDefaultCitiesUseCase(
            cityService = CityServiceImpl(room.database.cityDao())
        ).exec(citiesData)
    }
}