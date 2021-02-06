package com.digitalhouse.desafiofirebase.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.digitalhouse.desafiofirebase.R
import com.digitalhouse.desafiofirebase.adapters.HomeAdapter
import com.digitalhouse.desafiofirebase.databinding.FragmentHomeBinding
import com.digitalhouse.desafiofirebase.entities.Game
import com.google.firebase.database.*


class HomeFragment : Fragment(), HomeAdapter.Companion.OnClickGameListener {

    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference
    private var _binding: FragmentHomeBinding? =  null
    private val binding get() = _binding!!
    lateinit var listOfGames: MutableList<Game>
    private lateinit var game: Game




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        connectDB()
        readData()

        binding.btnRegisterGames.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_gameRegisterFragment)
        }

        binding.refreshLayout.setOnRefreshListener {
            readData()
            binding.refreshLayout.isRefreshing = false
        }





        return view
    }

    fun connectDB(){
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Games")
    }

    fun readData(){
        listOfGames = mutableListOf()
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listOfGames = mutableListOf()
                if (dataSnapshot.exists()) {
                    dataSnapshot.children.forEach {
                        val game = it.getValue(Game::class.java)
                        Log.i("game", game.toString())
                        if (game != null) {
                            listOfGames.add(game)

                        }

                    }

                }
                binding.rcGamesHome.apply {
                    adapter = HomeAdapter.HomeAdapter(listOfGames,this@HomeFragment)
                    layoutManager = GridLayoutManager(context, 2)
                    setHasFixedSize(true)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.i("error ", error.toString())
            }
        })


    }

    override fun onClickGame(position: Int) {
        game = listOfGames[position]

        val bundle = bundleOf("title" to game.title,
            "date" to game.date,
            "description" to game.description,
            "img" to game.img,
            "id" to game.id
        )

        findNavController().navigate(R.id.action_homeFragment_to_cardDetailFragment, bundle)
    }


}
