package com.example.booksharing.ui.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.booksharing.R
import com.example.booksharing.databinding.FragmentLibraryBinding

class LibraryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val root = inflater.inflate(R.layout.fragment_library,container,false)

        return root
    }
}