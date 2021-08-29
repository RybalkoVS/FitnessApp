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
import java.util.regex.Matcher

class RegisterFragment : Fragment(R.layout.fragment_register) {

    companion object {
        const val TAG = "REGISTER_FRAGMENT"
        const val EMAIL_INPUT = "EMAIL_INPUT"
        const val FIRSTNAME_INPUT = "FIRSTNAME_INPUT"
        const val LASTNAME_INPUT = "LASTNAME_INPUT"
        const val PASSWORD_INPUT = "PASSWORD_INPUT"
        const val REPEAT_PASSWORD_INPUT = "REPEAT_PASSWORD_INPUT"
        const val EMAIL_PATTERN = "^[A-Z0-9+_.-]+@[A-Z0-9.-]+\$"
        const val MIN_PASSWORD_LENGTH = 8

        fun newInstance(args: Bundle?): RegisterFragment {
            val fragment = RegisterFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var authorizationActivityCallback: AuthorizationActivityCallback? = null
    private lateinit var registerBtn: Button
    private lateinit var moveToLoginBtn: Button
    private lateinit var emailEditText: EditText
    private lateinit var firstnameEditText: EditText
    private lateinit var lastnameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var repeatPasswordEditText: EditText

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
        underlineLoginButton()

        registerBtn.setOnClickListener {
            if (isInputEmpty()) {
                Toast.makeText(
                    view.context,
                    getString(R.string.empty_fields_toast),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (isEnteredDataValid()) {
                    sendRegisterRequest()
                }
            }
        }
        moveToLoginBtn.setOnClickListener {
            moveToLogin()
        }
        restoreEnteredData(this.arguments)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(outState) {
            putString(EMAIL_INPUT, emailEditText.text.toString())
            putString(FIRSTNAME_INPUT, firstnameEditText.text.toString())
            putString(LASTNAME_INPUT, lastnameEditText.text.toString())
            putString(PASSWORD_INPUT, passwordEditText.text.toString())
            putString(REPEAT_PASSWORD_INPUT, repeatPasswordEditText.text.toString())
        }
        authorizationActivityCallback?.saveEnteredData(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        registerBtn.setOnClickListener(null)
        moveToLoginBtn.setOnClickListener(null)
    }

    override fun onDetach() {
        super.onDetach()
        authorizationActivityCallback = null
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = EMAIL_PATTERN.toRegex()
        return email.matches(pattern)
    }

    private fun isPasswordCorrect(password: String, repeatedPassword: String): Boolean {
        return password == repeatedPassword && password.length >= MIN_PASSWORD_LENGTH
    }

    private fun isEnteredDataValid(): Boolean {
        return isEmailValid(emailEditText.text.toString())
                && isPasswordCorrect(
            passwordEditText.text.toString(),
            repeatPasswordEditText.text.toString()
        )
    }

    private fun moveToLogin() {
        authorizationActivityCallback?.moveToLoginFragment()
    }

    private fun isInputEmpty(): Boolean {
        return emailEditText.text.isEmpty()
                || passwordEditText.text.isEmpty()
                || firstnameEditText.text.isEmpty()
                || lastnameEditText.text.isEmpty()
                || repeatPasswordEditText.text.isEmpty()
    }

    private fun sendRegisterRequest() {

    }

    private fun initViews(v: View) {
        registerBtn = v.findViewById(R.id.btn_register)
        moveToLoginBtn = v.findViewById(R.id.btn_move_to_login)
        emailEditText = v.findViewById(R.id.edit_text_email)
        firstnameEditText = v.findViewById(R.id.edit_text_firstname)
        lastnameEditText = v.findViewById(R.id.edit_text_lastname)
        passwordEditText = v.findViewById(R.id.edit_text_password)
        repeatPasswordEditText = v.findViewById(R.id.edit_text_repeat_password)
    }

    private fun underlineLoginButton() {
        moveToLoginBtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun restoreEnteredData(data: Bundle?) {
        data?.let {
            emailEditText.setText(it.getString(EMAIL_INPUT))
            firstnameEditText.setText(it.getString(FIRSTNAME_INPUT))
            lastnameEditText.setText(it.getString(LASTNAME_INPUT))
            passwordEditText.setText(it.getString(PASSWORD_INPUT))
            repeatPasswordEditText.setText(it.getString(REPEAT_PASSWORD_INPUT))
        }
    }
}