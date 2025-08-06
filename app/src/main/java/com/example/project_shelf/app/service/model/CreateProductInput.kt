package com.example.project_shelf.app.service.model

// Input Model used by Clean Architecture.
// https://medium.com/%40DrunknCode/clean-architecture-simplified-and-in-depth-guide-026333c54454
data class CreateProductInput(
    val name: String,
    val price: Long,
    val stock: Int,
)
