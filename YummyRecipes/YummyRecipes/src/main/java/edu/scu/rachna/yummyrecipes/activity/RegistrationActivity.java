package edu.scu.rachna.yummyrecipes.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;



import edu.scu.rachna.yummyrecipes.R;
import edu.scu.rachna.yummyrecipes.data.Default;
import edu.scu.rachna.yummyrecipes.data.LoadingCallback;
import edu.scu.rachna.yummyrecipes.data.Validator;


public class RegistrationActivity extends Activity
{
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_registration );

        Backendless.initApp(this, Default.APPLICATION_ID, Default.ANDROID_SECRET_KEY, Default.VERSION);

        Button registerButton = (Button) findViewById( R.id.registerButton );

        View.OnClickListener registerButtonClickListener = createRegisterButtonClickListener();

        registerButton.setOnClickListener( registerButtonClickListener );
    }


    public boolean isRegistrationValuesValid( CharSequence name, CharSequence email, CharSequence password, CharSequence passwordConfirm )
    {
        return Validator.isNameValid(this, name)
                && Validator.isEmailValid( this, email )
                && Validator.isPasswordValid( this, password )
                && isPasswordsMatch( password, passwordConfirm );
    }


    public boolean isPasswordsMatch( CharSequence password, CharSequence passwordConfirm )
    {
        if( !TextUtils.equals(password, passwordConfirm) )
        {
            Toast.makeText(this, getString(R.string.warning_passwords_do_not_match), Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public void registerUser( String name, String email, String password, AsyncCallback<BackendlessUser> registrationCallback )
    {
        BackendlessUser user = new BackendlessUser();
        user.setEmail( email );
        user.setPassword( password );
        user.setProperty( "name", name );

        Backendless.UserService.register( user, registrationCallback );
    }

    public LoadingCallback<BackendlessUser> createRegistrationCallback()
    {
        return new LoadingCallback<BackendlessUser>( this, getString( R.string.loading_register ) )
        {
            @Override
            public void handleResponse( BackendlessUser registeredUser )
            {
                super.handleResponse( registeredUser );
                Intent registrationResult = new Intent();
                registrationResult.putExtra( BackendlessUser.EMAIL_KEY, registeredUser.getEmail() );
                setResult( RESULT_OK, registrationResult );
                RegistrationActivity.this.finish();
            }
        };
    }

    public View.OnClickListener createRegisterButtonClickListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                EditText nameField = (EditText) findViewById( R.id.nameField );
                EditText emailField = (EditText) findViewById( R.id.emailField );
                EditText passwordField = (EditText) findViewById( R.id.passwordField );
                EditText passwordConfirmField = (EditText) findViewById( R.id.passwordConfirmField );

                CharSequence name = nameField.getText();
                CharSequence email = emailField.getText();
                CharSequence password = passwordField.getText();
                CharSequence passwordConfirmation = passwordConfirmField.getText();

                if( isRegistrationValuesValid( name, email, password, passwordConfirmation ) )
                {
                    LoadingCallback<BackendlessUser> registrationCallback = createRegistrationCallback();

                    registrationCallback.showLoading();
                    registerUser( name.toString(), email.toString(), password.toString(), registrationCallback );
                }
            }
        };
    }
}

