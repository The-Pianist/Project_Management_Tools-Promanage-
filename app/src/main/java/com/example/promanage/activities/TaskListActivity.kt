package com.example.promanage.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.promanage.Adapters.TaskListItemAdapter
import com.example.promanage.R
import com.example.promanage.databinding.ActivityTaskListBinding
import com.example.promanage.firebase.FirestoreClass
import com.example.promanage.models.Board
import com.example.promanage.models.Card
import com.example.promanage.models.Task
import com.example.promanage.models.User
import com.example.promanage.utils.Constants

class TaskListActivity : BaseActivity() {
    private lateinit var mBoardDetail:Board
    private lateinit var mBoardDocumentId:String
    lateinit var mAssignedMembersDetailList: ArrayList<User>

    private var binding:ActivityTaskListBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityTaskListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)

        if(intent.hasExtra(Constants.DOCUMENT_ID)){
            mBoardDocumentId=intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getBoardDetail(this,mBoardDocumentId)


    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.toolbarTaskListActivity)
        val actionbar= supportActionBar
        if(actionbar!=null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionbar.title=mBoardDetail.name
        }
        binding?.toolbarTaskListActivity?.setNavigationOnClickListener { onBackPressed() }
    }

    fun boardDetails(board: Board) {
        mBoardDetail=board
        hideProgressDialog()

        setupActionBar()
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAssignedMembersListDetails(
            this@TaskListActivity,
            mBoardDetail.assignedTo
        )

    }

    fun addUpdateTaskListSuccess(){
        hideProgressDialog()
        FirestoreClass().getBoardDetail(this,mBoardDetail.documentId)
    }

    fun createTaskList(taskListName:String){
        val task=Task(taskListName,FirestoreClass().getCurrentUserId())
        mBoardDetail.taskList.add(0,task)
        mBoardDetail.taskList.removeAt(mBoardDetail.taskList.size-1)

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this,mBoardDetail)
    }

    fun updateTaskList(position:Int,listname:String,model:Task,cardList:ArrayList<Card>){
        val task=Task(listname,model.createdBy,cardList)

        mBoardDetail.taskList[position]=task
        mBoardDetail.taskList.removeAt(position+1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetail)
    }

    fun deleteTaskList(position:Int){
        mBoardDetail.taskList.removeAt(position)
        mBoardDetail.taskList.removeAt(mBoardDetail.taskList.size-1)
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this,mBoardDetail)
    }

    fun addCardToTaskList(position:Int, cardName:String){
        mBoardDetail.taskList.removeAt(mBoardDetail.taskList.size - 1)

        val cardAssignedUsersList: ArrayList<String> = ArrayList()
        cardAssignedUsersList.add(FirestoreClass().getCurrentUserId())

        val card = Card(cardName, FirestoreClass().getCurrentUserId(), cardAssignedUsersList)

        val cardsList = mBoardDetail.taskList[position].cards
        cardsList.add(card)

        val task = Task(
            mBoardDetail.taskList[position].title,
            mBoardDetail.taskList[position].createdBy,
            cardsList
        )

        mBoardDetail.taskList[position] = task

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetail)
    }

    fun cardDetails(taskListPosition:Int,cardPosition:Int){
        val intent=Intent(this,CardDetailActivity::class.java)
        intent.putExtra(Constants.TASK_LIST_ITEM_POSITION,taskListPosition)
        intent.putExtra(Constants.CARD_LIST_ITEM_POSITION,cardPosition)
        intent.putExtra(Constants.DOCUMENT_ID,mBoardDocumentId)
        intent.putExtra(Constants.BOARD_MEMBERS_LIST,mAssignedMembersDetailList)
        startActivityForResult(intent, CARD_DETAIL_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_member,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_member->{
                val intent=Intent(this,MembersActivity::class.java)
                intent.putExtra(Constants.BOARD_DETAIL,mBoardDetail)
                startActivityForResult(intent, MEMBER_CODE)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode== MEMBER_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetail(this,mBoardDocumentId)
        }else if(resultCode== Activity.RESULT_OK&&requestCode== CARD_DETAIL_REQUEST_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardDetail(this,mBoardDocumentId)
        }else{
            Log.e("Cancelled","Cancelled")
        }
    }

    fun boardMembersDetailList(list: ArrayList<User>) {

        mAssignedMembersDetailList = list

        hideProgressDialog()
        val addTaskList = Task(resources.getString(R.string.add_list))
        mBoardDetail.taskList.add(addTaskList)

        binding?.rvTaskList?.layoutManager =
            LinearLayoutManager(this@TaskListActivity, LinearLayoutManager.HORIZONTAL, false)
        binding?.rvTaskList?.setHasFixedSize(true)

        val adapter = TaskListItemAdapter(this@TaskListActivity, mBoardDetail.taskList)
        binding?.rvTaskList?.adapter = adapter
    }

    fun updateCardInTaskList(taskListPosition: Int, cards: ArrayList<Card>) {

        mBoardDetail.taskList.removeAt(mBoardDetail.taskList.size - 1)

        mBoardDetail.taskList[taskListPosition].cards = cards
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().addUpdateTaskList(this@TaskListActivity, mBoardDetail)
    }


    companion object{
        const val MEMBER_CODE:Int=64
        const val CARD_DETAIL_REQUEST_CODE:Int=83
    }
}