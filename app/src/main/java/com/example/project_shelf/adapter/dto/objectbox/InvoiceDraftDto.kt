package com.example.project_shelf.adapter.dto.objectbox

import com.example.project_shelf.app.entity.InvoiceDraft
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import java.util.Date

@Entity
data class InvoiceDraftDto(
    @Id var id: Long = 0,
    var date: Date = Date(),
    var remainingUnpaidBalance: Long = 0,
    var customerId: Long? = null,
) {
    // Auto-creates a one-to-many relation based on the ToOne in Note.
    // https://docs.objectbox.io/relations#to-many-relations
    @Backlink(to = "invoiceDraft")
    lateinit var products: ToMany<InvoiceDraftProductDto>

    fun toEntity(): InvoiceDraft = InvoiceDraft(
        date = this.date,
        remainingUnpaidBalance = this.remainingUnpaidBalance,
        products = this.products.map { it.toEntity() },
        customerId = this.customerId,
    )
}