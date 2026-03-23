package com.example.stickynotes

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NoteRepository(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("sticky_notes", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val notesKey = "notes_list"

    fun getAllNotes(): List<Note> {
        val json = sharedPreferences.getString(notesKey, "[]") ?: "[]"
        val type = object : TypeToken<List<Note>>() {}.type
        // Reversed so new notes appear at the end (or start, depending on UI needs)
        // Usually, users expect new notes to appear at the beginning or end.
        // The requirement says "new boxes must be in the right of other ones", 
        // which means adding to the end of the list.
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addNote(note: Note) {
        val notes = getAllNotes().toMutableList()
        notes.add(note) // Adds to the end of the list
        saveNotes(notes)
    }

    fun updateNote(note: Note) {
        val notes = getAllNotes().toMutableList()
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note
            saveNotes(notes)
        }
    }

    fun deleteNote(noteId: String) {
        val notes = getAllNotes().toMutableList()
        notes.removeAll { it.id == noteId }
        saveNotes(notes)
    }

    private fun saveNotes(notes: List<Note>) {
        val json = gson.toJson(notes)
        sharedPreferences.edit().putString(notesKey, json).apply()
    }
}
