package com.example.project_shelf.app.use_case.debug

import android.util.Log
import com.example.project_shelf.app.service.ProductService
import com.example.project_shelf.app.service.model.CreateProductInput
import javax.inject.Inject
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

class LoadProductsUseCase @Inject constructor(private val service: ProductService) {
    // https://medium.com/androiddevelopers/coroutines-on-android-part-i-getting-the-background-3e0e54d20bb
    suspend fun exec() = withContext(Dispatchers.IO) {
        Log.d("USE-CASE", "[DEBUG] Loading products")
        val faker = Faker()

        // FIXME: I know this is incorrect. But I can't think of a better way of doing this.
        val names = mutableSetOf<String>()
        repeat(100) {
            names.add(faker.minecraft.items())
        }

        // NOTE: We don't need to do the money converting thing we are doing in
        //  `CreateProductUseCase` here, as this is just test data.
        names
            .map {
                CreateProductInput(
                    name = it.uppercase(),
                    defaultPrice = Random.nextLong(0, 1_000_000L),
                    stock = Random.nextInt(0, 100),
                )
            }
            .let {
                service.create(it)
            }
    }
}