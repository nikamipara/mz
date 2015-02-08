package com.nikunj.nvideoplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.brightec.example.mediacontroller.R;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nikunj.nvideoplayer.VideoPlayerActivity.MyAdapter;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

public class SearchableActivity extends ActionBarActivity {
	private GridView myGridView;
	/*public static HashMap<String,Bitmap> thumbnaillist = MainActivity.thumbnaillist;*/
	AlphaInAnimationAdapter adapter;
	public ArrayList<File> resultvideos= new ArrayList<File>();
	CustomArrayAdaptor myadapter;
	/*private static int[] color=MainActivity.color;*/
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);
	    setuptoolbar();
	    handleIntent(getIntent());
	    
	    
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default
        return true;
	}
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle action bar item clicks here. The action bar will
	        // automatically handle clicks on the Home/Up button, so long
	        // as you specify a parent activity in AndroidManifest.xml.
	        /*int id = item.getItemId();
	        if (id == R.id.refreshlist) {
	           // suppose to refresh result
	        	//resultvideos = (ArrayList<File>) fileListnew.clone();
*/	           
	       // }
	        
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
			case android.R.id.home :
				NavUtils.navigateUpFromSameTask(this);
				return true;
			case R.id.refreshlist :
				adapter.notifyDataSetChanged();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	private void initviews() {
		TextView t = (TextView) findViewById(R.id.noresults);
		t.setVisibility(View.GONE);
		setupadapter();
		
	}
	private void setupadapter() {
		myGridView = (GridView) findViewById(R.id.searchgridview);
		myadapter = new CustomArrayAdaptor(this,resultvideos);
		adapter = new AlphaInAnimationAdapter(myadapter);
		adapter.setAbsListView(myGridView);
		myGridView.setAdapter(adapter);
		myGridView.setOnItemClickListener(mListItemClickListener);
	}
	private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            File mfile = resultvideos.get(position);
            playfile(view,mfile);
        }
    
		
    };
	private String mQuery;
	private void playfile(View view2, File mfile) {

		Uri sourceUri = Util.converttoUri(mfile.toURI());
		Intent intent = new Intent(this, VideoPlayerActivity.class);
		intent.setData(sourceUri);
		intent.putExtra("keyword", mQuery);
		startActivity(intent);
		
		/*
		 * ActivityOptionsCompat options =
		 * ActivityOptionsCompat.makeSceneTransitionAnimation( this, view2,
		 * VideoPlayerActivity.EXTRA_VIDEO); ActivityCompat.startActivity(this,
		 * intent, options.toBundle());
		 */

	}
	private void setuptoolbar() {
		android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbarsearch);
        if (toolbar != null) {
        	setSupportActionBar(toolbar);/*setSupportActionBar(toolbar);*/
        	getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        	getSupportActionBar().setTitle("Results..");
        }
	}
	@Override
	protected void onNewIntent(Intent intent) {
	    setIntent(intent);
	    handleIntent(intent);
	}
	private void handleIntent(Intent intent) {
		initviews();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      mQuery = intent.getStringExtra(SearchManager.QUERY);
	      savequerytosearchhistory(mQuery);
	      doMySearch(mQuery);
	     // clearsearchHistory()
	    }
	}
	private void clearsearchHistory() {
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
		        SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
		suggestions.clearHistory();
		
	}
	private void savequerytosearchhistory(String query) {
		SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);
		
	}
	
	private void doMySearch(String query) {
		/*TextView t = (TextView) findViewById(R.id.textview);
		t.setText("searching for"+query);*/
		getSupportActionBar().setTitle("Results for "+query);
		new search().execute(query);
		
		
	}
	private class search extends AsyncTask<String,Void,String>{
        ProgressDialog pDialog;
        @Override
         protected void onPreExecute(){
            pDialog = new ProgressDialog(SearchableActivity.this);
            pDialog.setMessage("Fetching Results...");
            pDialog.show();
         }
        @Override
        protected String doInBackground(String... query) {
        	resultvideos = Util.searchallfiles(FileManagerActivity.fileList, query[0]);
            return null;
        }
        protected void onPostExecute(String params){
            super.onPostExecute(params);

            pDialog.dismiss();
            if(resultvideos==null||resultvideos.isEmpty()){
            	TextView t = (TextView) findViewById(R.id.noresults);
        		t.setVisibility(View.VISIBLE);
            }
            StringBuffer s = new StringBuffer();
    		for(File f :resultvideos){
    			s.append(f.getAbsolutePath()+" ");
    		}
    		/*TextView t = (TextView) findViewById(R.id.textview);
    		t.setText(s.toString());*/
    		//initviews();
    		myadapter.clear();
    		myadapter.addAll(resultvideos);
    		/*myadapter.notifyDataSetChanged();
    		myGridView.invalidate();*/
        }

     }
}
