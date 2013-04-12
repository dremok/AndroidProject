package com.dremok.main;

import java.util.ArrayList;

import com.dremok.R;
import com.dremok.model.Movie;
import com.dremok.model.Person;
import com.dremok.services.HttpRetriever;
import com.dremok.services.XmlParser;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AndroidMovieSearchAppProjectActivity extends Activity {
    
    private static final String EMPTY_STRING = "";
    
    private EditText searchEditText;
    private RadioButton moviesSearchRadioButton;
    private RadioButton peopleSearchRadioButton;
    private RadioGroup searchRadioGroup;
    private TextView searchTypeTextView;
    private Button searchButton;
    
    private HttpRetriever retriever;
    private XmlParser parser;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.findAllViewsById();
        
        moviesSearchRadioButton.setOnClickListener(radioButtonListener);
        peopleSearchRadioButton.setOnClickListener(radioButtonListener);
        
        this.retriever = new HttpRetriever();
        this.parser = new XmlParser();
        
        searchButton.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                if (moviesSearchRadioButton.isChecked()) {
                	String xml = retriever.retrieve("http://api.themoviedb.org/2.1/Movie.search/en/xml/c9b207380c2ac6de634dc6d6cc6c7403/" + query.toUpperCase().replace(' ', '+'));
                    ArrayList<Movie> movieList = parser.parseMoviesResponse(xml);
                	longToast(movieList.get(0).overview);
                }
                else if (peopleSearchRadioButton.isChecked()) {
                    String xml = retriever.retrieve("http://api.themoviedb.org/2.1/Person.search/en/xml/c9b207380c2ac6de634dc6d6cc6c7403/" + query.toUpperCase().replace(' ', '+'));
                    ArrayList<Person> personList = parser.parsePeopleResponse(xml);
                	longToast(personList.get(0).biography);
                }
            }
        });
        
        searchEditText.setOnFocusChangeListener(new DftTextOnFocusListener(getString(R.string.search)));
        
        int id = searchRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = (RadioButton) findViewById(id);
        searchTypeTextView.setText(radioButton.getText());
        
    }
    
    private void findAllViewsById() {
        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        moviesSearchRadioButton = (RadioButton) findViewById(R.id.movie_search_radio_button);
        peopleSearchRadioButton = (RadioButton) findViewById(R.id.people_search_radio_button);
        searchRadioGroup = (RadioGroup) findViewById(R.id.search_radio_group);
        searchTypeTextView = (TextView) findViewById(R.id.search_type_text_view);
        searchButton = (Button) findViewById(R.id.search_button);
    }
    
    public void longToast(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    private OnClickListener radioButtonListener = new OnClickListener() {
        public void onClick(View v) {
            RadioButton radioButton = (RadioButton) v;
            searchTypeTextView.setText(radioButton.getText());
        }
    };

    private class DftTextOnFocusListener implements OnFocusChangeListener {
        
        private String defaultText;

        public DftTextOnFocusListener(String defaultText) {
            this.defaultText = defaultText;
        }

        public void onFocusChange(View v, boolean hasFocus) {
            if (v instanceof EditText) {
                EditText focusedEditText = (EditText) v;
                // handle obtaining focus
                if (hasFocus) {
                    if (focusedEditText.getText().toString().equals(defaultText)) {
                        focusedEditText.setText(EMPTY_STRING);
                    }
                }
                // handle losing focus
                else {
                    if (focusedEditText.getText().toString().equals(EMPTY_STRING)) {
                        focusedEditText.setText(defaultText);
                    }
                }
            }
        }
        
    }
    
}