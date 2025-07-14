package com.example.project_shelf.adapter.presenter

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.example.project_shelf.adapter.dto.ui.CityDto
import com.example.project_shelf.adapter.dto.ui.toDto
import com.example.project_shelf.adapter.repository.CityRepository
import com.example.project_shelf.app.use_case.city.CheckDefaultLoadedDataUseCase
import com.example.project_shelf.app.use_case.city.LoadDefaultCitiesUseCase
import com.example.project_shelf.app.use_case.city.SearchCitiesUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.InputStream
import javax.inject.Inject

class CityPresenter @Inject constructor(
    private val loadDefaultCitiesUseCase: LoadDefaultCitiesUseCase,
    private val checkDefaultLoadedDataUseCase: CheckDefaultLoadedDataUseCase,
    private val searchCitiesUseCase: SearchCitiesUseCase,
) : CityRepository {
    override suspend fun loadDefaultCities(stream: InputStream) {
        Log.d("PRESENTER", "Loading default cities")
        loadDefaultCitiesUseCase.exec(stream)
    }

    override suspend fun hasLoadedDefaultCities(): Boolean {
        Log.d("PRESENTER", "Checking if default cities loaded")
        return checkDefaultLoadedDataUseCase.exec()
    }

    override fun search(value: String): Flow<PagingData<CityDto>> {
        Log.d("PRESENTER", "Searching cities with: $value")
        return searchCitiesUseCase.exec(value).map {
            it.map { entity -> entity.toDto() }
        }
    }
}

@Module
@InstallIn(ViewModelComponent::class)
abstract class CityModule {
    @Binds
    abstract fun bindCityService(presenter: CityPresenter): CityRepository
}