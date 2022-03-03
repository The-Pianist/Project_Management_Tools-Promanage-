package com.example.promanage.Adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.promanage.R
import com.example.promanage.activities.TaskListActivity
import com.example.promanage.databinding.ItemTaskBinding
import com.example.promanage.models.Task
import java.util.*
import kotlin.collections.ArrayList

open class TaskListItemAdapter(private val context: Context
                               ,private val list:ArrayList<Task>):
    RecyclerView.Adapter<TaskListItemAdapter.MyViewHolder>() {
    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[holder.adapterPosition]

        if (holder is MyViewHolder) {

            if (position == list.size - 1) {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility = View.GONE
            } else {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<LinearLayout>(R.id.ll_task_item).visibility =
                    View.VISIBLE
            }

            holder.itemView.findViewById<TextView>(R.id.tv_task_list_title).text = model.title

            holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).setOnClickListener {

                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility = View.GONE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_add_task_list_name).visibility =
                    View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_list_name).setOnClickListener {
                holder.itemView.findViewById<TextView>(R.id.tv_add_task_list).visibility =
                    View.VISIBLE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_add_task_list_name).visibility =
                    View.GONE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_list_name).setOnClickListener {
                val listName =
                    holder.itemView.findViewById<EditText>(R.id.et_task_list_name).text.toString()

                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_edit_list_name).setOnClickListener {

                holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name)
                    .setText(model.title)
                holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility =
                    View.GONE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_edit_task_list_name).visibility =
                    View.VISIBLE
            }

            holder.itemView.findViewById<ImageButton>(R.id.ib_close_editable_view)
                .setOnClickListener {
                    holder.itemView.findViewById<LinearLayout>(R.id.ll_title_view).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_edit_task_list_name).visibility =
                        View.GONE
                }

            holder.itemView.findViewById<ImageButton>(R.id.ib_done_edit_list_name)
                .setOnClickListener {
                    val listName =
                        holder.itemView.findViewById<EditText>(R.id.et_edit_task_list_name).text.toString()

                    if (listName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.updateTaskList(
                                holder.adapterPosition,
                                listName,
                                model,
                                model.cards
                            )
                        }
                    } else {
                        Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            holder.itemView.findViewById<ImageButton>(R.id.ib_delete_list).setOnClickListener {

                alertDialogForDeleteList(holder.adapterPosition, model.title)
            }

            holder.itemView.findViewById<TextView>(R.id.tv_add_card).setOnClickListener {

                holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility = View.GONE
                holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_add_card).visibility =
                    View.VISIBLE

                holder.itemView.findViewById<ImageButton>(R.id.ib_close_card_name)
                    .setOnClickListener {
                        holder.itemView.findViewById<TextView>(R.id.tv_add_card).visibility =
                            View.VISIBLE
                        holder.itemView.findViewById<androidx.cardview.widget.CardView>(R.id.cv_add_card).visibility =
                            View.GONE
                    }

                holder.itemView.findViewById<ImageButton>(R.id.ib_done_card_name)
                    .setOnClickListener {

                        val cardName =
                            holder.itemView.findViewById<EditText>(R.id.et_card_name).text.toString()

                        if (cardName.isNotEmpty()) {
                            if (context is TaskListActivity) {
                                context.addCardToTaskList(holder.adapterPosition, cardName)
                            }
                        } else {
                            Toast.makeText(context, "Please Enter Card Detail.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).layoutManager =
                LinearLayoutManager(context)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).setHasFixedSize(true)

            val adapter =
                CardListItemAdapter(context, model.cards)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list).adapter = adapter

            adapter.setOnClickListener(object :
                CardListItemAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(holder.adapterPosition, cardPosition)
                    }
                }
            })

            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            holder.itemView.findViewById<RecyclerView>(R.id.rv_card_list)
                .addItemDecoration(dividerItemDecoration)

            val helper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = dragged.adapterPosition
                    val targetPosition = target.adapterPosition

                    if (mPositionDraggedFrom == -1) {
                        mPositionDraggedFrom = draggedPosition
                    }
                    mPositionDraggedTo = targetPosition

                    Collections.swap(
                        list[holder.adapterPosition].cards,
                        draggedPosition,
                        targetPosition
                    )

                    adapter.notifyItemMoved(draggedPosition, targetPosition)

                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {

                        (context as TaskListActivity).updateCardInTaskList(
                            holder.adapterPosition,
                            list[holder.adapterPosition].cards
                        )
                    }

                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }
            })

            helper.attachToRecyclerView(holder.itemView.findViewById(R.id.rv_card_list))

        }
    }


    override fun getItemCount(): Int {
        return list.size
    }


    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss()

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


}