package com.example.promanage.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.promanage.Adapters.MemberListItemsAdapter
import com.example.promanage.R
import com.example.promanage.databinding.ActivityMembersBinding
import com.example.promanage.firebase.FirestoreClass
import com.example.promanage.models.Board
import com.example.promanage.models.User
import com.example.promanage.utils.Constants

class MembersActivity : BaseActivity() {
    private lateinit var mBoardDetail:Board
    private lateinit var mAssignedMembersList:ArrayList<User>
    private var binding:ActivityMembersBinding?=null
    private var AnyChange:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityMembersBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)
        setupActionBar()

        if(intent.hasExtra(Constants.BOARD_DETAIL)){
            mBoardDetail=intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(this,mBoardDetail.assignedTo)
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarMembersActivity)
        val actionBar=supportActionBar
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title=resources.getString(R.string.members)
        }
        binding?.toolbarMembersActivity?.setOnClickListener { onBackPressed() }
    }

    fun setupMemberList(list:ArrayList<User>){
        mAssignedMembersList=list
        hideProgressDialog()
        binding?.rvMembersList?.layoutManager=LinearLayoutManager(this)
        binding?.rvMembersList?.setHasFixedSize(true)
        val adapter=MemberListItemsAdapter(this,list)
        binding?.rvMembersList?.adapter=adapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        when (item.itemId){
            R.id.action_add_member->{
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dialogSearchMember(){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_search_member)
        dialog.findViewById<TextView>(R.id.tv_add).setOnClickListener{
            val email=dialog.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.et_email_search_member).text.toString()
            if(email.isNotEmpty()){
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this,email)
                dialog.dismiss()
            }else{ Toast.makeText(this,"Please input the email",Toast.LENGTH_LONG).show() }

        }
        dialog.findViewById<TextView>(R.id.tv_cancel).setOnClickListener { dialog.dismiss() }

        dialog.show()
}

    fun memberDetails(user:User){
        mBoardDetail.assignedTo.add(user.id)
        FirestoreClass().assignMemberToBoard(this,mBoardDetail,user)
    }

    fun memberAssignedSuccess(user:User){
        hideProgressDialog()
        AnyChange=true
        mAssignedMembersList.add(user)
        setupMemberList(mAssignedMembersList)
    }

    override fun onBackPressed() {
        check(AnyChange){
            setResult(Activity.RESULT_OK)
            finish()
        }

        super.onBackPressed()
    }

}