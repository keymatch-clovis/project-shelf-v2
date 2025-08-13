package com.example.project_shelf.adapter.dto.objectbox

import com.example.project_shelf.app.entity.InvoiceDraftProduct
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.util.Locale

@Entity
data class InvoiceDraftProductDto(
    @Id var id: Long = 0,
    var productId: Long = 0,
    var count: Int = 0,
    var price: Long = 0,
) {
    // To-one relation to an Author Object.
    // https://docs.objectbox.io/relations#to-one-relations
    lateinit var invoiceDraft: ToOne<InvoiceDraftDto>

    fun toEntity(): InvoiceDraftProduct = InvoiceDraftProduct(
        count = this.count,
        price = Money.ofMinor(CurrencyUnit.of(Locale.getDefault()), this.price),
        productId = this.productId,
    )
}