package com.example.ca1227

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ca1227.databinding.ActivityMainBinding
import com.example.ca1227.databinding.DialogStudentBinding
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private lateinit var adapter: StudentAdapter
    private val studentList = mutableListOf<Student>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔥 IMPORTANT: Correct region URL
        database = FirebaseDatabase
            .getInstance("https://ca12271-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference
            .child("students")

        adapter = StudentAdapter(
            studentList,
            onEditClick = { showDialog(it) },
            onDeleteClick = { deleteStudent(it) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.fabAdd.setOnClickListener {
            showDialog(null)
        }

        fetchStudents()
    }

    private fun fetchStudents() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentList.clear()
                for (child in snapshot.children) {
                    val student = child.getValue(Student::class.java)
                    student?.id = child.key
                    if (student != null) {
                        studentList.add(student)
                    }
                }
                adapter.updateData(ArrayList(studentList))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDialog(student: Student?) {
        val dialogBinding = DialogStudentBinding.inflate(LayoutInflater.from(this))
        val dialog = AlertDialog.Builder(this).setView(dialogBinding.root).create()

        if (student != null) {
            dialogBinding.etName.setText(student.name)
            dialogBinding.etRollNo.setText(student.rollNo)
            dialogBinding.etMarks.setText(student.marks)
        }

        dialogBinding.btnSave.setOnClickListener {
            val name = dialogBinding.etName.text.toString().trim()
            val rollNo = dialogBinding.etRollNo.text.toString().trim()
            val marks = dialogBinding.etMarks.text.toString().trim()

            if (name.isEmpty() || rollNo.isEmpty() || marks.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (student == null) {
                val id = database.push().key ?: return@setOnClickListener
                val newStudent = Student(id, name, rollNo, marks)
                database.child(id).setValue(newStudent)
            } else {
                val updatedStudent = Student(student.id, name, rollNo, marks)
                database.child(student.id!!).setValue(updatedStudent)
            }

            dialog.dismiss()
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteStudent(student: Student) {
        student.id?.let {
            database.child(it).removeValue()
        }
    }
}
