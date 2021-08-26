package com.example.fitnessapp.ui.authorization

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.fitnessapp.R
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
        underlineRegisterButton()

        loginBtn.setOnClickListener {
            if (isInputEmpty()) {
                Toast.makeText(
                    view.context,
                    getString(R.string.empty_fields_toast),
                    Toast.LENGTH_SHORT
                ).show()
            } else{
                sendLoginRequest()
            }
        }
        moveToRegisterBtn.setOnClickListener {
            moveToRegistration()
        }
        restoreEnteredData(this.arguments)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putString(EMAIL_INPUT_LOGIN, emailEditText.text.toString())
            putString(PASSWORD_INPUT_LOGIN, passwordEditText.text.toString())
        }
        authorizationActivityCallback?.saveEnteredData(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        loginBtn.setOnClickListener(null)
        moveToRegisterBtn.setOnClickListener(null)
    }

    override fun onDetach() {
        super.onDetach()
        authorizationActivityCallback = null
    }

    private fun moveToRegistration() {
        authorizationActivityCallback?.moveToRegisterFragment()
    }

    private fun isInputEmpty(): Boolean {
        return emailEditText.text.isEmpty() || passwordEditText.text.isEmpty()
    }

    private fun initViews(v: View) {
        loginBtn = v.findViewById(R.id.btn_login)
        moveToRegisterBtn = v.findViewById(R.id.btn_move_to_register)
        emailEditText = v.findViewById(R.id.edit_text_email)
        passwordEditText = v.findViewById(R.id.edit_text_password)
    }

    private fun underlineRegisterButton() {
        moveToRegisterBtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun restoreEnteredData(data: Bundle?) {
        data?.let {
            emailEditText.setText(it.getString(EMAIL_INPUT_LOGIN))
            passwordEditText.setText(it.getString(PASSWORD_INPUT_LOGIN))
        }
    }

    private fun sendLoginRequest() {

    }


}