package com.example.promanage.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.promanage.R
import com.example.promanage.databinding.ItemBoardBinding
import com.example.promanage.models.Board

class BoardItemAdapter(private val context:Context
, private val list:ArrayList<Board>):
    RecyclerView.Adapter<BoardItemAdapter.MyViewHolder>(){

    private var onClickListener:OnClickListener?=null

    class MyViewHolder(binding: ItemBoardBinding):RecyclerView.ViewHolder(binding.root){
        val ivBoardImage=binding.ivBoardImage
        val tvname=binding.tvName
        val createdBy=binding.tvCreatedBy
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemBoardBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model=list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.ivBoardImage)

            holder.tvname.text=model.name
            holder.createdBy.text="Created by: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                if(onClickListener!=null){ onClickListener!!.onClick(position,model) }
            }
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    interface OnClickListener{
        fun onClick(position: Int, model:Board)
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener=onClickListener
    }


}