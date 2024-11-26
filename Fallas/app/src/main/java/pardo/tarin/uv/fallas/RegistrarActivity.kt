package pardo.tarin.uv.fallas

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import pardo.tarin.uv.fallas.databinding.FragmentRegistrarBinding

class RegistrarActivity: AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentRegistrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonRegistrar2.setOnClickListener {
            val email = binding.emailEditTextRegistrar.text.toString()
            val password = binding.passwordEditTextRegistrar.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login success
                            showHome(email)
                            val fallasFav: List<Map<String, Any>> = emptyList()
                            db.collection("users").document(email).set(
                                hashMapOf("email" to email, "favoritas" to fallasFav)
                            )
                        } else {
                            // Login failed
                            showAlertRegister(task.exception!!, password)
                        }
                    }
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Campo correo electrónico o contraseña vacios o inválidos")
                builder.setPositiveButton("OK", null)
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun showHome(email: String){
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        Log.d("EmailLogin", email)
        startActivity(homeIntent)
    }

    private fun showAlertRegister(e: Exception, password: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        var message = ""
        message = if(!isPasswordValid(password)){
            "${getString(R.string.contrasenaInvalida)}\n${getString(R.string.contrasenaRequisitos)}"
        } else {
            when(e){
                is FirebaseAuthWeakPasswordException -> getString(R.string.contrasenaInvalida)
                is FirebaseAuthInvalidCredentialsException -> getString(R.string.correoInvalido)
                is FirebaseAuthUserCollisionException -> getString(R.string.usuarioYaExiste)
                else -> getString(R.string.errorRegistro)
            }
        }
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Aceptar), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.contains(Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+\$).{6,}\$"))
    }
}