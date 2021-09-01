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
import java.lang.RuntimeException

class RegisterFragment : Fragment(R.layout.fragment_register) {

    companion object {
        const val TAG = "REGISTER_FRAGMENT"
        const val EMAIL_INPUT = "EMAIL_INPUT"
        const val FIRSTNAME_INPUT = "FIRSTNAME_INPUT"
        const val LASTNAME_INPUT = "LASTNAME_INPUT"
        const val PASSWORD_INPUT = "PASSWORD_INPUT"
        const val REPEAT_PASSWORD_INPUT = "REPEAT_PASSWORD_INPUT"
        const val EMAIL_PATTERN = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"

        fun newInstance(args: Bundle?): RegisterFragment {
            val fragment = RegisterFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var authorizationActivityCallback: AuthorizationActivityCallback? = null
    private var registerBtn: Button? = null
    private var moveToLoginBtn: Button? = null
    private var emailEditText: EditText? = null
    private var firstnameEditText: EditText? = null
    private var lastnameEditText: EditText? = null
    private var passwordEditText: EditText? = null
    private var repeatPasswordEditText: EditText? = null

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

        registerBtn?.setOnClickListener {
            if (isInputEmpty()) {
                showEmptyFieldsNotification()
            } else {
                if (isEnteredDataValid()) {
                    sendRegisterRequest()
                }
            }
        }
        moveToLoginBtn?.setOnClickListener {
            moveToLogin()
        }
        restoreEnteredData(this.arguments)
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
        moveToLoginBtn?.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }

    private fun isInputEmpty(): Boolean {
        return emailEditText?.text.isNullOrEmpty()
                || passwordEditText?.text.isNullOrEmpty()
                || firstnameEditText?.text.isNullOrEmpty()
                || lastnameEditText?.text.isNullOrEmpty()
                || repeatPasswordEditText?.text.isNullOrEmpty()
    }

    private fun showEmptyFieldsNotification() {
        Toast.makeText(
            context,
            getString(R.string.empty_fields_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun isEnteredDataValid(): Boolean {
        val isDataValid = isEmailValid(emailEditText?.text.toString())
                && doPasswordsMatch(
            passwordEditText?.text.toString(),
            repeatPasswordEditText?.text.toString()
        )
        if (!isDataValid) {
            showIncorrectDataNotification()
        }
        return isDataValid
    }

    private fun isEmailValid(email: String): Boolean {
        val pattern = EMAIL_PATTERN.toRegex()
        return email.matches(pattern)
    }

    private fun doPasswordsMatch(password: String, repeatedPassword: String): Boolean {
        return password == repeatedPassword
    }

    private fun showIncorrectDataNotification() {
        Toast.makeText(
            context,
            getString(R.string.incorrect_email_or_password_toast),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun sendRegisterRequest() {
        TODO()
    }

    private fun moveToLogin() {
        authorizationActivityCallback?.moveToLoginFragment()
    }

    private fun restoreEnteredData(data: Bundle?) {
        data?.let {
            emailEditText?.setText(it.getString(EMAIL_INPUT))
            firstnameEditText?.setText(it.getString(FIRSTNAME_INPUT))
            lastnameEditText?.setText(it.getString(LASTNAME_INPUT))
            passwordEditText?.setText(it.getString(PASSWORD_INPUT))
            repeatPasswordEditText?.setText(it.getString(REPEAT_PASSWORD_INPUT))
        }
    }

    private fun saveEnteredData() {
        val bundle = Bundle()
        with(bundle) {
            putString(EMAIL_INPUT, emailEditText?.text.toString())
            putString(FIRSTNAME_INPUT, firstnameEditText?.text.toString())
            putString(LASTNAME_INPUT, lastnameEditText?.text.toString())
            putString(PASSWORD_INPUT, passwordEditText?.text.toString())
            putString(REPEAT_PASSWORD_INPUT, repeatPasswordEditText?.text.toString())
        }
        authorizationActivityCallback?.saveEnteredData(bundle)
    }

    override fun onStop() {
        super.onStop()
        saveEnteredData()
    }

    override fun onDestroy() {
        super.onDestroy()
        registerBtn?.setOnClickListener(null)
        moveToLoginBtn?.setOnClickListener(null)
    }

    override fun onDetach() {
        super.onDetach()
        authorizationActivityCallback = null
    }


}