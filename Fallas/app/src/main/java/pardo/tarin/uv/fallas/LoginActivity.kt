package pardo.tarin.uv.fallas

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import pardo.tarin.uv.fallas.databinding.FragmentLoginBinding
import pardo.tarin.uv.fallas.ui.home.HomeFragment

class LoginActivity: AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var registerButton: Button

    private val db = FirebaseFirestore.getInstance()

    class UsuarioNoExisteException(message: String): Exception(message)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginButton = findViewById(R.id.loginButton)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        registerButton = findViewById(R.id.registerButton)

        setup()
        session()
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        Log.d("EmailLoginSesion", email.toString())
        if (email != null){
            showHome(email)
        }
    }

    private fun setup() {
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Login success
                            showHome(email)
                        } else {
                            // Login failed
                            showAlertLogin(task.exception!!)
                        }
                    }
            }

            registerButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

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
                }
            }
        }
    }

    private fun showAlertLogin(e: Exception){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        var message = ""
        message = when(e){
            is FirebaseAuthInvalidUserException -> getString(R.string.usuarioNoExiste)
            is FirebaseAuthInvalidCredentialsException -> getString(R.string.contrasenaIncorrecta)
            else -> getString(R.string.errorLogin)
        }
        builder.setMessage(message)
        builder.setPositiveButton(getString(R.string.Aceptar), null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
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

    private fun showHome(email: String){
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        Log.d("EmailLogin", email)
        startActivity(homeIntent)
    }
}