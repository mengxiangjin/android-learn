package com.jin.movie.utils

import android.util.Base64
import android.util.Log
import com.jin.movie.bean.BigCategory
import com.jin.movie.bean.FixedCategory
import com.jin.movie.bean.SmallCategory
import com.jin.movie.bean.Video
import org.jsoup.Jsoup
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * HTML 解析工具类
 * 负责解密反爬虫数据，并使用 Jsoup 提取内容
 */
object HtmlParseHelper {

    private const val TAG = "HtmlParseHelper"

    /**
     * 阶段一：解密
     * 将服务器返回的 <script>...atob(...)...</script> 还原为真实的 HTML 字符串
     */
    fun decodeHtml(rawResponse: String): String {
        try {
            // 1. 正则提取 atob("...") 里面的内容
            // 匹配 atob(" 后面，直到下一个 " 结束
            val regex = "atob\\(\"([^\"]+)\"\\)"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(rawResponse)

            if (matcher.find()) {
                val encodedStr = matcher.group(1) ?: return ""

                // 2. Base64 解码
                // 服务器逻辑是 decodeURIComponent(atob(...))
                // 所以我们先解 Base64，得到类似 "%3C%21DOCTYPE..." 的字符串
                val urlEncodedBytes = Base64.decode(encodedStr, Base64.DEFAULT)
                val urlEncodedStr = String(urlEncodedBytes, Charsets.UTF_8)

                // 3. URL 解码
                // 将 "%3C" 变回 "<"
                val finalHtml = URLDecoder.decode(urlEncodedStr, "UTF-8")

                // Log.d(TAG, "解密成功，HTML长度: ${finalHtml.length}")
                return finalHtml
            } else {
                Log.e(TAG, "未找到加密内容，可能是普通HTML或规则变了")
                return rawResponse // 如果没加密，直接返回原内容
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

    /**
     * 阶段二：解析数据 (类似 BeautifulSoup)
     * @param html 解密后的 HTML 字符串
     * @return 组装好的数据列表 (你需要根据实际网页结构修改选择器)
     */
    fun parseCategoryList(html: String): List<BigCategory> {
        val bigCategoryList = mutableListOf<BigCategory>()

        if (html.isEmpty()) return bigCategoryList

        try {
            // 1. 将字符串转为 Jsoup 文档对象
            val doc = Jsoup.parse(html)

            // --- 下面是模拟解析逻辑，你需要根据真实的 HTML 结构修改 CSS 选择器 ---

            // 假设视频都在 class="video-item" 的 div 里
            // CSS 选择器语法：div.video-item
            val navs = doc.select("div.van-tabs__nav")
            if (navs.size < 2) return bigCategoryList
            //先拿第一个对应大分类 start

            val elements = navs[0].select("a")


            for (element in elements) {
                val name = element.text().trim()
                val href = element.attr("href")

                // 提取 ID
                val id = extractIdFromUrl(href)

                val isSelected = element.hasClass("van-tab--active")

                bigCategoryList.add(BigCategory(id,href, name,isSelected))
            }
            // end

            val bigCategory = bigCategoryList.find {
                it.isSelected
            }
            if (bigCategory == null) return emptyList()

            //再拿第二个对应小分类 start
            val secondElements = navs[1].select("a")
            for (element in secondElements) {
                val name = element.text().trim()
                val href = element.attr("href")
                // 提取 ID
                val id = extractIdFromUrl(href)
                val isSelected = element.hasClass("van-tab--active")
                bigCategory.subCategories.add(SmallCategory(id,href,name,bigCategory.id,isSelected))
            }
            //end

            //拿固定标签分类start
            // 1. 定位容器 div.search_btn 下的所有 a 标签
            val thirdElements = doc.select("div.search_btn a")

            thirdElements.forEachIndexed { index, element ->
                // 提取文字 (Jsoup 会自动忽略 span 标签直接拿文字) -> "最新"
                val name = element.text().trim()

                // 提取 URL -> "/index.php/vod/show/by/time/id/1.html"
                val href = element.attr("href")

                // --- 判断选中状态 ---
                // 观察 HTML 发现：选中的是用 "van-button--primary" 样式
                // 未选中的是用 "van-button--default" 样式
                val isSelected = element.hasClass("van-button--primary")
                bigCategory.fixCategories.add(FixedCategory(index.toString(),href,name, bigCategory.id, isSelected))
            }
            //拿固定标签分类end

        } catch (e: Exception) {
            Log.e(TAG, "Jsoup 解析出错: ${e.message}")
        }

        return bigCategoryList
    }

    fun parseVideoList(html: String,isSearch: Boolean = false): List<Video> {
        val videoList = mutableListOf<Video>()
        if (html.isEmpty()) return videoList
        try {
            // 1. 将字符串转为 Jsoup 文档对象
            val doc = Jsoup.parse(html)
            val elements = doc.select("div#content a")
            for (element in elements) {
                val href = element.attr("href")
                val imgTag = element.selectFirst("img.van-image__img.md-lazy[data-src]")
                val imgSrc = imgTag?.attr("data-src")
                val playCounts = if (isSearch) {
                    element.selectFirst("div.list_play")?.text()?.trim()
                } else {
                    element.selectFirst("div.list_read_number")?.text()?.trim()
                }
                val duration = element.selectFirst("div.list_duration")?.text()?.trim()?:""
                //免费区可能没有时长
                val title = if (isSearch) {
                    element.selectFirst("div.list_title.van-multi-ellipsis--l2")?.text()?.trim()
                } else {
                    element.selectFirst("div.list_title.title_line_1.van-ellipsis")?.text()?.trim()
                }
                if (imgSrc == null || title == null || playCounts == null) continue
                videoList.add(Video(href,title,imgSrc,duration,playCounts))
            }

        }catch (e: Exception) {
            Log.e(TAG, "Jsoup 解析出错: ${e.message}")
        }
        return videoList
    }

    /**
     * 解析总页数
     * 针对字符串："共89386条数据,当前1/7449页"
     */
    fun parseTotalPage(html: String): Int {
        try {
            val regex = "当前\\d+/(\\d+)页"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(html)
            if (matcher.find()) {
                return matcher.group(1)?.toInt() ?: 1
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 1
    }

    /**
     * 【新增】独立解析排序标签 (适用于搜索页，首页不行)
     */
    fun parseSortTags(html: String): List<FixedCategory> {
        val list = mutableListOf<FixedCategory>()
        if (html.isEmpty()) return list

        try {
            val doc = Jsoup.parse(html)

            // 直接定位排序按钮区域
            val elements = doc.select("div.van-tabs__nav a")

            elements.forEachIndexed { index, element ->
                val name = element.text().trim()
                val href = element.attr("href")

                // 判断选中状态
                val isSelected = element.hasClass("van-tab--active")

                // 构造 FixedCategory
                // parentId 传空即可，搜索页不需要归属
                list.add(FixedCategory(index.toString(), href, name, "", isSelected))
            }
        } catch (e: Exception) {
            Log.e(TAG, "解析排序标签失败: ${e.message}")
        }
        return list
    }

    private fun extractIdFromUrl(url: String): String {
        // 正则含义：匹配 "/id/" 后面跟着的数字
        val regex = "/id/(\\d+)"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(url)
        return if (matcher.find()) {
            matcher.group(1) ?: ""
        } else {
            "" // 没找到ID，返回空或原链接
        }
    }

    // 获取网页 标题（测试用）
    fun getPageTitle(html: String): String {
        return Jsoup.parse(html).title()
    }
}