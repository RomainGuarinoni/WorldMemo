package com.example.worldmemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "worldmemo.db"
        private const val TBL_AUDIO = "tbl_audio"
        private const val ID_COL = "id"
        private const val SENTENCE_COL = "sentence"
        private const val TRANSLATION_COL = "translation"
        private const val COUNTRY_COL = "country"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblAudio =
            ("CREATE TABLE $TBL_AUDIO ($ID_COL INTEGER PRIMARY KEY, $SENTENCE_COL TEXT, $TRANSLATION_COL TEXT, $COUNTRY_COL TEXT)")

        db?.execSQL(createTblAudio)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_AUDIO")
        onCreate(db)
    }

    fun addAudio(audio: AudioModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID_COL, audio.id)
        contentValues.put(SENTENCE_COL, audio.sentence)
        contentValues.put(TRANSLATION_COL, audio.translation)
        contentValues.put(COUNTRY_COL, audio.country)

        val success = db.insert(TBL_AUDIO, null, contentValues)
        db.close()
        return success
    }

    fun getAllAudio(): ArrayList<AudioModel> {
        val result = ArrayList<AudioModel>()
        val selectQuery = "SELECT * FROM $TBL_AUDIO"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            do {
                val id: String = cursor.getString(cursor.getColumnIndex(ID_COL))
                val sentence: String = cursor.getString(cursor.getColumnIndex(SENTENCE_COL))
                val translation: String = cursor.getString(cursor.getColumnIndex(TRANSLATION_COL))
                val country: String = cursor.getString(cursor.getColumnIndex(COUNTRY_COL))

                result.add(
                    AudioModel(
                        id = id, sentence = sentence, translation = translation, country = country
                    )
                )

            } while (cursor.moveToNext())

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result

    }
}