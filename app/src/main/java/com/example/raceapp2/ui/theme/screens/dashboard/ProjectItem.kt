package com.example.raceapp2.model

data class ProjectItem(
    val id: String,
    val name: String,
    val taskCount: Int,
    val ownerName: String? = null,
    val participants: List<String> = emptyList()
)

