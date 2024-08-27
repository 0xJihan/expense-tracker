import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jihan.expensetracker.R
import com.jihan.expensetracker.databinding.ItemInsertBinding
import com.jihan.expensetracker.room.Information
import java.text.SimpleDateFormat
import java.util.Date

class RecyclerViewAdapter(
    private var myList: List<Information>, // Original list of items
    private val viewModel: com.jihan.expensetracker.model.ViewModel // ViewModel for managing data
) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    private var filteredList: List<Information> =
        myList // List that will be filtered based on search query
    private var query: String = "" // Current search query

    // ViewHolder class to hold and bind views
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNotes: TextView = itemView.findViewById(R.id.tvNotes) // Notes TextView
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount) // Amount TextView
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate) // Date TextView
        private val imageView: ImageView = itemView.findViewById(R.id.imgIcon) // Icon ImageView

        // Bind data to the views
        fun bind(data: Information) {
            tvNotes.text = highlightQuery(data.notes) // Set highlighted notes text
            tvDate.text = dateToTime(data.date) // Set formatted date
            tvAmount.text = if (data.isIncome) {
                tvAmount.setTextColor(Color.GREEN) // Set text color for income
                "${data.amount} TK"
            } else {
                tvAmount.setTextColor(Color.RED) // Set text color for expense
                "- ${data.amount} TK"
            }
            imageView.setImageResource(if (data.isIncome) R.drawable.up else R.drawable.down) // Set icon based on income/expense
        }

        // Format date to a readable string
        private fun dateToTime(date: Date): String {
            val format = SimpleDateFormat("hh:mm a   dd MMM yyyy") // Date format
            return format.format(date)
        }

        // Highlight the search query in the notes text
        private fun highlightQuery(text: String): Spannable {
            val spannable = SpannableString(text) // Create a SpannableString from the text
            if (query.isNotEmpty()) { // Check if there's a query to highlight
                val start = text.indexOf(query, ignoreCase = true) // Find the start of the query
                if (start >= 0) { // If the query is found
                    val end = start + query.length // Determine the end of the query
                    // Highlight the query with a background color
                    spannable.setSpan(
                        BackgroundColorSpan(
                            ContextCompat.getColor(
                                itemView.context, R.color.highlighted_color
                            )
                        ), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return spannable
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Inflate the item layout and create ViewHolder
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = filteredList.size // Return the size of the filtered list

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Bind data to the ViewHolder
        holder.bind(filteredList[position])

        // Set long click listener to show a popup menu
        holder.itemView.setOnLongClickListener {
            showPopupMenu(it, holder.adapterPosition)
            true
        }
    }

    // Show a popup menu with options to edit or delete the item
    private fun showPopupMenu(view: View, position: Int) {
        val popUp = PopupMenu(view.context, view, Gravity.END)
        popUp.inflate(R.menu.pop_up) // Inflate the menu

        // Handle menu item clicks
        popUp.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_edit -> showAlertDialog(
                    view.context, filteredList[position]
                ) // Edit item
                R.id.menu_item_delete -> {
                    viewModel.deleteInfo(filteredList[position]) // Delete item
                    Toast.makeText(view.context, "Item Deleted Successfully", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true
        }

        popUp.show()
    }

    // Show an alert dialog to update the item
    private fun showAlertDialog(context: Context, information: Information) {
        val binding = ItemInsertBinding.inflate(LayoutInflater.from(context))
        val dialog = Dialog(context).apply {
            setContentView(binding.root) // Set dialog content
            binding.btnInput.text = "Update" // Set button text to "Update"
            binding.edInputAmount.setText(information.amount.toInt().toString()) // Set amount
            binding.edInputNotes.setText(information.notes) // Set notes
            binding.chipIncome.isChecked = information.isIncome // Set income checkbox
            binding.chipExpense.isChecked = !information.isIncome // Set expense checkbox

            // Cancel button listener
            binding.btnCancel.setOnClickListener { dismiss() }
            // Update button listener
            binding.btnInput.setOnClickListener {
                val amountStr = binding.edInputAmount.text.toString()
                val notes = binding.edInputNotes.text.toString()

                // Validate input and update item
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
                        dismiss() // Close the dialog
                    } catch (e: NumberFormatException) {
                        Toast.makeText(context, "Invalid Amount", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                }
            }
        }
        dialog.show() // Show the dialog
    }

    // Update the list and notify changes
    fun updateList(list: List<Information>) {
        myList = list
        filteredList = list
        notifyDataSetChanged()
    }

    // Filter the list based on the search query
    fun filter(query: String) {
        this.query = query
        filteredList = if (query.isEmpty()) {
            myList
        } else {
            myList.filter {
                it.notes.contains(query, ignoreCase = true) || it.amount.toString()
                    .contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged() // Notify the adapter of data changes
    }
}
