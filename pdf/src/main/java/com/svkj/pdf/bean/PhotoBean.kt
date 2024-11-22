package com.svkj.pdf.bean

import java.io.Serializable


class PhotoBean(val id: Int, val name: String, val filePath: String, val modifyTime: String, val fileSize: String,val bucketName: String): Serializable {

}