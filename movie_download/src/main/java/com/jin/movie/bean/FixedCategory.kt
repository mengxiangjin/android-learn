package com.jin.movie.bean

data class FixedCategory(
    val id: String,
    val url: String,
    val name: String,
    val parentId: String, // 可选，方便回溯
    val isSelected: Boolean = false,
) {
    override fun toString(): String {
        return "FixedCategory(id='$id', url='$url', name='$name', parentId='$parentId', isSelected=$isSelected)"
    }
}
