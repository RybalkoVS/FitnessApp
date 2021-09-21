package com.example.fitnessapp.presentation.authorization

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import bolts.Task
import com.example.fitnessapp.DependencyProvider
import com.example.fitnessapp.R
import com.example.fitnessapp.data.model.login.LoginRequest
import com.example.fitnessapp.data.model.login.LoginResponse
import com.example.fitnessapp.data.network.ResponseStatus
import com.example.fitnessapp.getValue
import com.example.fitnessapp.presentation.FragmentContainerActivityCallback
import com.example.fitnessapp.presentation.main.AuthorizationTokenExpiredDialog
import com.example.fitnessapp.presentation.main.MainActivity
import com.example.fitnessapp.showMessage

class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        const val TAG = "LOGIN_FRAGMENT"
        const val EMAIL_INPUT = "EMAIL_INPUT_LOGIN"
        const val PASSWORD_INPUT = "PASSWORD_INPUT_LOGIN"
        private const val SAVED_STATE = "SAVED_STATE"

        fun newInstance() = LoginFragment().apply {
            arguments = Bundle()
        }
    }

    private var fragmentContainerActivityCallback: FragmentContainerActivityCallback? = null
    private val authDataValidator = AuthorizationDataValidator()
    private val remoteRepository = DependencyProvider.remoteRepository
    private val preferencesStore = DependencyProvider.preferencesStore
    private lateinit var loginBtn: Button
    private lateinit var moveToRegisterBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentContainerActivityCallback) {
            fragmentContainerActivityCallback = context
        } else {
            throw RuntimeException(context.toString() + getString(R.string.no_callback_implementation_error))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        moveToRegisterBtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        if (preferencesStore.isTokenExpired(context = requireContext())) {
            showExplanationDialog()
        }

        loginBtn.setOnClickListener {
            checkEmptyInput()
        }
        moveToRegisterBtn.setOnClickListener {
            moveToRegistration()
        }
        savedInstanceState?.let {
            arguments = it.getBundle(SAVED_STATE)
            restoreEnteredData(arguments)
        }
    }

    private fun initViews(v: View) {
        loginBtn = v.findViewById(R.id.btn_login)
        moveToRegisterBtn = v.findViewById(R.id.btn_move_to_register)
        emailEditText = v.findViewById(R.id.edit_text_email)
        passwordEditText = v.findViewById(R.id.edit_text_password)
    }

    private fun showExplanationDialog() {
        AuthorizationTokenExpiredDialog().show(
            childFragmentManager,
            AuthorizationTokenExpiredDialog.TAG
        )
        preferencesStore.setTokenValid(context = requireContext())
    }

    private fun checkEmptyInput() {
        if (authDataValidator.isInputEmpty(
                emailEditText.getValue(),
                passwordEditText.getValue()
            )
        ) {
            context.showMessage(message = getString(R.string.empty_fields_toast))
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
                context.showMessage(message = task.error.message.toString())
            } else {
                checkEnteredData(task.result)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun checkEnteredData(loginResponse: LoginResponse) {
        when (loginResponse.status) {
            ResponseStatus.OK.toString() -> {
                preferencesStore.saveAuthorizationToken(
                    context = requireContext(),
                    token = loginResponse.token
                )
                moveToMainScreen()
            }
            ResponseStatus.ERROR.toString() -> {
                context.showMessage(message = loginResponse.errorCode)
            }
        }
    }

    private fun moveToMainScreen() {
        preferencesStore.setTokenValid(context = requireContext())
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        fragmentContainerActivityCallback?.closeActivity()
    }

    private fun moveToRegistration() {
        fragmentContainerActivityCallback?.showFragment(RegisterFragment.TAG)
    }

    private fun restoreEnteredData(data: Bundle?) {
        data?.let {
            emailEditText.setText(it.getString(EMAIL_INPUT))
            passwordEditText.setText(it.getString(PASSWORD_INPUT))
        }
    }

    override fun onPause() {
        super.onPause()
        arguments?.apply {
            putString(EMAIL_INPUT, emailEditText.getValue())
            putString(PASSWORD_INPUT, passwordEditText.getValue())
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(SAVED_STATE, arguments)
    }

    override fun onDetach() {
        fragmentContainerActivityCallback = null
        super.onDetach()
    }


}