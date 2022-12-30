package com.example.worldmemo

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.worldmemo.model.AudioModel
import com.example.worldmemo.model.CountryModel
import com.example.worldmemo.model.PhotoModel

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    val FAIL_STATUS = -1

    companion object {
        private const val DATABASE_VERSION = 5
        private const val DATABASE_NAME = "worldmemo.db"
        private const val TBL_AUDIO = "tbl_audio"
        private const val TBL_PHOTO = "tbl_photo"
        private const val ID_COL = "id"
        private const val SENTENCE_COL = "sentence"
        private const val TRANSLATION_COL = "translation"
        private const val COUNTRY_COL = "country"
        private const val TITLE_COL = "title"
        private const val DESCRIPTION_COL = "description"
        private const val CREATED_DATE_COL = "created_date"
        private const val PATH_COL = "path"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTblAudio =
            ("CREATE TABLE $TBL_AUDIO ($ID_COL TEXT PRIMARY KEY, $SENTENCE_COL TEXT, $TRANSLATION_COL TEXT, $COUNTRY_COL TEXT, $CREATED_DATE_COL TEXT, $PATH_COL TEXT)")

        val createTblPhoto =
            ("CREATE TABLE $TBL_PHOTO($ID_COL TEXT PRIMARY KEY, $TITLE_COL TEXT, $DESCRIPTION_COL TEXT, $COUNTRY_COL TEXT, $CREATED_DATE_COL TEXT, $PATH_COL TEXT)")

        db?.execSQL(createTblAudio)
        db?.execSQL(createTblPhoto)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_AUDIO")
        db!!.execSQL("DROP TABLE IF EXISTS $TBL_PHOTO")
        onCreate(db)
    }

    fun addAudio(audio: AudioModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID_COL, audio.id)
        contentValues.put(SENTENCE_COL, audio.sentence)
        contentValues.put(TRANSLATION_COL, audio.translation)
        contentValues.put(COUNTRY_COL, audio.country)
        contentValues.put(PATH_COL, audio.path)
        contentValues.put(CREATED_DATE_COL, audio.createdDate)

        val success = db.insert(TBL_AUDIO, null, contentValues)
        db.close()
        return success
    }

    fun getAllAudio(): ArrayList<AudioModel> {
        val result = ArrayList<AudioModel>()
        val selectQuery = "SELECT * FROM $TBL_AUDIO ORDER BY $CREATED_DATE_COL DESC"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val id: String = cursor.getString(cursor.getColumnIndex(ID_COL))
                    val sentence: String = cursor.getString(cursor.getColumnIndex(SENTENCE_COL))
                    val translation: String =
                        cursor.getString(cursor.getColumnIndex(TRANSLATION_COL))
                    val country: String = cursor.getString(cursor.getColumnIndex(COUNTRY_COL))
                    val path: String = cursor.getString(cursor.getColumnIndex(PATH_COL))
                    val createdDate: String = cursor.getString(
                        cursor.getColumnIndex(
                            CREATED_DATE_COL
                        )
                    )

                    result.add(
                        AudioModel(
                            id = id,
                            sentence = sentence,
                            translation = translation,
                            country = country,
                            path = path,
                            createdDate = createdDate
                        )
                    )

                } while (cursor.moveToNext())
            }
            cursor.close()

        } catch (e: Exception) {
            Log.println(Log.ERROR, "sql get all audios", "An error happened")
            e.printStackTrace()
        }
        return result
    }

    fun addPhoto(photo: PhotoModel): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(ID_COL, photo.id)
        contentValues.put(TITLE_COL, photo.title)
        contentValues.put(DESCRIPTION_COL, photo.description)
        contentValues.put(COUNTRY_COL, photo.country)
        contentValues.put(PATH_COL, photo.path)
        contentValues.put(CREATED_DATE_COL, photo.createdDate)

        val success = db.insert(TBL_PHOTO, null, contentValues)
        db.close()
        return success
    }

    fun getAllPhotos(): ArrayList<PhotoModel> {
        val result = ArrayList<PhotoModel>()
        val selectQuery = "SELECT * FROM $TBL_PHOTO ORDER BY $CREATED_DATE_COL DESC"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val id: String = cursor.getString(cursor.getColumnIndex(ID_COL))
                    val title: String = cursor.getString(cursor.getColumnIndex(TITLE_COL))
                    val description: String =
                        cursor.getString(cursor.getColumnIndex(DESCRIPTION_COL))
                    val country: String = cursor.getString(cursor.getColumnIndex(COUNTRY_COL))
                    val path: String = cursor.getString(cursor.getColumnIndex(PATH_COL))
                    val createdDate: String = cursor.getString(
                        cursor.getColumnIndex(
                            CREATED_DATE_COL
                        )
                    )

                    result.add(
                        PhotoModel(
                            id = id,
                            title = title,
                            description = description,
                            country = country,
                            path = path,
                            createdDate = createdDate
                        )
                    )

                } while (cursor.moveToNext())
            }
            cursor.close()

        } catch (e: Exception) {
            Log.println(Log.ERROR, "sql get all photos", "An error happened")
            e.printStackTrace()
        }
        return result
    }

    fun getAllCountries(): ArrayList<CountryModel> {
        val result = ArrayList<CountryModel>()
        val selectQuery = "SELECT DISTINCT $COUNTRY_COL FROM $TBL_AUDIO"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val name: String = cursor.getString(cursor.getColumnIndex(COUNTRY_COL))
                    result.add(
                        CountryModel(name)
                    )

                } while (cursor.moveToNext())
            }
            cursor.close()

        } catch (e: Exception) {
            Log.println(Log.ERROR, "sql get all countries", "An error happened")
            e.printStackTrace()
        }
        return result
    }

    fun getAudiosByCountry(countryName: String): ArrayList<AudioModel> {
        val result = ArrayList<AudioModel>()
        val selectQuery =
            "SELECT * FROM $TBL_AUDIO WHERE $COUNTRY_COL = '$countryName' ORDER BY $CREATED_DATE_COL DESC"
        val db = this.readableDatabase
        val cursor: Cursor?


        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val id: String = cursor.getString(cursor.getColumnIndex(ID_COL))
                    val sentence: String = cursor.getString(cursor.getColumnIndex(SENTENCE_COL))
                    val translation: String =
                        cursor.getString(cursor.getColumnIndex(TRANSLATION_COL))
                    val country: String = cursor.getString(cursor.getColumnIndex(COUNTRY_COL))
                    val path: String = cursor.getString(cursor.getColumnIndex(PATH_COL))
                    val createdDate: String = cursor.getString(
                        cursor.getColumnIndex(
                            CREATED_DATE_COL
                        )
                    )
                    result.add(
                        AudioModel(
                            id = id,
                            sentence = sentence,
                            translation = translation,
                            country = country,
                            path = path,
                            createdDate = createdDate
                        )
                    )

                } while (cursor.moveToNext())
            }
            cursor.close()

        } catch (e: Exception) {
            Log.println(Log.ERROR, "sql get all audios by country name", "An error happened")
            e.printStackTrace()
        }

        return result
    }

    fun getPhotosByCountry(countryName: String): ArrayList<PhotoModel> {
        val result = ArrayList<PhotoModel>()
        val selectQuery =
            "SELECT * FROM $TBL_PHOTO WHERE $COUNTRY_COL = '$countryName' ORDER BY $CREATED_DATE_COL DESC"
        val db = this.readableDatabase
        val cursor: Cursor?


        try {
            cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val id: String = cursor.getString(cursor.getColumnIndex(ID_COL))
                    val title: String = cursor.getString(cursor.getColumnIndex(TITLE_COL))
                    val description: String =
                        cursor.getString(cursor.getColumnIndex(DESCRIPTION_COL))
                    val country: String = cursor.getString(cursor.getColumnIndex(COUNTRY_COL))
                    val path: String = cursor.getString(cursor.getColumnIndex(PATH_COL))
                    val createdDate: String = cursor.getString(
                        cursor.getColumnIndex(
                            CREATED_DATE_COL
                        )
                    )
                    result.add(
                        PhotoModel(
                            id = id,
                            title = title,
                            description = description,
                            country = country,
                            path = path,
                            createdDate = createdDate
                        )
                    )

                } while (cursor.moveToNext())
            }
            cursor.close()

        } catch (e: Exception) {
            Log.println(Log.ERROR, "sql get all photos by country name", "An error happened")
            e.printStackTrace()
        }

        return result
    }

    fun deleteAudio(audio: AudioModel): Int {
        val db = this.writableDatabase

        val success = db.delete(TBL_AUDIO, "id='${audio.id}'", null)
        db.close()

        return success
    }

    fun deletePhoto(photo: PhotoModel): Int {
        val db = this.writableDatabase

        val success = db.delete(TBL_PHOTO, "id='${photo.id}'", null)
        db.close()

        return success
    }
}