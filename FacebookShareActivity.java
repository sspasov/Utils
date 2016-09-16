package com.night_bg.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.night_bg.C;
import com.night_bg.R;
import com.night_bg.utils.AppPrefs;
import com.night_bg.utils.DialogUtils;
import com.night_bg.utils.ScheduleUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class FacebookShareActivity extends AppCompatActivity {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = FacebookShareActivity.class.getSimpleName();

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private CallbackManager callbackManager;

    // ---------------------------------------------------------------------------------------------
    // Interfaces
    // ---------------------------------------------------------------------------------------------
    public interface OnFacebookShareListener {
        void onFacebookShareClick();
    }

    // ---------------------------------------------------------------------------------------------
    // Activity lifecycle
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    // ---------------------------------------------------------------------------------------------
    // Override methods
    // ---------------------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private void createPostRequest(String message, final FacebookCallback<GraphResponse> callback) {
        GraphRequest request = null;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", message);
            request = GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(), "/me/feed",
                jsonObject, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        try {
                            if (response.getConnection()
                                    .getResponseCode() == 200) {
                                Log.d(TAG, "Completed creating post request");
                                Log.d(TAG, response.toString());
                                callback.onSuccess(response);
                            } else {
                                callback.onError(response.getError()
                                    .getException());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (request != null) {
            request.executeAsync();
        }
    }

    private void createShareLinkDialog(String url, String imageUrl,
        final FacebookCallback<Sharer.Result> callback) {
        ShareDialog shareDialog = new ShareDialog(this);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    callback.onSuccess(result);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }

                @Override
                public void onError(FacebookException error) {
                    callback.onError(error);
                }
            });

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "http://" + url;
            }
            ShareLinkContent.Builder linkContentBuilder = new ShareLinkContent.Builder();
            linkContentBuilder.setContentUrl(Uri.parse(url));
            if (imageUrl != null) {
                linkContentBuilder.setImageUrl(Uri.parse(imageUrl));
            }
            ShareLinkContent linkContent = linkContentBuilder.build();
            shareDialog.show(linkContent, ShareDialog.Mode.AUTOMATIC);
        } else {
            callback.onCancel();
        }
    }

    private void createShareImageDialog(Bitmap image,
        final FacebookCallback<Sharer.Result> callback) {
        if (ShareDialog.canShow(SharePhotoContent.class)) {
            ShareDialog shareDialog = new ShareDialog(this);
            shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                @Override
                public void onSuccess(Sharer.Result result) {
                    callback.onSuccess(result);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }

                @Override
                public void onError(FacebookException error) {
                    callback.onError(error);
                }
            });
            SharePhoto photo = new SharePhoto.Builder().setBitmap(image)
                .build();
            SharePhotoContent photoContent = new SharePhotoContent.Builder().addPhoto(photo)
                .build();
            shareDialog.show(photoContent, ShareDialog.Mode.AUTOMATIC);
        } else {
            callback.onCancel();
        }
    }

    private void log(AccessToken accessToken) {
        Log.d(TAG, "UserID=" + accessToken.getUserId());
        Log.d(TAG, "AppID=" + accessToken.getApplicationId());
        Log.d(TAG, "Token=" + accessToken.getToken());
        Log.d(TAG, "Permissions=" + accessToken.getPermissions()
            .toString());
    }

    // ---------------------------------------------------------------------------------------------
    // Protected methods
    // ---------------------------------------------------------------------------------------------
    protected void loginFacebookForSharing(final FacebookCallback<LoginResult> callback) {
        LoginManager.getInstance()
            .logInWithPublishPermissions(FacebookShareActivity.this,
                Arrays.asList("publish_actions"));
        LoginManager.getInstance()
            .registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.d(TAG, "onSuccess()");
                    log(loginResult.getAccessToken());
                    callback.onSuccess(loginResult);
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "onCancel()");
                    callback.onCancel();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "onError()");
                    Log.d(TAG, error.toString());
                    callback.onError(error);
                }
            });
    }

    protected void makeSimplePostInFacebook(final String message,
        final FacebookCallback<GraphResponse> callback) {
        if (AccessToken.getCurrentAccessToken() != null) {
            log(AccessToken.getCurrentAccessToken());
            createPostRequest(message, callback);
        } else {
            loginFacebookForSharing(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    createPostRequest(message, callback);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }

                @Override
                public void onError(FacebookException error) {
                    callback.onError(error);
                }
            });
        }
    }

    protected void shareLinkInFacebook(final String url, final String imageUrl,
        final FacebookCallback<Sharer.Result> callback) {
        if (AccessToken.getCurrentAccessToken() != null) {
            log(AccessToken.getCurrentAccessToken());
            createShareLinkDialog(url, imageUrl, callback);
        } else {
            loginFacebookForSharing(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    createShareLinkDialog(url, imageUrl, callback);
                }

                @Override
                public void onCancel() {
                    callback.onCancel();
                }

                @Override
                public void onError(FacebookException error) {
                    callback.onError(error);
                }
            });
        }
    }

    protected void shareImageInFacebook(final Bitmap image,
        final FacebookCallback<Sharer.Result> callback) {
        if (AccessToken.getCurrentAccessToken() != null) {
            log(AccessToken.getCurrentAccessToken());
            createShareImageDialog(image, callback);
        } else {
            loginFacebookForSharing(new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    createShareImageDialog(image, callback);
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        }
    }
}
