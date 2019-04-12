package com.example.balckjack_mingyuan_fangying.account

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.balckjack_mingyuan_fangying.MainActivity
import com.example.balckjack_mingyuan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_create_account.*

@SuppressLint("ValidFragment")
class CreateAccountFragment(context: Context): Fragment() {
    var firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var parentContext = context
    val REQUEST_IMAGE_CAPTURE = 1
    val REQUEST_IMAGE_FROM_GALLERY = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == REQUEST_IMAGE_CAPTURE || requestCode == REQUEST_IMAGE_FROM_GALLERY) && resultCode == Activity.RESULT_OK) {
            profile_pic.setImageBitmap(
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    data?.extras?.get("data") as? Bitmap
                } else {
                    val returnUri = data?.data
                    MediaStore.Images.Media.getBitmap(context!!.contentResolver, returnUri)
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()

        create_account.setOnClickListener {
            val firstName = first_name.text.toString()
            val lastName = last_name.text.toString()
            val email = email.text.toString()
            val username = username.text.toString()
            val password = password.text.toString()

            if (firstName != "" && lastName != "" && email != "" && username != "" && password != "") {
                firebaseAuth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener { task ->
                    // keep not working here
                    if (task.isSuccessful) {
                        Log.d(TAG, "createUserWithEmail:success")
                        val db = FirebaseFirestore.getInstance()
                        val userData = HashMap<String, Any>()
                        userData["first_name"] = firstName
                        userData["last_name"] = lastName
                        userData["email"] = email
                        userData["username"] = username

                        db.document("Player/${firebaseAuth?.currentUser?.uid}")
                            .set(userData)
                            .addOnSuccessListener {
                                startActivity(Intent(activity, MainActivity::class.java))
                                activity?.finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(parentContext, "Failed to write user data", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(parentContext, "Email and/or password unacceptable", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else {
                Toast.makeText(parentContext, "Must fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}