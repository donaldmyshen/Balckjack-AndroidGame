package com.example.balckjack_mingyuan_fangying.account

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.balckjack_mingyuan_fangying.MainActivity
import com.example.balckjack_mingyuan.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign_in.*

@SuppressLint("ValidFragment")
class SignInFragment(context: Context): Fragment() {
    var firebaseAuth: FirebaseAuth? = FirebaseAuth.getInstance()
    private var parentContext = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return layoutInflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onStart() {
        super.onStart()

        sign_in.setOnClickListener {
            val email = email.text.toString()
            val password = password.text.toString()

            firebaseAuth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener { it2 ->
                if (it2.isSuccessful) {
                    startActivity(Intent(activity, MainActivity::class.java))
                    activity?.finish()

                }
                else {
                    Toast.makeText(parentContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}