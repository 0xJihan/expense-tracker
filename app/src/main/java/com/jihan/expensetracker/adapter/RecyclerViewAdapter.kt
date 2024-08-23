package com.jihan.expensetracker.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.jihan.expensetracker.R
import com.jihan.expensetracker.databinding.ItemInsertBinding
import com.jihan.expensetracker.room.Information
import java.text.SimpleDateFormat
import java.util.Date

class RecyclerViewAdapter(
    private var myList: List<Information>,
    private val viewModel: com.jihan.expensetracker.model.ViewModel
) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // initialize views
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val imageView: ImageView = itemView.findViewById(R.id.imgIcon)


        // binding data to views
        fun bind(data: Information) {
            tvNotes.text = data.notes
            tvDate.text = dateToTime(data.date)
            tvAmount.text = if (data.isIncome) {
                tvAmount.setTextColor(Color.GREEN)
                "${data.amount} TK"
            } else {
                tvAmount.setTextColor(Color.RED)
                "- ${data.amount} TK"
            }
            imageView.setImageResource(if (data.isIncome) R.drawable.up else R.drawable.down)
        }

        // converting date
        private fun dateToTime(date: Date): String {
            val format = SimpleDateFormat("hh:mm a   dd MMM yyyy")
            return format.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = myList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myList[position])

        // showing pop up menu for long click on item
        holder.itemView.setOnLongClickListener {
            showPopupMenu(it, holder.adapterPosition)
            true
        }
    }

    // function for showing pop menu
    private fun showPopupMenu(view: View, position: Int) {
        val popUp = PopupMenu(view.context, view, Gravity.END)
        popUp.inflate(R.menu.pop_up)

        // editing or deleting item based on click
        popUp.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_edit -> showAlertDialog(view.context, myList[position])
                R.id.menu_item_delete -> {
                    viewModel.deleteInfo(myList[position])
                    Toast.makeText(view.context, "Item Deleted Successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true
        }

        popUp.show()
    }


    // function for showing alert dialog to update database
    private fun showAlertDialog(context: Context, information: Information) {
        val binding = ItemInsertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context).apply {
            setContentView(binding.root)
            binding.btnInput.text = "Update"
            binding.edInputAmount.setText(information.amount.toInt().toString())
            binding.edInputNotes.setText(information.notes)
            binding.chipIncome.isChecked = information.isIncome
            binding.chipExpense.isChecked = !information.isIncome

            binding.btnCancel.setOnClickListener { dismiss() }
            binding.btnInput.setOnClickListener {
                val amountStr = binding.edInputAmount.text.toString()
                val notes = binding.edInputNotes.text.toString()

                if (amountStr.isNotEmpty() && notes.isNotEmpty()) {
                    try {
                        val amount = amountStr.toFloat()
                        viewModel.updateInfo(
                            Information(
                                information.id,
                                amount,
                                notes,
                                binding.chipIncome.isChecked,
                                information.date
                            )
                        )
                        Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show()
    }

    fun updateList(list: List<Information>) {
        myList = list
        notifyDataSetChanged()
    }
}
