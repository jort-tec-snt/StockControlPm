package com.jort.stockcontrolpm.domain.model

data class Movement(
    val id: Long = 0,
    val productId: Long,
    val productName: String,  // snapshot para mostrar en historial
    val type: MovementType,
    val qty: Int,             // siempre positivo; el tipo define dirección
    val reason: String,
    val date: Long,
    val userId: String = "",
    val notes: String? = null
)

enum class MovementType(val label: String) {
    IN("Entrada"),
    OUT("Salida"),
    ADJUST("Ajuste")
}

// Motivos predefinidos por tipo
val InReasons  = listOf("Compra a proveedor", "Devolución de cliente", "Corrección de inventario")
val OutReasons = listOf("Venta en caja", "Merma / vencimiento", "Devolución a proveedor", "Ajuste de inventario")
