package com.example.sastore.view

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sastore.R
import com.example.sastore.adapter.ShoppingPostsListAdapter
import com.example.sastore.databinding.FragmentNavBottomHomeBinding
import com.example.sastore.model.ShoppingPostModel
import com.example.sastore.viewmodel.MainActivityViewModel

class HomeNavigationFragment : Fragment() {
    lateinit var binding: FragmentNavBottomHomeBinding
    lateinit var viewModel:MainActivityViewModel
    var shoppingPostsListAdapter = ShoppingPostsListAdapter()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_nav_bottom_home,container,false)
        var recyclerView = binding.fragmentHomeRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = shoppingPostsListAdapter
        viewModel = ViewModelProvider(requireActivity()).get(MainActivityViewModel::class.java)
        viewModel.getShoppingPosts().observe(viewLifecycleOwner,object :
            Observer<List<ShoppingPostModel>>{
            override fun onChanged(t: List<ShoppingPostModel>?) {
                shoppingPostsListAdapter.setShoppingPostModelList(t!!)
            }
        })
              return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}