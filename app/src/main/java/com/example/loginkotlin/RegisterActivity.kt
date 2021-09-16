package com.example.loginkotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        btnCadastrar.setOnClickListener(View.OnClickListener {

            //chamando a classe people
            if (edtNameRegister.text.toString().isEmpty() && edtEmailRegister.text.toString()
                    .isEmpty() &&
                edtPasswordRegister.text.toString().isEmpty() && edtPhoneRegister.text.toString()
                    .isEmpty()
            ) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            }
            val user = People(
                edtNameRegister.text.toString(), edtEmailRegister.text.toString(),
                edtPasswordRegister.text.toString(), edtPhoneRegister.text.toString()
            )

            db.collection("users")//Nome da coleção inicial
                .document(edtEmailRegister.text.toString())//colocando email do usuario como id
                .set(user)//Setando o user dentro da coleção

                .addOnSuccessListener {
                    Toast.makeText(this, "Cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("user", user.toString())
                    startActivity(intent)

                }

                .addOnFailureListener(OnFailureListener {
                    Toast.makeText(this, "Revise seus dados", Toast.LENGTH_LONG).show()
                })

        })
    }
}