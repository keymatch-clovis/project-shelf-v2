package com.example.project_shelf.adapter.presenter

import android.util.Log
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.app.use_case.product.CheckDefaultLoadedDataUseCase
import com.example.project_shelf.app.use_case.product.LoadDefaultCitiesUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import java.io.InputStream
import javax.inject.Inject

class CityPresenter @Inject constructor(
    private val loadDefaultCitiesUseCase: LoadDefaultCitiesUseCase,
    private val checkDefaultLoadedDataUseCase: CheckDefaultLoadedDataUseCase,
) : CityRepository {
    override suspend fun loadDefaultCities(stream: InputStream) {
        Log.d("CITY-PRESENTER", "Loading default cities")
        loadDefaultCitiesUseCase.exec(stream)
    }

    override suspend fun hasLoadedDefaultCities(): Boolean {
        Log.d("CITY-PRESENTER", "Checking if default cities loaded")
        return checkDefaultLoadedDataUseCase.exec()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class CityModule {
    @Binds
    abstract fun bindCityService(presenter: CityPresenter): CityRepository
}