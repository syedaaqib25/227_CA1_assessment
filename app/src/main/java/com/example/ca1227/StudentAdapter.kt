package com.example.ca1227

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ca1227.databinding.ItemStudentBinding

class StudentAdapter(
    private var students: List<Student>,
    private val onEditClick: (Student) -> Unit,
    private val onDeleteClick: (Student) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.binding.tvName.text = student.name
        holder.binding.tvRollNo.text = "Roll No: ${student.rollNo}"
        holder.binding.tvMarks.text = "Marks: ${student.marks}"
        holder.binding.btnEdit.setOnClickListener { onEditClick(student) }
        holder.binding.btnDelete.setOnClickListener { onDeleteClick(student) }
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }
}
