package pardo.tarin.uv.fallas

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import pardo.tarin.uv.fallas.databinding.FragmentLoginBinding
import java.util.Locale

class LoginActivity: AppCompatActivity() {

    lateinit var loginButton: Button
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var registerButton: Button
    lateinit var olvidoButton: TextView
    lateinit var cargaOlvido : View
    private lateinit var fAuth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = FragmentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fAuth = FirebaseAuth.getInstance()

        loginButton = binding.loginButton
        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText
        registerButton = binding.registerButton
        olvidoButton = findViewById(R.id.textOlvideContraseña)
        cargaOlvido = binding.cargaOlvido

        emailEditText.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                v.clearFocus()
                true
            } else {
                false
            }
        }

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
        }

        registerButton.setOnClickListener {
            /*val email = emailEditText.text.toString()
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
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Error")
                builder.setMessage("Campo orreo electrónico o contraseña incorrectas")
            }*/
            val registrarIntent = Intent(this, RegistrarActivity::class.java)
            startActivity(registrarIntent)
        }

        olvidoButton.setOnClickListener{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            if(emailEditText.text.isNotEmpty()){
                db.collection("users").document(emailEditText.text.toString()).get()
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            val user = task.result
                            if(user.exists()){
                                cargaOlvido.visibility = View.VISIBLE
                                resetPsswd()
                            } else {
                                builder.setMessage(getString(R.string.usuarioNoExiste))
                                builder.setPositiveButton("OK", null)
                                val dialog: AlertDialog = builder.create()
                                dialog.show()
                            }
                        }
                    }
            } else {
                builder.setMessage(getString(R.string.correoInvalido))
                builder.setPositiveButton("OK", null)
                val dialog: AlertDialog = builder.create()
                dialog.show()
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

    private fun resetPsswd(){
        fAuth.setLanguageCode(Locale.getDefault().language)
        fAuth.sendPasswordResetEmail(emailEditText.text.toString()).addOnCompleteListener(){ task ->
            if(task.isSuccessful){
                Toast.makeText(this, getString(R.string.correoEnviado), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.correoNoEnviadoError), Toast.LENGTH_SHORT).show()
            }

            cargaOlvido.visibility = View.INVISIBLE
        }
    }
}