package com.ankasoft.powerstickynotes

import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.ankasoft.powerstickynotes.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: NoteRepository
    private lateinit var adapter: NotesAdapter

    private var timer: CountDownTimer? = null
    private var timeLeftInMillis: Long = TimerPhase.WORK.durationMinutes * 60 * 1000L
    private var totalTimeInMillis: Long = TimerPhase.WORK.durationMinutes * 60 * 1000L
    private var isTimerRunning = false
    private var pomodoroCount = 0
    private var currentPhase = TimerPhase.WORK

    enum class TimerPhase(val durationMinutes: Int) {
        WORK(25),
        SHORT_BREAK(5),
        LONG_BREAK(15)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setShowWhenLocked()

        repository = NoteRepository(this)
        pomodoroCount = repository.getPomodoroCount()

        if (savedInstanceState != null) {
            restoreTimerState(savedInstanceState)
        }

        setupRecyclerView()
        setupFAB()
        setupPomodoro()
        loadNotes()
        updateTimerUI()
    }

    private fun setShowWhenLocked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(KEY_TIME_LEFT, timeLeftInMillis)
        outState.putLong(KEY_TOTAL_TIME, totalTimeInMillis)
        outState.putBoolean(KEY_TIMER_RUNNING, isTimerRunning)
        outState.putInt(KEY_POMODORO_COUNT, pomodoroCount)
        outState.putInt(KEY_CURRENT_PHASE, currentPhase.ordinal)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restoreTimerState(savedInstanceState)
    }

    private fun restoreTimerState(savedInstanceState: Bundle) {
        timeLeftInMillis = savedInstanceState.getLong(KEY_TIME_LEFT, TimerPhase.WORK.durationMinutes * 60 * 1000L)
        totalTimeInMillis = savedInstanceState.getLong(KEY_TOTAL_TIME, TimerPhase.WORK.durationMinutes * 60 * 1000L)
        isTimerRunning = savedInstanceState.getBoolean(KEY_TIMER_RUNNING, false)
        pomodoroCount = savedInstanceState.getInt(KEY_POMODORO_COUNT, 0)
        currentPhase = TimerPhase.entries[savedInstanceState.getInt(KEY_CURRENT_PHASE, 0)]

        if (isTimerRunning) {
            startTimer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
        repository.savePomodoroCount(pomodoroCount)
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter(
            onNoteClick = { note -> editNote(note) },
            onNoteLongClick = { note -> deleteNote(note) }
        )
        binding.notesRecyclerView.layoutManager = GridLayoutManager(this, 4)
        binding.notesRecyclerView.adapter = adapter
    }

    private fun setupFAB() {
        binding.fabAddNote.setOnClickListener {
            addNewNote()
        }
    }

    private fun setupPomodoro() {
        binding.pomodoroCard.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.pomodoroCard.setOnLongClickListener {
            resetTimer()
            true
        }
    }

    private fun startTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(timeLeftInMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerUI()
            }

            override fun onFinish() {
                isTimerRunning = false
                timeLeftInMillis = 0
                updateTimerUI()
                playSound()
                handlePhaseEnd()
            }
        }.start()

        isTimerRunning = true
    }

    private fun playSound() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseTimer() {
        timer?.cancel()
        isTimerRunning = false
    }

    private fun resetTimer() {
        pauseTimer()
        currentPhase = TimerPhase.WORK
        totalTimeInMillis = TimerPhase.WORK.durationMinutes * 60 * 1000L
        timeLeftInMillis = totalTimeInMillis
        updateTimerUI()
        binding.pomodoroCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.pomodoro_red))
    }

    private fun handlePhaseEnd() {
        when (currentPhase) {
            TimerPhase.WORK -> {
                pomodoroCount++
                repository.savePomodoroCount(pomodoroCount)
                if (pomodoroCount % 4 == 0) {
                    currentPhase = TimerPhase.LONG_BREAK
                } else {
                    currentPhase = TimerPhase.SHORT_BREAK
                }
                binding.pomodoroCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.sticky_yellow))
            }
            TimerPhase.SHORT_BREAK, TimerPhase.LONG_BREAK -> {
                currentPhase = TimerPhase.WORK
                binding.pomodoroCard.setCardBackgroundColor(ContextCompat.getColor(this, R.color.pomodoro_red))
            }
        }
        totalTimeInMillis = currentPhase.durationMinutes * 60 * 1000L
        timeLeftInMillis = totalTimeInMillis
        updateTimerUI()
    }

    private fun updateTimerUI() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.pomodoroTimerText.text = timeFormatted

        val progress = if (totalTimeInMillis > 0) {
            ((totalTimeInMillis - timeLeftInMillis).toDouble() / totalTimeInMillis * 100).toInt()
        } else 0
        binding.pomodoroProgress.progress = progress
    }

    private fun addNewNote() {
        showEditDialog(null)
    }

    private fun editNote(note: Note) {
        showEditDialog(note)
    }

    private fun deleteNote(note: Note) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .setPositiveButton("Delete") { _, _ ->
                repository.deleteNote(note.id)
                loadNotes()
            }
            .show()
    }

    private fun showEditDialog(note: Note?) {
        val dialogView = layoutInflater.inflate(
            R.layout.dialog_edit_note,
            null
        )
        val editText = dialogView.findViewById<EditText>(
            R.id.editNoteText
        )

        if (note != null) {
            editText.setText(note.content)
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<android.widget.Button>(
            R.id.btnCancel
        ).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<android.widget.Button>(
            R.id.btnSave
        ).setOnClickListener {
            val content = editText.text.toString().trim()
            if (content.isNotEmpty()) {
                if (note != null) {
                    repository.updateNote(note.copy(content = content))
                } else {
                    repository.addNote(Note(content = content))
                }
                loadNotes()
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun loadNotes() {
        val notes = repository.getAllNotes()
        adapter.updateNotes(notes)

        if (notes.isEmpty()) {
            binding.emptyStateText.visibility = android.view.View.VISIBLE
            binding.notesRecyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyStateText.visibility = android.view.View.GONE
            binding.notesRecyclerView.visibility = android.view.View.VISIBLE
        }
    }

    companion object {
        private const val KEY_TIME_LEFT = "key_time_left"
        private const val KEY_TOTAL_TIME = "key_total_time"
        private const val KEY_TIMER_RUNNING = "key_timer_running"
        private const val KEY_POMODORO_COUNT = "key_pomodoro_count"
        private const val KEY_CURRENT_PHASE = "key_current_phase"
    }
}
