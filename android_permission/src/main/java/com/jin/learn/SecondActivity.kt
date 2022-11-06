package com.jin.learn

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.jin.learn.adapter.ContactsAdapter
import com.jin.learn.databinding.ActivitySecondBinding


/*
* 读取联系人信息回显到此activity  contentResolver --contentProvider
* */
class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding
    private var contacts = mutableListOf<String>()
    private lateinit var contactsAdapter: ContactsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contactsAdapter = ContactsAdapter(this,contacts)
        binding.contacts.adapter =contactsAdapter
        binding.contacts.layoutManager = LinearLayoutManager(this)

        //申请读取联系人列表
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS),100)
        } else {
            readContacts()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            100 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts()
                } else {
                    Toast.makeText(this,"You denied permission for  read contacts",Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @SuppressLint("Range")
    private fun readContacts() {
        //跨进程读取数据contentResolver
        var cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.apply {
            while(this.moveToNext()) {
                val name = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contacts.add(name + "\n" + number)
            }
            contactsAdapter.notifyDataSetChanged()
            close()
        }
    }
}