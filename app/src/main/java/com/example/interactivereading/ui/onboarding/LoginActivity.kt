package com.example.interactivereading.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.interactivereading.R
import com.example.interactivereading.databinding.ActivityLoginBinding
import com.example.interactivereading.ui.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val RC_SIGN_IN = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        createSignInRequest()

        binding.signInButton.setSize(SignInButton.SIZE_WIDE)
        binding.signInButton.setOnClickListener { signInWithGoogle() }

        binding.btnLogin.setOnClickListener {
            signInWithEmailAndPassword(
                binding.tetLoginEmailAddress.text.toString(),
                binding.tetLoginPassword.text.toString()
            )
        }

        binding.btnCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun createSignInRequest() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        if (!validateForm()) {
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, redirect user to main application
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // If sign in fails, display a message to the user.
                    when (task.exception) {
                        // if input fields are incorrect (e.g. invalid email or short password)
                        is FirebaseAuthInvalidCredentialsException -> {
                            binding.tvEmailPasswordLoginError.text =
                                (task.exception as FirebaseAuthInvalidCredentialsException).message.toString()
                        }
                        // if user already exists
                        is FirebaseAuthUserCollisionException -> {
                            setErrorMessageOnFormInput(
                                binding.tilEmailAddress,
                                (task.exception as FirebaseAuthUserCollisionException).message.toString()
                            )
                        }
                        else -> {
//                            // reset error messages
                            setErrorMessageOnFormInput(binding.tilEmailAddress, null)
                            setErrorMessageOnFormInput(binding.tilPassword, null)
                            binding.tvEmailPasswordLoginError.text = ""
                            Toast.makeText(
                                baseContext, getString(R.string.authentication_failed),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                if (!task.isSuccessful) {
                    // if too many requests made to login with same account
                    if (task.exception is FirebaseTooManyRequestsException) {
                        binding.tvEmailPasswordLoginError.text =
                            getString(R.string.too_many_requests_error)
                    } else {
                        binding.tvEmailPasswordLoginError.setText(R.string.authentication_failed)
                    }
                }
            }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(
                    baseContext,
                    getString(R.string.google_sign_in_unsuccessful),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Snackbar.make(
                        binding.loginActivityMainLayout,
                        getString(R.string.authentication_failed),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

    /**
     * Validates if the input fields are not empty
     * returns true if inputs of user are valid, else false
     */
    private fun validateForm(): Boolean {
        var isValid = true

        val email = binding.tetLoginEmailAddress.text.toString()
        if (TextUtils.isEmpty(email)) {
            setErrorMessageOnFormInput(binding.tilEmailAddress, getString(R.string.required))
            isValid = false
        } else {
            setErrorMessageOnFormInput(binding.tilEmailAddress, null)
        }

        val password = binding.tetLoginPassword.text.toString()
        if (TextUtils.isEmpty(password)) {
            setErrorMessageOnFormInput(binding.tilPassword, getString(R.string.required))
            isValid = false
        } else {
            setErrorMessageOnFormInput(binding.tilPassword, null)
        }

        return isValid
    }

    /**
     * Displays an error message for a text input
     */
    private fun setErrorMessageOnFormInput(viewLayout: TextInputLayout, errorMessage: String?) {
        viewLayout.error = errorMessage
    }
}