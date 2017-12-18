package com.hyyas.ouy.movies;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ABPassNew.Client.Client;
import ABPassNew.HttpUtil;
import ABPassNew.Model.PairingSingleton;
import ABPassNew.Model.PublicParametersIO;
import ABPassNew.Utils.ElementUtil;
import ABPassNew.Verifier.Movie;
import ABPassNew.Verifier.Verifier;
import it.unisa.dia.gas.jpbc.Element;


public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    private int lastMovieId;

    private String TA;//11.29添加
    private Element challenge; //11.30添加


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (VerifierSingleton.getVerifier() == null) {
            requestPublicParameters();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String ret = HttpUtil.get("http://115.159.211.53/movies.jsp");
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initMovies(ret);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    private void requestPublicParameters() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String publicParametersJson = Client.requestPublicParameters();
                if (publicParametersJson.equals("false")) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "Cannot connect to server , please retry !", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    PublicParametersIO publicParametersIO = PublicParametersIO.getInstance(publicParametersJson);
                    VerifierSingleton.setVerifier(new Verifier(publicParametersIO));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                           Toast.makeText(MainActivity.this, "Verifier is ready !", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }).start();
    }

    private void initMovies(String movieListJson) {
//        ArrayList<Movie> movies = Movies.getMovies();
        ArrayList<Movie> movies = Movies.getMovies(movieListJson);
        TextView[] titleTextViews = new TextView[movies.size()];
        TextView[] summaryTextViews = new TextView[movies.size()];
        ImageView[] imageViews = new ImageView[movies.size()];
        LinearLayout[] movieLinearLayouts = new LinearLayout[movies.size()];

        LinearLayout.LayoutParams movieLinearParameter = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        movieLinearParameter.weight = 1;
        LinearLayout.LayoutParams imageViewParameter = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 700);

        for (int i = 0; i < movies.size(); i++) {
            ImageView imageView = new ImageView(this);
            new LoadImagesTask(imageView).execute(movies.get(i).getImgUrl());
//          imageView.setImageResource(getDrawResourceID(movies.get(i).getImgUrl()));
            imageViews[i] = imageView;
            imageView.setLayoutParams(imageViewParameter);

            TextView titleTextView = new TextView(this);
            titleTextView.setText(movies.get(i).getName());
            titleTextViews[i] = titleTextView;
            titleTextView.setTextColor(Color.DKGRAY);
            titleTextView.setTextSize(20f);
            titleTextView.setPadding(5, 0, 5, 0);

            TextView summaryTextView = new TextView(this);
            summaryTextView.setText(movies.get(i).getSummary());
            summaryTextViews[i] = summaryTextView;
            summaryTextView.setTextColor(Color.DKGRAY);
            summaryTextView.setPadding(5, 0, 5, 0);

            LinearLayout movieLinearLayout = new LinearLayout(this);
            movieLinearLayout.setOrientation(LinearLayout.VERTICAL);
            movieLinearLayout.setPadding(5, 10, 5, 10);
            movieLinearLayout.addView(imageView);
            movieLinearLayout.addView(titleTextView);
            movieLinearLayout.addView(summaryTextView);
            movieLinearLayouts[i] = movieLinearLayout;
            movieLinearLayout.setLayoutParams(movieLinearParameter);
            final int movieIndex = i;
            final String policyJson = Verifier.policiesToJson(movies.get(i).getPolicies());
            final String TAJson = Verifier.TAToJson(movies.get(i).getTA()); //11.29添加
            //12.11添加，方便测试
            if(i == 0){
                TA = new String(TAJson); //11.29添加
            }

            final String warning = movies.get(i).getWarning();
            movieLinearLayouts[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(VerifierSingleton.getVerifier()==null){
                        requestPublicParameters();
                        Toast.makeText(MainActivity.this, "Wait a moment for verifier !", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("观看本视频需要满足:\r\n").setIcon(android.R.drawable.ic_dialog_info);

                    builder.setMessage(warning);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent();
                            intent.setClassName("com.hyyas.www.abpassclient", "com.hyyas.www.abpassclient.MainActivity");
                            intent.putExtra("policy", policyJson);
                            intent.putExtra("TA", TAJson); //11.29添加
                            challenge = PairingSingleton.getPairing().getZr().newRandomElement().getImmutable(); //11.30添加
                            String challengeBase64 = ElementUtil.getBase64OfElement(challenge); //11.30添加
                            intent.putExtra("challenge", challengeBase64);
                            lastMovieId = movieIndex;
                            startActivityForResult(intent, 0);
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();

                }
            });

        }

        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        LinearLayout mainLinearLayout = null;
        for (int i = 0; i < movies.size(); i++) {
            if (i % 2 == 0) {
                mainLinearLayout = new LinearLayout(this);
                mainLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                container.addView(mainLinearLayout);
            }
            mainLinearLayout.addView(movieLinearLayouts[i]);

        }
    }


    /**
     * 根据图片的名称获取对应的资源id
     *
     * @param resourceName
     * @return
     */
    private int getDrawResourceID(String resourceName) {
        ApplicationInfo appInfo = getApplicationInfo();
        int resId = getResources().getIdentifier(resourceName, "drawable", appInfo.packageName);
        return resId;
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (resultCode) { //resultCode为回传的标记
            case RESULT_OK:
//                String proofBase64 = intent.getStringExtra("proofBase64");//11.30修改为下一行
                String commitmentBase64 = intent.getStringExtra("commitmentBase64"); //11.30添加
                String responseJson = intent.getStringExtra("responseJson"); //11.30添加
                String aggregateSignatureBase64 = intent.getStringExtra("aggregateSignatureBase64");
                String TAJson = new String(TA);
                Element c = challenge.duplicate().getImmutable();
//                boolean result = VerifierSingleton.getVerifier().verify(proofBase64, aggregateSignatureBase64); 11.30修改为下一行
                boolean result = VerifierSingleton.getVerifier().verify(aggregateSignatureBase64, commitmentBase64, responseJson, TAJson, c);

                if (result) {
                    Toast.makeText(MainActivity.this, "Your are verified !", Toast.LENGTH_SHORT).show();
                    Intent playerIntent = new Intent(MainActivity.this,PlayerActivity.class);
                    playerIntent.putExtra("id", lastMovieId);
                    startActivity(playerIntent);
                } else {
                    Toast.makeText(MainActivity.this, "Unverified !", Toast.LENGTH_SHORT).show();
                }
                break;
            case RESULT_CANCELED:
                Toast.makeText(MainActivity.this, "User canceled !", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(MainActivity.this, "Unknown error !", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
