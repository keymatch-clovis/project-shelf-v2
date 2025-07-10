package com.example.project_shelf.adapter.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.project_shelf.app.use_case.product.DeletePendingForDeletionUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeleteProductsMarkedForDeletionWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val deleteMarkedForDeletionUseCase: DeletePendingForDeletionUseCase
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        Log.d("WORK", "Deleting products marked for deletion.")
        deleteMarkedForDeletionUseCase.exec()

        return Result.success()
    }
}