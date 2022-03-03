package com.example.promanage.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.promanage.Adapters.BoardItemAdapter
import com.example.promanage.R
import com.example.promanage.databinding.ActivityMainBinding
import com.example.promanage.firebase.FirestoreClass
import com.example.promanage.models.Board
import com.example.promanage.models.User
import com.example.promanage.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var binding:ActivityMainBinding?=null
    private lateinit var userName:String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding= ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding?.root)



        setupActionBar()
        val navView=findViewById<NavigationView>(R.id.nav_view)

        navView.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUser(this,true)

        binding?.appbar?.fabCreateBoard?.setOnClickListener {
            val intent=Intent(this,CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME,userName)
            startActivityForResult(intent,BOARD_CODE)
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(binding?.appbar?.toolbarMainActivity)
        binding?.appbar?.toolbarMainActivity?.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        binding?.appbar?.toolbarMainActivity?.setNavigationOnClickListener {
            toggledrawer()
        }

    }

    private fun toggledrawer(){
        if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            binding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(binding?.drawerLayout!!.isDrawerOpen(GravityCompat.START)){
            binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(Intent(this@MainActivity, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE)
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding?.drawerLayout?.closeDrawer(GravityCompat.START)
        return true
    }

    fun updateNavigationUserDetails(user: User, readBoardList: Boolean) {
        var header=findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.nav_header)
        userName=user.name
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(header)
        var userName=findViewById<TextView>(R.id.tv_username)
        userName.text=user.name

        if(readBoardList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardList(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== Activity.RESULT_OK&&requestCode== MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUser(this)
        }else if(resultCode== Activity.RESULT_OK&&requestCode== BOARD_CODE){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardList(this)
        }
        else{ Log.e("Cancel","Cancel") }
    }

    fun populateBoardListToUI(boardList:ArrayList<Board>){
        hideProgressDialog()
        if(boardList.size>0){
            binding?.appbar?.maincontent?.rvBoardsList?.visibility=View.VISIBLE
            binding?.appbar?.maincontent?.tvNoBoardsAvailable?.visibility=View.GONE

            binding?.appbar?.maincontent?.rvBoardsList?.layoutManager=LinearLayoutManager(this)
            binding?.appbar?.maincontent?.rvBoardsList?.setHasFixedSize(true)
            val adapter=BoardItemAdapter(this,boardList)
            binding?.appbar?.maincontent?.rvBoardsList?.adapter=adapter

            adapter.setOnClickListener(object :
                BoardItemAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        }else{
            binding?.appbar?.maincontent?.rvBoardsList?.visibility=View.GONE
            binding?.appbar?.maincontent?.tvNoBoardsAvailable?.visibility=View.VISIBLE
        }
    }

    companion object{
        const val MY_PROFILE_REQUEST_CODE=11
        const val BOARD_CODE=22
    }

}