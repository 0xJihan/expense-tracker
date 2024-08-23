package com.jihan.expensetracker.Adapter

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


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // initialize the views
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val imageView: ImageView = itemView.findViewById(R.id.imgIcon)

        // function for binding data to the views

        fun bind(data: Information) {
            tvNotes.text = data.notes
            tvDate.text = dateToTime(data.date)

            if (data.isIncome) {
                tvAmount.text = "${data.amount} TK"
                tvAmount.setTextColor(Color.GREEN)
                imageView.setImageResource(R.drawable.up)
            } else {
                tvAmount.text = "- ${data.amount} TK"
                tvAmount.setTextColor(Color.RED)
                imageView.setImageResource(R.drawable.down)
            }

        }

        fun dateToTime(date: Date): String {

            val format = SimpleDateFormat("hh:mm a   dd MMM yyyy")

            return format.format(date)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)

        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (myList.isNullOrEmpty()) {
            return 0
        } else return myList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(myList[position])

        holder.itemView.rootView.setOnLongClickListener {

            // create a popup menu
            val popUp = PopupMenu(it.context, it, Gravity.END)
            // inflate the popup menu
            popUp.inflate(R.menu.pop_up)

            // set menu item click listener
            popUp.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_item_edit -> {

                        showAlertDialog(
                            holder.itemView.context, myList[position]
                        )


                    }

                    R.id.menu_item_delete -> {
                        viewModel.deleteInfo(myList[position])
                        Toast.makeText(
                            holder.itemView.context, "Item Deleted Successfully", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                true
            }

            // display the popup menu
            popUp.show()



            true
        }


    }


    fun showAlertDialog(context: Context, information: Information) {
        val binding = ItemInsertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context)

        dialog.setContentView(binding.root)
        binding.btnInput.text = "Update"
        binding.edInputAmount.setText(information.amount.toInt().toString())
        binding.edInputNotes.setText(information.notes)
        if (information.isIncome) {
            binding.chipIncome.isChecked = true
        } else {
            binding.chipExpense.isChecked = true
        }
        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
        binding.btnInput.setOnClickListener {
            val amount = binding.edInputAmount.text.toString()
            val notes = binding.edInputNotes.text.toString()

            if (amount.isNotEmpty() && notes.isNotEmpty()) {
                try {
                    val amount = amount.toFloat()
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
                    dialog.dismiss()
                    //notifyItemChanged(position)
                } catch (e: Exception) {
                    Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            }
        }


        dialog.show()

    }


    fun updateList(list:List<Information>){
        this.myList = list
        notifyDataSetChanged()
    }


}