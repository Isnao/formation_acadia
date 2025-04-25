package isnao.acadiaformation

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import java.io.InputStream
import java.text.SimpleDateFormat

class SessionItem(
    private var date: String,
    private var comportements: String,
    private var objectif: String,
    private var remarques: String,
    private var duree: String,
    private var validation: Boolean,
) {
    override fun toString(): String =
        "$date;$comportements;$objectif;$remarques;$duree;${ if (validation) {
            "oui"
        } else {
            "non"
        }}\n"

    fun getDate(): String = date
}

class SessionViewModel : ViewModel() {
    private var list: MutableList<SessionItem> = ArrayList()
    lateinit var progress: Array<Array<String>>

    @SuppressLint("SimpleDateFormat")
    fun addSession(item: SessionItem) {
        list.add(item)
        list.sortBy { SimpleDateFormat("dd/MM/yyyy").parse(it.getDate()) }
    }

    fun import(inputStream: InputStream) {
        inputStream.readBytes().decodeToString().split("\n").forEach { line ->
            if (line != "" && !line.contains("Date;Comportement")) {
                val elems = line.split(";")
                var validation = false
                if (elems[5] == "oui") {
                    validation = true
                }
                addSession(SessionItem(elems[0], elems[1], elems[2], elems[3], elems[4], validation))
            }
        }
    }

    fun listToString(): String {
        var result = ""
        list.forEach { sessionItem: SessionItem ->
            result += sessionItem.toString()
        }
        return result
    }

    fun clean() {
        list = ArrayList()
    }
}
