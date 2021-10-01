package com.example.loginkotlin

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    val db = Firebase.firestore

    private var cancellationSignal: CancellationSignal? = null

    private val authentactionCallback: BiometricPrompt.AuthenticationCallback
        get() =
            @RequiresApi(Build.VERSION_CODES.P)
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Erro na autenticação: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser("Autenticado com sucesso")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        checkBiometricSupport()

        btnBiometric.setOnClickListener(View.OnClickListener {
            val biometricPrompt = BiometricPrompt.Builder(this)
                .setTitle("Autenticação Login")
                .setSubtitle("Autenticação necessaria")
                .setDescription("Este aplicativo usa proteção de impressão digital")
                .setNegativeButton(
                    "Cancelar",
                    this.mainExecutor,
                    DialogInterface.OnClickListener { dialog, wich ->
                        notifyUser("Autenticação Cancelada")
                    }).build()

            biometricPrompt.authenticate(
                getCancellationSignal(),
                mainExecutor,
                authentactionCallback
            )
        })

        txtForgot.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@LoginActivity, SendSmsActivity::class.java)
//            intent.putExtra("phoneUser"phone.toString)
//            startActivity(intent)
        })

        txtCreateRegister.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        })

        btnLogin.setOnClickListener(View.OnClickListener {

            if (edtEmail.text.toString().isEmpty() || edtPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_LONG).show()
            } else {
                //Referencia ao id da pessoa no banco
                val emailRef = db.collection("users").document(edtEmail.text.toString()) //
                emailRef.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null) {
                            var userEmail = document.id
                            if (userEmail.equals(edtEmail.text.toString())) {
                                var passwordLogin = document.getString("password")
                                if (passwordLogin.equals(edtPassword.text.toString())) {
                                    Toast.makeText(this, "Entrando", Toast.LENGTH_SHORT).show()
                                }
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Usuario nao encontrado primeiro else",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(this, "Usuario nao segundo else", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }


    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser("Authentication was cancelled by the user")
        }
        return cancellationSignal as CancellationSignal
    }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure) {
            notifyUser("Firepringt authenticatio has not been enabled in settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("Fingerprint authentication permissions is not enabled")
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }


    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    public fun permitionSms(){

    }
}

