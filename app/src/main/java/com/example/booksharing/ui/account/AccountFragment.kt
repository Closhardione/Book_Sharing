package com.example.booksharing.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.booksharing.R
import com.example.booksharing.ui.firebase_data.Account

class AccountFragment(account:Account) : Fragment() {
    private var currentAccount:Account = account
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_account, container, false)
        return view
    }

}