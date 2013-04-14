package com.dremok.main;

import java.util.List;

import com.dremok.R;
import com.dremok.model.Movie;
import com.dremok.model.Person;
import com.dremok.services.GenericSeeker;
import com.dremok.services.HttpRetriever;
import com.dremok.services.MovieSeeker;
import com.dremok.services.XmlParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
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
    
    private GenericSeeker<Movie> movieSeeker = new MovieSeeker();
    private GenericSeeker<Person> personSeeker = new PersonSeeker();
    
    private ProgressDialog progressDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        this.findAllViewsById();
        
        moviesSearchRadioButton.setOnClickListener(radioButtonListener);
        peopleSearchRadioButton.setOnClickListener(radioButtonListener);
        
        new HttpRetriever();
        new XmlParser();
        
        searchButton.getBackground().setColorFilter(0xFF00FF00, PorterDuff.Mode.MULTIPLY);
        
        searchButton.setOnClickListener(new OnClickListener() {            
            @Override
            public void onClick(View v) {
                String query = searchEditText.getText().toString();
                performSearch(query);
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
    
    private class PerformMovieSearchTask extends AsyncTask<String, Void, List<Movie>> {
		@Override
		protected List<Movie> doInBackground(String... params) {
			String query = params[0];
			return movieSeeker.find(query);
		}
		
		@Override
		protected void onPostExecute(final List<Movie> result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (result != null) {
						for (Movie movie : result) {
							longToast(movie.name + ", Rating: " + movie.rating);
						}
					}
				}
			});
		}
    }
    
    private class PerformPersonSearchTask extends AsyncTask<String, Void, List<Person>> {
		@Override
		protected List<Person> doInBackground(String... params) {
			String query = params[0];
			return personSeeker.find(query);
		}
		
		@Override
		protected void onPostExecute(final List<Person> result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (progressDialog != null) {
						progressDialog.dismiss();
						progressDialog = null;
					}
					if (result != null) {
						for (Person person : result) {
							longToast(person.name + ", Popularity: " + person.popularity);
						}
					}
				}
			});
		}
    }
    
    private void performSearch(String query) {
    	progressDialog = ProgressDialog.show(AndroidMovieSearchAppProjectActivity.this, "Please wait...", "Performing search...", true, true);
    	if (moviesSearchRadioButton.isChecked()) {
    		PerformMovieSearchTask task = new PerformMovieSearchTask();
    		task.execute(query);
    		progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
    	} else if (peopleSearchRadioButton.isChecked()) {
    		PerformPersonSearchTask task = new PerformPersonSearchTask();
    		task.execute(query);
    		progressDialog.setOnCancelListener(new CancelTaskOnCancelListener(task));
    	}
    }
    
    private class CancelTaskOnCancelListener implements android.content.DialogInterface.OnCancelListener {
    	private AsyncTask<?, ?, ?> task;
    	public CancelTaskOnCancelListener(AsyncTask<?, ?, ?> task) {
    		this.task = task;
    	}
		@Override
		public void onCancel(DialogInterface dialog) {
			if (task != null) {
				task.cancel(true);
			}
		}
    }
    
}