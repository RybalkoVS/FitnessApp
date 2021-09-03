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
import com.example.fitnessapp.data.model.registration.RegistrationRequest
import com.example.fitnessapp.data.model.registration.RegistrationResponse
import com.example.fitnessapp.data.network.ResponseStatus
import com.example.fitnessapp.getValue
import com.example.fitnessapp.presentation.main.MainActivity
import java.lang.RuntimeException

class RegisterFragment : Fragment(R.layout.fragment_register) {

    companion object {
        const val TAG = "REGISTER_FRAGMENT"
        const val EMAIL_INPUT = "EMAIL_INPUT"
        const val FIRSTNAME_INPUT = "FIRSTNAME_INPUT"
        const val LASTNAME_INPUT = "LASTNAME_INPUT"
        const val PASSWORD_INPUT = "PASSWORD_INPUT"
        const val REPEAT_PASSWORD_INPUT = "REPEAT_PASSWORD_INPUT"

        fun newInstance(args: Bundle?): RegisterFragment {
            val fragment = RegisterFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var authorizationActivityCallback: AuthorizationActivityCallback? = null
    private val autDataValidator = AuthorizationDataValidator()
    private val remoteRepository = FitnessApp.INSTANCE.remoteRepository
    private val toastProvider = FitnessApp.INSTANCE.toastProvider
    private val preferencesStore = FitnessApp.INSTANCE.preferencesStore
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
        moveToLoginBtn.paintFlags = Paint.UNDERLINE_TEXT_FLAG

        registerBtn.setOnClickListener {
            checkEmptyInput()
        }
        moveToLoginBtn.setOnClickListener {
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

    private fun checkEmptyInput() {
        if (autDataValidator.isInputEmpty(
                emailEditText.getValue(),
                firstnameEditText.getValue(),
                lastnameEditText.getValue(),
                passwordEditText.getValue(),
                repeatPasswordEditText.getValue()
            )
        ) {
            toastProvider.showErrorMessage(getString(R.string.empty_fields_toast))
        } else {
            checkEnteredData()
        }
    }

    private fun checkEnteredData() {
        val isDataValid = autDataValidator.isEmailValid(emailEditText.getValue())
                && autDataValidator.isPasswordValid(
            password = passwordEditText.getValue(),
            repeatPassword = repeatPasswordEditText.getValue()
        )
        if (isDataValid) {
            sendRegisterRequest()
        } else {
            toastProvider.showErrorMessage(error = getString(R.string.incorrect_email_or_password_toast))
        }
    }

    private fun sendRegisterRequest() {
        remoteRepository.register(
            registerRequest = RegistrationRequest(
                email = emailEditText.getValue(),
                firstName = firstnameEditText.getValue(),
                lastName = lastnameEditText.getValue(),
                password = passwordEditText.getValue()
            )
        ).continueWith({ task ->
            if (task.error != null) {
                toastProvider.showErrorMessage(error = task.error.message.toString())
            } else {
                checkRegisterResponse(task.result)
            }
        }, Task.UI_THREAD_EXECUTOR)
    }

    private fun checkRegisterResponse(registrationResponse: RegistrationResponse) {
        when (registrationResponse.status) {
            ResponseStatus.OK.toString() -> {
                preferencesStore.saveAuthorizationToken(registrationResponse.token)
                moveToMainScreen()
            }
            ResponseStatus.ERROR.toString() -> {
                toastProvider.showErrorMessage(error = registrationResponse.errorCode)
            }
        }
    }

    private fun moveToMainScreen() {
        val intent = Intent(context, MainActivity::class.java)
        startActivity(intent)
        authorizationActivityCallback?.closeActivity()
    }

    private fun moveToLogin() {
        authorizationActivityCallback?.showFragment(
            LoginFragment.newInstance(null),
            LoginFragment.TAG
        )
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

    private fun saveCurrentEnteredData() {
        val bundle = Bundle().apply {
            putString(EMAIL_INPUT, emailEditText.getValue())
            putString(FIRSTNAME_INPUT, firstnameEditText.getValue())
            putString(LASTNAME_INPUT, lastnameEditText.getValue())
            putString(PASSWORD_INPUT, passwordEditText.getValue())
            putString(REPEAT_PASSWORD_INPUT, repeatPasswordEditText.getValue())
        }
        authorizationActivityCallback?.saveEnteredData(bundle)
    }

    override fun onPause() {
        saveCurrentEnteredData()
        super.onPause()
    }

    override fun onDetach() {
        authorizationActivityCallback = null
        super.onDetach()
    }


}