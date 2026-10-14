package com.example.model

import java.util.UUID

data class PaperTemplate(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val style: PaperStyle = PaperStyle(),
    val sections: List<PaperSection> = emptyList()
)
