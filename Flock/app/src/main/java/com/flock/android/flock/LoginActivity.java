package com.flock.android.flock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Student on 2/2/2016.
 */
public class LoginActivity extends Activity {
    private Button facebookButton;

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.login );

        initUI();

        Backendless.setUrl( Defaults.SERVER_URL );
        Backendless.initApp( this, Defaults.APPLICATION_ID, Defaults.SECRET_KEY, Defaults.VERSION );

        Backendless.UserService.isValidLogin( new DefaultCallback<Boolean>( this )
        {
            @Override
            public void handleResponse( Boolean isValidLogin )
            {
                if( isValidLogin && Backendless.UserService.CurrentUser() == null )
                {
                    String currentUserId = Backendless.UserService.loggedInUser();

                    if( !currentUserId.equals( "" ) )
                    {
                        Backendless.UserService.findById( currentUserId, new DefaultCallback<BackendlessUser>( LoginActivity.this, "Logging in..." )
                        {
                            @Override
                            public void handleResponse( BackendlessUser currentUser )
                            {
                                super.handleResponse( currentUser );
                                Backendless.UserService.setCurrentUser( currentUser );
                                startActivity( new Intent( getBaseContext(), LoginSuccessActivity.class ) );
                                finish();
                            }
                        } );
                    }
                }

                super.handleResponse( isValidLogin );
            }
        });
    }

    private void initUI()
    {
        facebookButton = (Button) findViewById( R.id.loginFacebookButton );

        facebookButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                onLoginWithFacebookButtonClicked();
            }
        } );
    }

    public void onLoginWithFacebookButtonClicked()
    {
        Backendless.UserService.loginWithFacebook( LoginActivity.this, new SocialCallback<BackendlessUser>( LoginActivity.this )
        {
            @Override
            public void handleResponse( BackendlessUser backendlessUser )
            {
                startActivity( new Intent( getBaseContext(), LoginSuccessActivity.class ) );
                finish();
            }
        } );
    }
}
