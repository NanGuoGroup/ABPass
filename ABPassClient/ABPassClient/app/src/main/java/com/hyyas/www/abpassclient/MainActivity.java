package com.hyyas.www.abpassclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ABPassNew.Client.Client;
import ABPassNew.Model.Policy;
import ABPassNew.Model.PublicParametersIO;


public class MainActivity extends AppCompatActivity {


    private Handler handler = new Handler();

    private EditText editTextUsername;
    private EditText editTextPassword;

    private Button buttonSubmit;
    private Button buttonRetry;

    private TextView mainTextView;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        if (ClientSingleton.getClient() == null) {
            requestPublicParameters();
        } else {
            if (ClientSingleton.getClient().isSignin()) {
                loggedView();
            } else {
                loadedView();
            }
        }
    }

    private void init() {

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        buttonRetry = (Button) findViewById(R.id.retryButton);
        buttonSubmit = (Button) findViewById(R.id.submitButton);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mainTextView = (TextView) findViewById(R.id.mainTextView);
    }

    //11.29修改，增加final String TAJson参数和final String challengeStr参数
    private void showDialog(final String policyJson, final String TAJson, final String challengeBases64, String domain) {
        final EditText editText = new EditText(this);
        editText.setSingleLine(true);
        editText.setHint("PIN number");
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setPadding(80, 0, 80, 0);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.addView(editText);

        new AlertDialog.Builder(this).setTitle(domain + "\nis requesting your proof .").setIcon(android.R.drawable.ic_dialog_info).setView(linearLayout).setPositiveButton("Submit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String password = editText.getText().toString();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                loadingView();
                                ProofTask proofTask = new ProofTask();
                                proofTask.execute(password, policyJson, TAJson, challengeBases64); //11.29修改，添加参数TAJson和challengeBase64参数
                            }
                        });
                    }
                }).setNegativeButton("cancel", null).show();
    }


    private void requestPublicParameters() {
        loadingView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String publicParametersJson = Client.requestPublicParameters();
                if (publicParametersJson.equals("false")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Cannot connect to server!", Toast.LENGTH_SHORT).show();
                            loadingFailedView();
                        }
                    });
                } else {
                    PublicParametersIO publicParametersIO = PublicParametersIO.getInstance(publicParametersJson);
                    ClientSingleton.setClient(new Client(publicParametersIO));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            loadedView();
                        }
                    });

                }
            }
        }).start();
    }

    private void requestSignIn(final String username, final String password) {
        loadingView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ClientSingleton.getClient().requestSignIn(username, password)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Sign in successful !", Toast.LENGTH_SHORT).show();
                            loggedView();
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Invalid username or password!", Toast.LENGTH_SHORT).show();
                            loginFailedView();
                        }
                    });
                }
            }
        }).start();
    }


    private void loadedView() {
        buttonSubmit.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        buttonRetry.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        mainTextView.setVisibility(View.INVISIBLE);

    }

    private void loadingFailedView() {

        buttonSubmit.setVisibility(View.INVISIBLE);
        editTextPassword.setVisibility(View.INVISIBLE);
        editTextUsername.setVisibility(View.INVISIBLE);
        buttonRetry.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        mainTextView.setVisibility(View.INVISIBLE);
    }

    private void loginFailedView() {
        buttonSubmit.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        buttonRetry.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        mainTextView.setVisibility(View.INVISIBLE);
    }

    private void loggedView() {

        buttonSubmit.setVisibility(View.INVISIBLE);
        editTextPassword.setVisibility(View.INVISIBLE);
        editTextUsername.setVisibility(View.INVISIBLE);
        buttonRetry.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        mainTextView.setVisibility(View.VISIBLE);

//11.15修改为下述代码
//        Intent intent = getIntent();
//        String policyJson = intent.getStringExtra("policy");
//        if (policyJson == null || policyJson.isEmpty()) {
//            return;
//        }
//        showDialog(policyJson, TAJson, challengeBase64, "Movies.com"); //11.29修改，增加TAJson参数

        //11.30修改
        Intent intent = getIntent();
        String policyJson = intent.getStringExtra("policy");
        String TAJson = intent.getStringExtra("TA");
        String challengeBase64 = intent.getStringExtra("challenge");
        if (policyJson == null || policyJson.isEmpty() || TAJson == null || TAJson.isEmpty()) {
            return;
        }
        showDialog(policyJson, TAJson, challengeBase64, "Movies.com"); //11.29修改，增加TAJson参数
    }

    private void loadingView() {
        buttonSubmit.setVisibility(View.INVISIBLE);
        editTextPassword.setVisibility(View.INVISIBLE);
        editTextUsername.setVisibility(View.INVISIBLE);
        buttonRetry.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mainTextView.setVisibility(View.INVISIBLE);
    }


    public void onSubmitClick(View view) {
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        if (username.equals("") || password.equals("")) {
            Toast.makeText(MainActivity.this, "Empty field!", Toast.LENGTH_SHORT).show();
            return;
        }
        requestSignIn(username, password);
    }

    public void onRetryClick(View view) {
        requestPublicParameters();
    }


    protected class ProofTask extends AsyncTask<String, String, Boolean> {
        private String aggregateSignatureBase64;
        private String commitmentBase64; //11.29修改，将proofBase64修改为commitmentBase64
        private String responseJson; //11.30添加
//        private String proofBase64; 11.30修改为上一行

        //11.29修改，增加参数params[2]
        @Override
        protected Boolean doInBackground(String... params) {
            //publishProgress("Requesting token !");
            String password = params[0];
            String policyJson = params[1];
            String TAJson = params[2];
            String challengeBase64 = params[3];

            boolean result = ClientSingleton.getClient().requestCAndgenerateTts(params[0]);
            result = result && ClientSingleton.getClient().requestVerify();
            if (result) {
                //publishProgress("Got token !");
            } else {
                publishProgress("Can not get token !");
                return false;
            }
            //处理策略的方案1：在客户端判断用户的属性是否满足策略，如果不满足，则不生成proof，直接返回false，先注销，将验证工作全部交给验证方
            ArrayList<Policy> policies = Client.getPolicyListFromJson(policyJson);
            ArrayList<String> TA = Client.getTAListFromJson(TAJson);
//            if (!ClientSingleton.getClient().meetPolicy(policies)) {
//                publishProgress("You would not meet the policy !");
//                return false;
//            }
            publishProgress("Requesting signatures !");
            for (Policy policy : policies
                    ) {
                if (ClientSingleton.getClient().getUser().hasValidSignature(policy.getAttribute())) {
                    continue;
                }
                if (!ClientSingleton.getClient().requestSignature(policy.getAttribute())) {
                    publishProgress("Error on requesting signature !");
                    return false;
                }

            }
            publishProgress("Generating proof !");
            aggregateSignatureBase64 = ClientSingleton.getClient().getAggregateSignatureBase64(policies);
            commitmentBase64 = ClientSingleton.getClient().getCommitmentBase64(TA);  //11.30添加
            responseJson = ClientSingleton.getClient().getResponseBase64(password, challengeBase64); //11.30添加
//            proofBase64 = ClientSingleton.getClient().getProofBase64(policies, password); 11.30改为上一行

            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Toast.makeText(MainActivity.this, values[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            Intent resultIntent = new Intent();
            if (result == true) {

                Toast.makeText(MainActivity.this, "Proof is ready !", Toast.LENGTH_SHORT).show();
                resultIntent.putExtra("aggregateSignatureBase64", aggregateSignatureBase64);
                resultIntent.putExtra("commitmentBase64", commitmentBase64); //11.30修改
//                resultIntent.putExtra("proofBase64", proofBase64); 11.30修改为上一行
                resultIntent.putExtra("responseJson", responseJson); //11.30添加

                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                setResult(RESULT_CANCELED, resultIntent);
                finish();
            }
        }


    }


}
