package com.digitalhouse.desafiofirebase.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.digitalhouse.desafiofirebase.R
import com.digitalhouse.desafiofirebase.databinding.FragmentGameRegisterBinding
import com.digitalhouse.desafiofirebase.entities.Game
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.random.Random

class GameRegisterFragment : Fragment() {

    lateinit var database: FirebaseDatabase
    lateinit var reference: DatabaseReference
    private var _binding: FragmentGameRegisterBinding? = null
    private val binding get() = _binding!!
    lateinit var storageReference: StorageReference
    private var imgURl: String? = null
    private var game: Game? = null
    private val CODE_IMG = 1000



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGameRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        config(Random.nextInt(40, 10000))
        connectDB()
        var update = 0
        var idUpdate: Int = Random.nextInt(40, 10000)

        binding.btnSelectPic.setOnClickListener {
            setIntent()
        }

        arguments?.getInt("update").let {
            if (it != null) {
                update = it
            }
        }

        arguments?.getInt("id").let {
            if (it != null) {
                idUpdate = it
            }
        }

        if(update == 1 ) {
            binding.btnSaveGame.setOnClickListener {
                val title = binding.etNameGame.text.toString()
                val date = binding.etDateGame.text.toString()
                val description = binding.etDescriptionGame.text.toString()
                if(title.isNotEmpty() && date.isNotEmpty() && description.isNotEmpty()) {
                    game = getProduct(idUpdate, title, date, description, imgURl)

                    game?.let { it1 -> UpdateProductDB(it1, game!!.id!!) }

                    val bundle = bundleOf(
                        "title" to game!!.title,
                        "date" to game!!.date,
                        "description" to game!!.description,
                        "img" to game!!.img,
                        "id" to game!!.id
                    )

                    findNavController().navigate(R.id.action_gameRegisterFragment_to_cardDetailFragment, bundle)
                    getActivity()?.getFragmentManager()?.popBackStack()
                }else{
                    Toast.makeText(view.context, "Preencha todos os dados", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            binding.btnSaveGame.setOnClickListener {
                val title = binding.etNameGame.text.toString()
                val date = binding.etDateGame.text.toString()
                val description = binding.etDescriptionGame.text.toString()
                if (title.isNotEmpty() && date.isNotEmpty() && description.isNotEmpty()) {
                    game =
                        getProduct(Random.nextInt(10000, 50000), title, date, description, imgURl)

                    game?.let { it1 -> sendProductDB(it1, game!!.id!!) }

                    activity?.onBackPressed()
                }else{
                    Toast.makeText(view.context, "Preencha todos os dados", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    fun config(id: Int){
        storageReference = FirebaseStorage.getInstance().getReference(id.toString())
    }

    fun setIntent(){
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Captura IMG"), CODE_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CODE_IMG){
            val uploadTask = data?.data?.let { storageReference.putFile(it) }
            uploadTask?.continueWithTask {task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Carregando", Toast.LENGTH_SHORT).show()
                }
                storageReference.downloadUrl
            }?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val url = downloadUri.toString()
                        .substring(0, downloadUri.toString().indexOf("&token"))

                    imgURl = url

                    context?.let {
                        Glide.with(it)
                            .load(downloadUri.toString())
                            .into(binding.imagePreview)
                    }
                }
            }
        }
    }

    fun connectDB(){
        database = FirebaseDatabase.getInstance()
        reference = database.getReference("Games")
    }

    fun getProduct(id: Int, title: String, date: String, description: String, img: String?): Game {

        return Game(id ,title, date, description, img)
    }

    fun sendProductDB(game: Game, id:Int): String{
        var res = reference.child(id.toString()).setValue(game)
        return res.toString()
    }

    fun UpdateProductDB(game: Game, id:Int): String{
        var res = reference.child(id.toString()).setValue(game)
        return res.toString()
    }




}