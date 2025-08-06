package com.example.project_shelf.adapter.view_model.invoice

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.project_shelf.adapter.dto.ui.InvoiceDraftDto
import com.example.project_shelf.adapter.view_model.util.Consumable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel()
class InvoiceDraftViewModel @Inject constructor() : ViewModel() {
    // NOTE: We could use a nullable property here and call it a day, but I feel using the
    //  `Consumable` util class makes it more explicit--We want a property that is readable once.
    var currentDraft by mutableStateOf(Consumable<InvoiceDraftDto>(null))
        private set

    fun setCurrentDraft(dto: InvoiceDraftDto?) {
        currentDraft = Consumable(dto)
    }
}