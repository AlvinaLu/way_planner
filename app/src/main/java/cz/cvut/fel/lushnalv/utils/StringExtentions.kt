package cz.cvut.fel.lushnalv.utils

fun String.cut(max: Int): String {
    if (this.length > max) {
        var name = this.substring(0, max)
        name += "..."
        return name
    } else {
        return this
    }
}