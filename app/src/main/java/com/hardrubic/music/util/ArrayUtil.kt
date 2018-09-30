package com.hardrubic.music.util

object ArrayUtil {
    fun itob(intarr: IntArray): ByteArray {
        val bytelength = intarr.size * 4//长度
        val bt = ByteArray(bytelength)//开辟数组
        var curint = 0
        var j = 0
        var k = 0
        while (j < intarr.size) {
            curint = intarr[j]
            bt[k] = (curint shr 24 and 255).toByte()//右移4位，与1作与运算
            bt[k + 1] = (curint shr 16 and 255).toByte()
            bt[k + 2] = (curint shr 8 and 255).toByte()
            bt[k + 3] = (curint shr 0 and 255).toByte()
            j++
            k += 4
        }
        return bt
    }

    fun btoi(btarr: ByteArray): IntArray? {
        if (btarr.size % 4 != 0) {
            return null
        }
        val intarr = IntArray(btarr.size / 4)
        var i1: Int
        var i2: Int
        var i3: Int
        var i4: Int
        var j = 0
        var k = 0
        while (j < intarr.size)
        //j循环int		k循环byte数组
        {
            i1 = btarr[k].toInt()
            i2 = btarr[k + 1].toInt()
            i3 = btarr[k + 2].toInt()
            i4 = btarr[k + 3].toInt()
            if (i1 < 0) {
                i1 += 256
            }
            if (i2 < 0) {
                i2 += 256
            }
            if (i3 < 0) {
                i3 += 256
            }
            if (i4 < 0) {
                i4 += 256
            }
            intarr[j] = (i1 shl 24) + (i2 shl 16) + (i3 shl 8) + (i4 shl 0)//保存Int数据类型转换
            j++
            k += 4
        }
        return intarr
    }
}