package com.example.fitnessapp.presentation.authorization

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fitnessapp.R
import com.example.fitnessapp.presentation.ToastProvider
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
    private var authDataValidator = AuthorizationDataValidator()
    private var toastProvider = ToastProvider(context = context)
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
                emailEditText.text.toString(),
                passwordEditText.text.toString()
            )
        ) {
            toastProvider.showErrorMessage(error = getString(R.string.empty_fields_toast))
        } else {
            sendLoginRequest()
        }
    }

    private fun sendLoginRequest() {
        TODO()
    }

    private fun moveToRegistration() {
        authorizationActivityCallback?.moveToRegisterFragment()
    }

    private fun restoreEnteredData(data: Bundle?) {
        data?.let {
            emailEditText.setText(it.getString(EMAIL_INPUT_LOGIN))
            passwordEditText.setText(it.getString(PASSWORD_INPUT_LOGIN))
        }
    }

    override fun onPause() {
        saveEnteredData()
        super.onPause()
    }

    private fun saveEnteredData() {
        val bundle = Bundle().apply {
            putString(EMAIL_INPUT_LOGIN, emailEditText.text.toString())
            putString(PASSWORD_INPUT_LOGIN, passwordEditText.text.toString())
        }
        authorizationActivityCallback?.saveEnteredData(bundle)
    }

    override fun onDestroy() {
        moveToRegisterBtn.setOnClickListener(null)
        loginBtn.setOnClickListener(null)
        super.onDestroy()
    }

    override fun onDetach() {
        authorizationActivityCallback = null
        super.onDetach()
    }


}