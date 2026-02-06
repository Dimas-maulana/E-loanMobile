package com.example.eloanmust.feature.product.data.mapper

import com.example.eloanmust.feature.product.data.dto.PlafondDto
import com.example.eloanmust.feature.product.data.local.PlafondEntity
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Extension function to convert PlafondDto to PlafondEntity for Room storage.
 */
fun PlafondDto.toEntity(): PlafondEntity {
    val createdAtLong = createdAt?.let {
        try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(it)?.time
        } catch (e: Exception) {
            null
        }
    }
    
    return PlafondEntity(
        id = id,
        name = name,
        description = description,
        minAmount = minAmount,
        maxAmount = maxAmount,
        interestRate = interestRate,
        maxTenor = maxTenor,
        isActive = isActive,
        createdAt = createdAtLong,
        cachedAt = System.currentTimeMillis()
    )
}

/**
 * Extension function to convert PlafondEntity to PlafondDto for UI consumption.
 */
fun PlafondEntity.toDto(): PlafondDto {
    val createdAtStr = createdAt?.let {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(it)
    }
    
    return PlafondDto(
        id = id,
        name = name,
        description = description,
        minAmount = minAmount,
        maxAmount = maxAmount,
        interestRate = interestRate,
        maxTenor = maxTenor,
        isActive = isActive,
        createdAt = createdAtStr
    )
}

/**
 * Extension function to convert list of PlafondDto to list of PlafondEntity.
 */
fun List<PlafondDto>.toEntityList(): List<PlafondEntity> = map { it.toEntity() }

/**
 * Extension function to convert list of PlafondEntity to list of PlafondDto.
 */
fun List<PlafondEntity>.toDtoList(): List<PlafondDto> = map { it.toDto() }
