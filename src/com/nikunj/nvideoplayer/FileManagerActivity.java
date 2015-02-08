package com.nikunj.nvideoplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import uk.co.brightec.example.mediacontroller.R;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

public class FileManagerActivity extends ActionBarActivity  {
	public static ArrayList<File> fileList = new ArrayList<File>();
	private ArrayList<File> fileListnew = new ArrayList<File>();
	private File root;
	private GridView myGridView;
	public static HashMap<String,Bitmap> thumbnaillist =new HashMap<String,Bitmap>();  
	AlphaInAnimationAdapter adapter;
	private CustomArrayAdaptor myadapter;
	static Random rand = new Random();
	//private String title;
	public static int[] color;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setupcolorarray();
    	//getActionBar().setBackgroundDrawable(R.color.titeactionbar);
    	//title = "Magic Video Player";
    	//getActionBar().setTitle(title);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
        	setSupportActionBar(toolbar);/*setSupportActionBar(toolbar);*/
        	//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        	
        }
        
       /* Button selectVideo = (Button) findViewById(R.id.btn_selectvideo);
        selectVideo.setVisibility(View.INVISIBLE);
        selectVideo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Video File to Play"), 0);
            }
        });*/
       // setlist();

        /*Button searchDlg = (Button) findViewById(R.id.btn_searchdlg);
        searchDlg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSearchDialogsClick();
            }
        });*/
        
        
        
       /* view = (LinearLayout) findViewById(R.id.filelist);*/
      //getting SDcard root path
      		/*root = new File(Environment.getExternalStorageDirectory()
      				.getAbsolutePath()+"/Movies");
      		getfile(root);*/
        loadfileList();
		updatepref();
        initviews();

    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	myGridView.setNumColumns(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE ? 3 : 2);
    	super.onConfigurationChanged(newConfig);
    	//myGridView.invalidate();
    }
	private void updatepref() {
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {

				updateprefp();
			}
		});
		t.start();
		
	}
	private void setupcolorarray() {
		// TODO Auto-generated method stub
		color = new int[10];
		color[0] =0xff00695C;
		color[1] =0xff424242; 
		color[2] =0xffBF360C;
		color[3] =0xff4527A0;
		color[4] =0xff455A64;
		color[5] =0xff283593;
		color[6] =0xff2E7D32;
		color[7] =0xff6A1B9A;
		color[8] =0xff1565C0;
		color[9] =0xff0277BD;
	}
	private void loadfileList() {
		if (null == fileList) {
			fileList = new ArrayList<File>();
		}
		updatefilelist();
		// load tasks from preference
		SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE,
				Context.MODE_PRIVATE);
		try {
			fileList = (ArrayList<File>) ObjectSerializer.deserialize(prefs
					.getString(TASKS,
							ObjectSerializer.serialize(new ArrayList<File>())));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void updatefilelist() {
		// TODO Auto-generated method stub
		String[] projection = { MediaStore.Video.Media._ID};
		Cursor cursor = new CursorLoader(this, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, 
		            null, // Return all rows
		            null, null).loadInBackground();
		
		StringBuffer s = new StringBuffer();
		for(String ss :cursor.getColumnNames())s.append(ss);
	Log.d("TAG","returned columns are " +s.toString() +"and the total no of videos are  "+cursor.getCount()
			+"cursor data is "+cursor.getString(0));
		int i = 10;//check point.
	}
	private void initviews() {
	
     myGridView = (GridView) findViewById(R.id.listviewfile);
     myadapter = new CustomArrayAdaptor(this,fileList);
     adapter = new AlphaInAnimationAdapter(myadapter);
     adapter.setAbsListView(myGridView);
     myGridView.setAdapter(adapter);
     myGridView.setOnItemClickListener(mListItemClickListener);
     
     
     

     
	}
    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            File mfile = fileList.get(position);
            playfile(view,mfile);
        }

		
    };
	private String SHARED_PREFS_FILE= "filelistsharedpref";
	private String TASKS= "MyFILES";
    private void playfile(View view2, File mfile) {
    	
    	Uri sourceUri  = SearchUtil.converttoUri(mfile.toURI());
    	Intent intent = new Intent(this, VideoPlayerActivity.class);
        intent.setData(sourceUri);
        startActivity(intent);
        
        /*ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
        	     this, view2, VideoPlayerActivity.EXTRA_VIDEO);
        	ActivityCompat.startActivity(this, intent,
        	options.toBundle());*/
		
	}
	/*private Uri converttoUri(URI sourceURI) {
		return new Uri.Builder().scheme(sourceURI.getScheme())
				.encodedAuthority(sourceURI.getRawAuthority())
				.encodedPath(sourceURI.getRawPath())
				.query(sourceURI.getRawQuery())
				.fragment(sourceURI.getRawFragment()).build();
	}*/
	public ArrayList<File> getfile(File dir) {
		File listFile[] = dir.listFiles();
		if (listFile != null && listFile.length > 0) {
			for (int i = 0; i < listFile.length; i++) {

				if (listFile[i].isDirectory()) {
					//fileList.add(listFile[i]);
					getfile(listFile[i]);

				} else {
					if (listFile[i].getName().endsWith(".mkv")
							|| listFile[i].getName().endsWith(".mp4")
							|| listFile[i].getName().endsWith(".avi")
							/*|| listFile[i].getName().endsWith(".jpeg")
							|| listFile[i].getName().endsWith(".gif")*/)

					{
						fileListnew.add(listFile[i]);
					}
				}

			}
		}
		return fileListnew;
	}
/*	private void setlist() {
		// Use the current directory as title
		path = "/";
		if (getIntent().hasExtra("path")) {
			path = getIntent().getStringExtra("path");
		}
		setTitle(path);
		// Read all files sorted into the values-array
		List values = new ArrayList();
		File dir = new File(path);
		if (!dir.canRead()) {
			setTitle(getTitle() + " (inaccessible)");
		}
		String[] list = dir.list();
		if (list != null) {
			for (String file : list) {
				if (!file.startsWith(".")) {
					values.add(file);
				}
			}
		}
		Collections.sort(values);

		// Put the data into the list
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_list_item_2, android.R.id.text1, values);
		setListAdapter(adapter);

	}*/
	/*private List<String> path_vid;
	public void searchVid(File dir) {
	    String pattern = ".mp4";
	            //Get the listfile of that flder
	    final File listFile[] = dir.listFiles();

	    if (listFile != null) {
	        for (int i = 0; i < listFile.length; i++) {
	            final int x = i;
	            if (listFile[i].isDirectory()) {
	                walkdir(listFile[i]);
	            } else {
	                if (listFile[i].getName().endsWith(pattern)) {
	                    // Do what ever u want, add the path of the video to the list
	                       path_vid.add(listFile[i]);
	                }
	            }
	        }
	    }
	}*/
	/*private void searchvideo(){
		File sdCard = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		//For example:
		//File vidsFolder= new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Videos");
		searchVid(sdCard);
		if(path_vid.size()>0){
		   //Convert list into array
		   String[] array = path_vid.toArray(new String[path_vid.size()]);
		   //Create Adapter
		   ArrayAdapter<String> adapter =new ArrayAdapter<String>(this,android.R.layout.simple_list_item, array);
		   //Set adapter to videlist
		   videolist.setAdapter(adapter);
		}else{
		   //No vids found
		   exit();
		}
	}*/
	void onSearchDialogsClick() {
        File file = Environment.getExternalStorageDirectory();
        Log.d("Pankaj", "MainActivity.onSearchDialogsClick " + file.getAbsolutePath());
        File videoDir = new File(file.getAbsoluteFile()+"/Videos");
/*        if(videoDir.exists()) {
            String[] videoList = 
        }*/
    }
	/*@Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    String filename = (String) getListAdapter().getItem(position);
	    if (path.endsWith(File.separator)) {
	      filename = path + filename;
	    } else {
	      filename = path + File.separator + filename;
	    }
	    if (new File(filename).isDirectory()) {
	      Intent intent = new Intent(this, MainActivity.class);
	      intent.putExtra("path", filename);
	      startActivity(intent);
	    } else {
	      Toast.makeText(this, filename + " is not a directory", Toast.LENGTH_LONG).show();
	    }
	  }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Uri sourceUri = data.getData();
                Log.d("nikunj",""+sourceUri);
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.setData(sourceUri);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       /* // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;*/
    	
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
       // searchView.setIconifiedByDefault(true); // Do not iconify the widget; expand it by default

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.refreshlist) {
			fileList = new ArrayList<File> (fileListnew);
			myadapter.clear();
			myadapter.addAll(fileList);
		   adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }
    /*private class ImageLoadTask extends AsyncTask<File, String, Bitmap> {
    	 
        @Override
        protected void onPreExecute() {
            Log.i("ImageLoadTask", "Loading image...");
        }
 
        // PARAM[0] IS IMG URL
        protected Bitmap doInBackground(File... param) {
        	File file = param[0];
        	String uri = file.getAbsolutePath();
        	Bitmap thumb1 = null;
            Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0].getAbsolutePath());
            try {
				 thumb1 = ThumbnailUtils.createVideoThumbnail(
						file.getAbsolutePath(),
						MediaStore.Images.Thumbnails.MINI_KIND);

				Log.d("nikunj", "uri  for image path is" + uri);
				thumbnaillist.put(uri, thumb1);

			} catch (IllegalArgumentException e) {
				Log.d("nikunj", "something went wrong");
			}
			return thumb1;
        }
 
        protected void onProgressUpdate(String... progress) {
            // NO OP
        }
 
        protected void onPostExecute(Bitmap ret) {
            if (ret != null) {
                Log.i("ImageLoadTask", "Successfully loaded ");
                if (adapter != null) {
                    // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.e("ImageLoadTask", "Failed to load "  + " image");
            }
        }
    }
    *//*class MySimpleArrayAdapter extends ArrayAdapter<File> {
    	  private final Context context;
    	  private final ArrayList<File> values;

    	  public MySimpleArrayAdapter(Context context, ArrayList<File> values) {
    		String[] filenames = new String[values.length];
    		for(int i=0;i<values.length;i++){
    			filenames[i]= values[i].getName();
    		}
    	    super(context, R.layout.list_item_file, values);
    	    this.context = context;
    	    this.values = values;
    	  }

    	 
    	 

		@Override
    	  public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			 if (rowView == null) {
			      LayoutInflater inflater = getLayoutInflater();
			      rowView = inflater.inflate(R.layout.list_item_file, null);
			      // configure view holder
			      ViewHolder viewHolder = new ViewHolder();
			      viewHolder.text = (TextView) rowView.findViewById(R.id.label);
			      viewHolder.image = (ImageView) rowView
			          .findViewById(R.id.icon);
			      rowView.setTag(viewHolder);
			    }
			
			// fill data
			    ViewHolder holder = (ViewHolder) rowView.getTag();
			    holder.text.setText(values.get(position).getName());
			    setImage(holder.image,values.get(position));
			    
			   // int textcolor = color[randInt(0,9)];
			    int textcolor = color[position%10];
			    Log.d("nikunj","text color is"+textcolor);
			    holder.text.setBackgroundColor(textcolor);
			    holder.text.setTextColor(0xffffffff);
			    return rowView;
			
    	    LayoutInflater inflater = (LayoutInflater) context
    	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	    rowView = inflater.inflate(R.layout.list_item_file, parent, false);
    	    TextView textView = (TextView) rowView.findViewById(R.id.label);
    	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
    	    textView.setText(values.get(position).getName());
    	    // change the icon for Windows and iPhone
    	    setImage(imageView,values.get(position));

    	    return rowView;
    	  }
		
		private void setImage(ImageView imageView, final File file) {
			// imageView.setImageResource(R.drawable.thumbnail);
			Bitmap thumb = null;
			final String uri = file.getAbsolutePath();
			if (thumbnaillist.containsKey(uri)) {
				thumb = thumbnaillist.get(uri);
				if (null != thumb)
					imageView.setBackground(new BitmapDrawable(getResources(),
							thumb));
				return;
			} else {
				 new ImageLoadTask().execute(file);
				//imageView.setBackground(R.drawable.thumbnail);
				
				Thread t= new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							Bitmap thumb1 = ThumbnailUtils.createVideoThumbnail(
									file.getAbsolutePath(),
									MediaStore.Images.Thumbnails.MINI_KIND);

							Log.d("nikunj", "uri  for image path is" + uri);
							thumbnaillist.put(uri, thumb1);

						} catch (IllegalArgumentException e) {
							Log.d("nikunj", "something went wrong");
						}
						
					}
				
				});
				
				//t.run();
				
			}
			

		}
	}
  */
   /* public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }*/
    /*static class ViewHolder {
	    public TextView text;
	    public ImageView image;
	  }*/
    /*private void colorize(Bitmap photo) {
        Palette palette = Palette.generate(photo);
        applyPalette(palette);
    }*/

    
    
	/*class MyAdapter extends BaseAdapter {

		private ArrayList<File> mDataList;

		MyAdapter(ArrayList<File> datalist) {
			mDataList = datalist;
		}

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public Object getItem(int index) {
			return mDataList.get(index);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			TextView text;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (view == null) {
				ViewHolder holder = new ViewHolder();
				LayoutInflater inflater = getLayoutInflater();
				view = inflater.inflate(R.layout.list_item_file, null);
				holder.text = (TextView) view.findViewById(R.id.filename);
				view.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) view.getTag();
			File s = mDataList.get(position);
			Log.d("nikunj","filename"+s.getName());
			int timeStartIndex = s.indexOf("\n") + 1;
			String text = s.substring(timeStartIndex);
			int timeEndIndex = text.indexOf("\n");
			text = s.substring(timeEndIndex + timeStartIndex);
			String timeString = s.substring(timeStartIndex, timeStartIndex
					+ timeEndIndex);
			Log.d("Pankaj", "MyVideoView.mListItemClickListener  "
					+ timeStartIndex + " " + timeEndIndex + " " + timeString);
			String text = s.getName();
			if(null!=holder) holder.text.setText(text);
			return view;
		}

	}

*/
    

    public boolean updateprefp() {
    		fileListnew = new ArrayList<File>();
            /*root = new File(Environment.getExternalStorageDirectory()
      				.getAbsolutePath()+"/Movies");*/
    		root = new File(Environment.getExternalStorageDirectory()
      				.getAbsolutePath());
      		getfile(root);
        //save the task list to preference
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        try {
            editor.putString(TASKS, ObjectSerializer.serialize(fileListnew));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return editor.commit();
    }
	
	
}

/*public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this, VideoPlayerActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
    
}*/
