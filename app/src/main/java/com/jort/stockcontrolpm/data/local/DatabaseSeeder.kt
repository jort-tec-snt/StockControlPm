package com.jort.stockcontrolpm.data.local

import com.jort.stockcontrolpm.data.repository.ProductRepository
import com.jort.stockcontrolpm.domain.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Inserta productos de ejemplo la primera vez que se abre la app.
 * Se omite si ya hay datos en la base de datos.
 */
object DatabaseSeeder {

    suspend fun seedIfEmpty(productRepository: ProductRepository) {
        withContext(Dispatchers.IO) {
            if (productRepository.countProducts() > 0) return@withContext
            sampleProducts().forEach { productRepository.saveProduct(it) }
        }
    }

    private fun sampleProducts(): List<Product> {
        val now = System.currentTimeMillis()
        val day = 86_400_000L

        return listOf(
            Product(id = 0, name = "Arroz Costeño 5kg",          category = "Abarrotes",   stock = 48,  minStock = 10, unitPrice = 22.90, purchasePrice = 18.50, sku = "ARR-001", supplier = "Distribuidora del Norte",  expirationDate = null,              description = "Arroz extra largo premium",                   createdAt = now, updatedAt = now),
            Product(id = 0, name = "Aceite Vegetal 1L",          category = "Abarrotes",   stock = 30,  minStock = 8,  unitPrice = 8.50,  purchasePrice = 6.80,  sku = "ACE-002", supplier = "Alicorp S.A.",              expirationDate = now + 365 * day,   description = "Aceite vegetal para cocinar",                 createdAt = now, updatedAt = now),
            Product(id = 0, name = "Azúcar Rubia 1kg",           category = "Abarrotes",   stock = 60,  minStock = 15, unitPrice = 4.20,  purchasePrice = 3.40,  sku = "AZU-003", supplier = "Cartavio S.A.",              expirationDate = now + 180 * day,   description = "Azúcar rubia refinada",                       createdAt = now, updatedAt = now),
            Product(id = 0, name = "Leche Gloria Entera 400g",   category = "Lácteos",     stock = 5,   minStock = 12, unitPrice = 3.80,  purchasePrice = 2.90,  sku = "LEC-004", supplier = "Gloria S.A.",                expirationDate = now + 90 * day,    description = "Leche evaporada entera, lata 400g",           createdAt = now, updatedAt = now),
            Product(id = 0, name = "Yogurt Yomost 150g",         category = "Lácteos",     stock = 0,   minStock = 10, unitPrice = 1.50,  purchasePrice = 1.10,  sku = "YOG-005", supplier = "Gloria S.A.",                expirationDate = now + 14 * day,    description = "Yogurt bebible sabor fresa",                  createdAt = now, updatedAt = now),
            Product(id = 0, name = "Pollo Entero kg",            category = "Carnes",      stock = 8,   minStock = 5,  unitPrice = 10.50, purchasePrice = 8.20,  sku = "POL-006", supplier = "San Fernando",               expirationDate = now + 3 * day,     description = "Pollo entero sin menudencia",                 createdAt = now, updatedAt = now),
            Product(id = 0, name = "Huevos Pardos x30",          category = "Huevos",      stock = 15,  minStock = 5,  unitPrice = 15.00, purchasePrice = 12.00, sku = "HUE-007", supplier = "Redondos S.A.",              expirationDate = now + 21 * day,    description = "Huevos pardos talla L, cartón x30",           createdAt = now, updatedAt = now),
            Product(id = 0, name = "Pan de Molde Bimbo",         category = "Panadería",   stock = 3,   minStock = 6,  unitPrice = 6.50,  purchasePrice = 5.00,  sku = "PAN-008", supplier = "Bimbo Perú",                 expirationDate = now + 7 * day,     description = "Pan de molde blanco 500g",                    createdAt = now, updatedAt = now),
            Product(id = 0, name = "Fideos Don Vittorio 500g",   category = "Abarrotes",   stock = 55,  minStock = 10, unitPrice = 3.20,  purchasePrice = 2.50,  sku = "FID-009", supplier = "Alicorp S.A.",               expirationDate = now + 400 * day,   description = "Fideos tallarin grueso",                      createdAt = now, updatedAt = now),
            Product(id = 0, name = "Detergente Ariel 500g",      category = "Limpieza",    stock = 22,  minStock = 8,  unitPrice = 7.90,  purchasePrice = 6.20,  sku = "DET-010", supplier = "P&G Perú",                   expirationDate = null,              description = "Detergente en polvo multiusos",               createdAt = now, updatedAt = now),
            Product(id = 0, name = "Jabón Bolívar x3",           category = "Limpieza",    stock = 18,  minStock = 6,  unitPrice = 4.50,  purchasePrice = 3.30,  sku = "JAB-011", supplier = "Alicorp S.A.",               expirationDate = null,              description = "Jabón de lavar ropa, pack x3 unidades",       createdAt = now, updatedAt = now),
            Product(id = 0, name = "Gaseosa Inca Kola 2L",       category = "Bebidas",     stock = 24,  minStock = 10, unitPrice = 7.20,  purchasePrice = 5.50,  sku = "GAS-012", supplier = "Coca-Cola Perú",             expirationDate = now + 120 * day,   description = "Gaseosa sabor original, botella 2 litros",    createdAt = now, updatedAt = now),
            Product(id = 0, name = "Agua San Luis 625ml",        category = "Bebidas",     stock = 72,  minStock = 24, unitPrice = 1.20,  purchasePrice = 0.80,  sku = "AGU-013", supplier = "Nestlé Perú",                expirationDate = now + 730 * day,   description = "Agua mineral sin gas",                        createdAt = now, updatedAt = now),
            Product(id = 0, name = "Atún Florida 170g",          category = "Conservas",   stock = 40,  minStock = 12, unitPrice = 5.50,  purchasePrice = 4.20,  sku = "ATU-014", supplier = "Inversiones Florida",        expirationDate = now + 1095 * day,  description = "Atún en trozos al agua, lata 170g",           createdAt = now, updatedAt = now),
            Product(id = 0, name = "Papel Higiénico Elite x6",   category = "Higiene",     stock = 9,   minStock = 4,  unitPrice = 12.90, purchasePrice = 9.80,  sku = "PAP-015", supplier = "CMPC Tissue Perú",           expirationDate = null,              description = "Papel higiénico doble hoja, pack x6 rollos",  createdAt = now, updatedAt = now)
        )
    }
}
