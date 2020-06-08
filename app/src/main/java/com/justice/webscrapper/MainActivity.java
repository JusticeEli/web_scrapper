package com.justice.webscrapper;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private EditText websiteEdtTxt;
    private EditText keywordEdtTxt;
    private EditText bottomDataEdtTxt;
    private TextView bottomKeywordTxtView;
    private ExtendedFloatingActionButton searchFob;
    private ExtendedFloatingActionButton nextFob;
    private BottomSheetBehavior bottomSheet;
    private LinearLayout linearLayout;
    private String website;
    private String keyword;
    private String fullString;
    private Matcher matcher;
    private Pattern p;

    private  volatile boolean searching = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        searchFob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searching){
                    progressBar.setVisibility(View.VISIBLE);
                }


                searchForKeyword();
            }
        });
        nextFob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callFind();
            }
        });
    }

    private void searchForKeyword() {
        website = websiteEdtTxt.getText().toString().trim();
        keyword = keywordEdtTxt.getText().toString().trim();

        if (field_is_empty()) {
            return;
        }

        go_to_website_and_search_for_word();


    }

    private void go_to_website_and_search_for_word() {

        new BackgroundAsycnTask().execute();
    }

    private boolean field_is_empty() {
        if (website.isEmpty()) {
            websiteEdtTxt.setError("Please fill Field");
            websiteEdtTxt.requestFocus();
            return true;
        }
        if (keyword.isEmpty()) {
            keywordEdtTxt.setError("Please fill Field");
            keywordEdtTxt.requestFocus();
            return true;
        }
        return false;
    }

    private void initWidgets() {
        progressBar=findViewById(R.id.progressBar);
        websiteEdtTxt = findViewById(R.id.websiteEdtTxt);
        keywordEdtTxt = findViewById(R.id.keywordEdtTxt);
        bottomDataEdtTxt = findViewById(R.id.bottomDataEdtTxt);
        bottomKeywordTxtView = findViewById(R.id.bottomKeywordTxtView);
        searchFob=findViewById(R.id.searchFob);
        nextFob=findViewById(R.id.nextFob);
        linearLayout = findViewById(R.id.bottomSheet);
        bottomSheet = BottomSheetBehavior.from(linearLayout);


    }


    private void isContainExactWord(String fullString, String partWord) {
        String pattern = "\\b" + partWord + "\\b";
         p = Pattern.compile(pattern);
        matcher = p.matcher(fullString);

        callFind();

    }

    private void callFind() {

        if (!searching){
            matcher = p.matcher(fullString);
        }

        if (matcher.find()) {
            nextFob.setVisibility(View.VISIBLE);
            high_light_found_text(matcher);
        } else {
            bottomKeywordTxtView.setText("Not Found");
            nextFob.setVisibility(View.INVISIBLE);
        }
    }

    private void high_light_found_text(Matcher m) {
        bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomKeywordTxtView.setText(keyword);

        ForegroundColorSpan spanGreen = new ForegroundColorSpan(Color.GREEN);
        SpannableString ss = new SpannableString(fullString);
        ss.setSpan(spanGreen, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        bottomDataEdtTxt.setText(ss);
        bottomDataEdtTxt.setSelection(m.start());


    }

    class BackgroundAsycnTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Document document = null;
            try {
                document = Jsoup.connect(website).get();
                fullString = document.text();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return fullString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            isContainExactWord(fullString, keyword);



            if (searching) {
                searchFob.setIcon(getDrawable(R.drawable.ic_search));
                searchFob.setText("search");

                 searching =false;
            } else {
                searchFob.setIcon(getDrawable(R.drawable.ic_reset));
                searchFob.setText("reset");
                searching =true;
            }

        }
    }
}
