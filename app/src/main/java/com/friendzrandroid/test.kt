package com.friendzrandroid


fun main() {
    println(MatrixChallenge(arrayOf("abfr, ryut, gmei, fadc","bar,daf,deurfby,fad,fruma,med,mur,murt,murtkaf,pokldj,xyz")))

}


fun ArrayChallenge(strArr: Array<String>): String {

    val get1Location = get1Location(strArr)
    val get2Location = get2locations(strArr)
    var minDis = 1000
    get2Location.forEach {
        val newDis =
            ((it.first - get1Location.first) * (it.first - get1Location.first)) + ((it.second - get1Location.second) * (it.second - get1Location.second))

        val dob = mySqrt(newDis)
        if (dob < minDis) {
            minDis = dob
        }

    }

    return minDis.toString()
}


fun get1Location(strArr: Array<String>): Pair<Int, Int> {
    var lox = 0
    var loy = 0
    for (x in 0 until strArr.size) {
        for (y in 0 until strArr[x].length) {
            if (strArr[x][y] == '1') {
                lox = x
                loy = y
            }
        }
    }
    return Pair(lox, loy)
}

fun get2locations(strArr: Array<String>): ArrayList<Pair<Int, Int>> {
    val list = ArrayList<Pair<Int, Int>>()
    for (x in 0 until strArr.size) {
        for (y in 0 until strArr[x].length) {
            if (strArr[x][y] == '2') {
                list.add(Pair(x, y))
            }
        }
    }
    return list
}


fun mySqrt(x: Int): Int {
    if (x == 0) return 0
    var left = 1
    var right = x / 2 + 1
    var res: Int = 0
    while (left <= right) {
        val mid = left + (right - left) / 2
        if (mid <= x / mid) {
            left = mid + 1
            res = mid
        } else {
            right = mid - 1
        }
    }
    return res
}


//fun StringChallenge(str: String): String {
//
//    var newStr = ""
//
//    val reg = Regex("[^a-zA-Z0-9]")
//    val list = str.split(reg)
//
//    newStr+=list.get(0)
//
//    for (i in 1 until list.size){
//
//        newStr += getUpperCaseStr(list[i])
//    }
//
//    return newStr
//
//}

fun getUpperCaseStr(str: String): String {

    if (str.length <= 1) {
        return str.replace(str[0], str[0] - 32)
    } else {
        var newStr = ""
        for (x in 0 until str.length) {
            if (x == 0) {
                if (str[0] >= 'A' && str[0] <= 'Z') {
                    newStr += str[x]
                } else {
                    newStr += str[x] - 32
                }
            } else {
                if (str[x] >= 'a' && str[x] <= 'z') {
                    newStr += str[x]
                } else
                    newStr += str[x] + 32
            }

        }

        return newStr
    }


}


fun filter(ch: Char, str: String): Int {
    var count = 0
    str.forEach {
        if (it == ch)
            count++
    }
    return count
}


fun StringChallenge(str: String): String {

    val list = str.split("<")
    val newList = ArrayList<String>()
    newList.addAll(list.subList(1, list.size))
    for (x in 0 until newList.size) {
        var isContain = false
        val serStr = newList[x].split(">")
        if (!serStr[0].equals("")) {
            for (y in x + 1 until newList.size) {
                if (newList[y].contains("/${serStr[0]}")) {
                    newList[y] = ""
                    isContain = true
                    break
                }
            }

            if (!isContain) {
                val endTxt = newList[x].split(">")
                return endTxt[0]
            }
        }
    }
    return "true"
}


fun MatrixChallenge(strArr: Array<String>): String {

    val tests = strArr.get(strArr.size - 1)
    strArr[strArr.size - 1] = ""
    val searchList = tests.split(",")
    var notFoundStr = ""
    searchList.forEach {  word ->
        var chrNotFound =  false
        for (y in 0 until word.length) {
           for (x in 0 until  strArr.size-1){
               if (!isCharFound(word[y],strArr[x])){
                   notFoundStr+= "$word,"
                   chrNotFound = true
               }
               if (chrNotFound)
                   break
           }
            if (chrNotFound)
                break
        }

    }

    if (notFoundStr.equals("")){
        return "true"
    }

    return notFoundStr.substring(0,notFoundStr.length-1)
}

fun isCharFound(char: Char,Str:String):Boolean{
    return Str.contains(char)
}
//
//fun lop(searchStr: String, searchIn: String): Boolean {
//
//
//}
//



