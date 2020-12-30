package com.example.interactivereading.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.interactivereading.R
import com.example.interactivereading.databinding.ActivityRegisterBinding
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.btnSignupLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnCreateAccount.setOnClickListener {
            createAccount(
                binding.tetSignUpEmail.text.toString(),
                binding.tetSignUpPassword.text.toString()
            )
        }
    }

    /**
     * Creates an account based on the email and password input fields
     * The user is informed if the sign in fails
     */
    private fun createAccount(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    when (task.exception) {
                        // if input fields are incorrect (e.g. invalid email or short password)
                        is FirebaseAuthInvalidCredentialsException -> {
                            binding.tvPasswordOrEmailError.text =
                                (task.exception as FirebaseAuthInvalidCredentialsException).message.toString()
                        }
                        // if user already exists
                        is FirebaseAuthUserCollisionException -> {
                            setErrorMessageOnFormInput(
                                binding.tilSignUpEmail,
                                (task.exception as FirebaseAuthUserCollisionException).message.toString()
                            )
                        }
                        else -> {
//                            // reset error messages
                            setErrorMessageOnFormInput(binding.tilSignUpEmail, null)
                            setErrorMessageOnFormInput(binding.tilSignUpPassword, null)
                            Toast.makeText(
                                baseContext, getString(R.string.authentication_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
    }

    /**
     * Validates if the input fields are not empty
     * returns true if inputs of user are valid, else false
     */
    private fun validateForm(): Boolean {
        var isValid = true

        val email = binding.tetSignUpEmail.text.toString()
        if (TextUtils.isEmpty(email)) {
            setErrorMessageOnFormInput(binding.tilSignUpEmail, getString(R.string.required))
            isValid = false
        } else {
            setErrorMessageOnFormInput(binding.tilSignUpEmail, null)
        }

        val password = binding.tetSignUpPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            setErrorMessageOnFormInput(binding.tilSignUpPassword, getString(R.string.required))
            isValid = false
        } else {
            setErrorMessageOnFormInput(binding.tilSignUpPassword, null)
        }

        return isValid
    }

    /**
     * Displays an error message for a text input
     */
    private fun setErrorMessageOnFormInput(viewLayout: TextInputLayout, errorMessage: String?) {
        // the property isErrorEnabled is necessary to set the error
        viewLayout.isErrorEnabled = true
        viewLayout.error = errorMessage
    }
}