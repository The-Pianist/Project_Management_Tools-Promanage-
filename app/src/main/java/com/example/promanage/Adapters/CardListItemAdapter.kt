package com.example.promanage.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.promanage.R
import com.example.promanage.activities.TaskListActivity
import com.example.promanage.databinding.ItemCardBinding
import com.example.promanage.models.Card
import com.example.promanage.models.SelectedMembers

class CardListItemAdapter(private val context: Context,
                                private val list:ArrayList<Card>)
    :RecyclerView.Adapter<CardListItemAdapter.MyViewHolder>() {
    private var onClickListener: OnClickListener? = null


    class MyViewHolder(binding:ItemCardBinding)
        :RecyclerView.ViewHolder(binding.root){
        val tvCardName=binding.tvCardName
        val rvMemberList=binding.rvCardSelectedMembersList
        val viewLabelColor=binding.viewLabelColor

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemCardBinding.inflate
            (LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = list[position]


        if (holder is MyViewHolder) {

            if (model.labelColor.isNotEmpty()) {
                holder.viewLabelColor.visibility = View.VISIBLE
                holder.viewLabelColor.setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                holder.viewLabelColor.visibility = View.GONE
            }
            holder.tvCardName.text = model.name

            if ((context as TaskListActivity).mAssignedMembersDetailList.size > 0) {
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                for (i in context.mAssignedMembersDetailList.indices) {
                    for (j in model.assignedTo) {
                        if (context.mAssignedMembersDetailList[i].id == j) {
                            val selectedMember = SelectedMembers(
                                context.mAssignedMembersDetailList[i].id,
                                context.mAssignedMembersDetailList[i].image
                            )

                            selectedMembersList.add(selectedMember)
                        }
                    }
                }

                if (selectedMembersList.size > 0) {

                    if (selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                        holder.rvMemberList.visibility = View.GONE
                    } else {
                        holder.rvMemberList.visibility = View.VISIBLE

                        holder.rvMemberList.layoutManager =
                            GridLayoutManager(context, 6)
                        val adapter = CardMemberListItemsAdapter(context, selectedMembersList, false)
                        holder.rvMemberList.adapter = adapter
                        adapter.setOnClickListener(object :
                            CardMemberListItemsAdapter.OnClickListener {
                            override fun onClick() {
                                if (onClickListener != null) {
                                    onClickListener!!.onClick(holder.adapterPosition)
                                }
                            }
                        })
                    }
                } else {
                    holder.rvMemberList.visibility = View.GONE
                }
            }


                holder.itemView.setOnClickListener {
                    if (onClickListener != null) {
                        onClickListener!!.onClick(holder.adapterPosition)
                    }
                }
            }
        }

    override fun getItemCount(): Int {
        return list.size
    }


    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }


    interface OnClickListener {
        fun onClick(cardPosition: Int)
    }

}