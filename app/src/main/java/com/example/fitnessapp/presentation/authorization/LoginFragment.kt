package com.example.fitnessapp.presentation.authorization

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import bolts.Task
import com.example.fitnessapp.FitnessApp
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.login.LoginRequest
import com.example.fitnessapp.data.model.login.LoginResponse
import com.example.fitnessapp.data.network.ResponseStatus
import com.example.fitnessapp.getValue
import com.example.fitnessapp.presentation.main.MainActivity
import java.lang.RuntimeException

class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        const val TAG = "LOGIN_FRAGMENT"
        const val EMAIL_INPUT_LOGIN = "EMAIL_INPUT_LOGIN"
        const val PASSWORD_INPUT_LOGIN = "PASSWORD_INPUT_LOGIN"

        fun newInstance(args: Bundle?): LoginFragment {
            val fragment = LoginFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var authorizationActivityCallback: AuthorizationActivityCallback? = null
    private val authDataValidator = AuthorizationDataValidator()
    private val remoteRepository = FitnessApp.INSTANCE.remoteRepository
    private val toastProvider = FitnessApp.INSTANCE.toastProvider
    private val preferencesStore = FitnessApp.INSTANCE.preferencesStore
    private lateinit var loginBtn: Button
    private lateinit var moveToRegisterBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AuthorizationActivityCallback) {
            authorizationActivityCallback = context
        } else {
            throw RuntimeException(context.toString() + getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        moveToRegisterBtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        loginBtn.setOnClickListener {
            checkEmptyInput()
        }
        moveToRegisterBtn.setOnClickListener {
            moveToRegistration()
        }
        restoreEnteredData(this.arguments)
    }

    private fun initViews(v: View) {
        loginBtn = v.findViewById(R.id.btn_login)
        moveToRegisterBtn = v.findViewById(R.id.btn_move_to_register)
        emailEditText = v.findViewById(R.id.edit_text_email)
        passwordEditText = v.findViewById(R.id.edit_text_password)
    }

    private fun checkEmptyInput() {
        if (authDataValidator.isInputEmpty(
                emailEditText.getValue(),
                passwordEditText.getValue()
            )
        ) {
            toastProvider.showErrorMessage(error = getString(R.string.empty_fields_toast))
        } else {
            sendLoginRequest()
        }
    }

    private fun sendLoginRequest() {
        remoteRepository.login(
            LoginRequest(
                email = emailEditText.getValue(),
                password = passwordEditText.getValue()
            )
        ).continueWith({ task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(error = task.error.message.toString())
            } else {
                checkEnteredData(task.result)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun checkEnteredData(loginResponse: LoginResponse) {
        when (loginResponse.status) {
            ResponseStatus.OK.toString() -> {
                preferencesStore.saveAuthorizationToken(loginResponse.token)
                moveToMainScreen()
            }
            ResponseStatus.ERROR.toString() -> {
                toastProvider.showErrorMessage(error = loginResponse.errorCode)
            }
        }
    }

    private fun moveToMainScreen() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        authorizationActivityCallback?.closeActivity()
    }

    private fun moveToRegistration() {
        authorizationActivityCallback?.showFragment(
            RegisterFragment.newInstance(null),
            RegisterFragment.TAG
        )
    }

    private fun restoreEnteredData(data: Bundle?) {
        data?.let {
            emailEditText.setText(it.getString(EMAIL_INPUT_LOGIN))
            passwordEditText.setText(it.getString(PASSWORD_INPUT_LOGIN))
        }
    }

    override fun onPause() {
        saveCurrentEnteredData()
        super.onPause()
    }

    private fun saveCurrentEnteredData() {
        val bundle = Bundle().apply {
            putString(EMAIL_INPUT_LOGIN, emailEditText.getValue())
            putString(PASSWORD_INPUT_LOGIN, passwordEditText.getValue())
        }
        authorizationActivityCallback?.saveEnteredData(bundle)
    }

    override fun onDetach() {
        authorizationActivityCallback = null
        super.onDetach()
    }


}