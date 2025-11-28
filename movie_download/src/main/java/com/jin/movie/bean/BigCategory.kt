package com.jin.movie.bean

data class BigCategory(
    val id: String,
    val url: String,
    val name: String,
    val isSelected: Boolean = false,
    // 包含的小分类列表
    val subCategories: MutableList<SmallCategory> = mutableListOf(),
    val fixCategories: MutableList<FixedCategory> = mutableListOf()
) {
    override fun toString(): String {
        return "BigCategory(id='$id', url='$url', name='$name', isSelected=$isSelected, subCategories=$subCategories, fixCategories=$fixCategories)"
    }
}



