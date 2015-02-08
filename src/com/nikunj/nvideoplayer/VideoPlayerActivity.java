package com.nikunj.nvideoplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import uk.co.brightec.example.mediacontroller.R;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;


public class VideoPlayerActivity extends Activity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, VideoControllerView.MediaPlayerControl {
	public static final String EXTRA_VIDEO = "VideoPlayerActivity:VIDEO";
    SurfaceView videoSurface;
    MediaPlayer player;
    VideoControllerView controller;
    private static AudioManager am;
    private String srtFileName;
	private String mVideoSource;
	private Uri sourceUri;
	private String keyword;
	private boolean isSetSearch;
	private boolean mIsPlaying;
	private boolean isSearchQuerySet;
	private String mSearchQuery;
	private ArrayList<String> mArrayList = new ArrayList<String>();
    ListView mListView;
    SearchView mSearchView;
    AlphaInAnimationAdapter adapter;
	private static File srtFile;
	private int myInt;
	private SurfaceHolder mHolder;
    
	private void initializefields() {
		am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
		if (player == null)
			player = new MediaPlayer();
		if (controller == null)
			controller = new VideoControllerView(this);
		if (sourceUri == null)
			sourceUri = getIntent().getData();
		keyword = getIntent().getStringExtra("keyword");
		isSetSearch = false;
		mIsPlaying = true;
		mListView = (ListView) findViewById(R.id.list);
		mSearchView = (SearchView) findViewById(R.id.searchview);
		MyAdapter myadapter = new MyAdapter(mArrayList);
		adapter = new AlphaInAnimationAdapter(myadapter);
		mVideoSource = getPath(sourceUri);
		srtFileName = (mVideoSource.substring(0, mVideoSource.lastIndexOf(".")))
				.concat(".srt");
		srtFile = new File(srtFileName);
	}
	
	private boolean requestAudioFocus(){
		
		// Request audio focus for playback
		int result = am.requestAudioFocus(afChangeListener,
		                                 // Use the music stream.
		                                 AudioManager.STREAM_MUSIC,
		                                 // Request permanent focus.
		                                 AudioManager.AUDIOFOCUS_GAIN);
	 
		 return (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED)?(true):(false);
	}
	private void abandonAudioFocus(){
		am.abandonAudioFocus(afChangeListener);
	}
	OnAudioFocusChangeListener afChangeListener = new OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				// Pause playback
				if (controller != null)
					controller.pause();
			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				if (controller != null)
					controller.play();
			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				am.abandonAudioFocus(afChangeListener);
				if (controller != null)
					controller.pause();
			}
		}

	};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//to keep screen on.
        //set to full screen 
        setfullscreen();
        setContentView(R.layout.activity_video_player);
       // getActionBar().hide();        
        /*videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
        player = new MediaPlayer();
        controller = new VideoControllerView(this);
        //ViewCompat.setTransitionName(videoSurface, EXTRA_VIDEO);
        sourceUri = getIntent().getData();
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, sourceUri);
            player.setOnPreparedListener(this);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mVideoSource = getPath(sourceUri);
        Log.d("NIKUNJ", "MyVideoView.onCreate " + mVideoSource);
        setscrneensize();
        srtFileName = (mVideoSource.substring(0, mVideoSource.lastIndexOf("."))).concat(".srt");*/
        initializefields();
    }

	private void setfullscreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	@Override
    protected void onStart() {
    	Log.d("methods","onStart()");
    	super.onStart();
    	initViews();
    	
    }
    @Override
    protected void onResume() {
    	Log.d("methods","onResume()");
    	setSearch();
    	initcontroller();
    	initializelistners();
    	super.onResume();
    }
    @Override
    public void onBackPressed() {
    	Log.d("methods","onBackpressed()");
        if (!mSearchView.isIconified()) {
            mSearchView.setIconified(true);
        } else {
            super.onBackPressed();
        }
    }
    private void setSearch() {
		Log.d("methods", "setsearch()");
		// TODO Auto-generated method stub
		if (isSearchQuerySet) {
			mListView.setVisibility(View.VISIBLE);
			mSearchView.setIconified(false);
			mSearchView.clearFocus();
			return;
		}
		/* keyword = getIntent().getStringExtra("keyword"); */
		Log.d("nikunj", "keyword is " + keyword);
		// keyword = "hi";
		if (keyword == null || keyword.isEmpty())
			return;
		// mSearchView.performClick();
		mSearchView.setIconified(false);
		mSearchView.setQuery(keyword, false);
		searchQuery(keyword);
		mSearchView.clearFocus();
		keyword=null;

	}
	private void searchQuery(String query) {
		Log.d("methods","searchQuery()");
		mListView.setVisibility(View.VISIBLE);
        mArrayList.clear();
        SearchUtil.searchText(srtFile, query, mArrayList);
        adapter.notifyDataSetChanged();
	}
	@Override
    protected void onPause() {
		
		Log.d("methods","onPause()");
		mIsPlaying  = controller.isPlaying();
    	player.stop();
    	myInt = player.getCurrentPosition();
    	mSearchQuery = (mSearchView.getQuery()).toString();
    	isSearchQuerySet = (mSearchQuery.isEmpty()?(false):(true));
    	abandonAudioFocus();
    	super.onPause();
    }
    
    private void setscrneensize() {
    	Log.d("methods","setscrneensize()");
    	final MediaMetadataRetriever retriever = new  MediaMetadataRetriever();
    	Bitmap bmp = null;      
    	int videoWidth = 0;
        int videoHeight = 0;
    	try 
    	{	Log.d("nikunj","source uri path is"+mVideoSource);
    	    retriever.setDataSource(mVideoSource);
    	    bmp = retriever.getFrameAtTime();
    	    videoHeight=bmp.getHeight();
    	    videoWidth=bmp.getWidth();
    	}catch(IllegalArgumentException e){
    		Log.d("nikunj","something went wrong");
    	}
    	/*//Get the dimensions of the video
        int videoWidth = player.getVideoWidth();
        int videoHeight = player.getVideoHeight();
        Log.d("nikunj","video width"+videoWidth+"   height"+videoHeight);*/
        //Get the width of the screen
		if (0 != videoHeight && 0 != videoWidth) {
			DisplayMetrics metrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metrics);
			int screenWidth = metrics.widthPixels;
			Log.d("nikunj", "screen width" + screenWidth);
			// Get the SurfaceView layout parameters
			android.view.ViewGroup.LayoutParams lp = videoSurface
					.getLayoutParams();

			// Set the width of the SurfaceView to the width of the screen
			lp.width = screenWidth;

			// Set the height of the SurfaceView to match the aspect ratio of
			// the video
			// be sure to cast these as floats otherwise the calculation will
			// likely be 0
			lp.height = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);
			Log.d("nikunj", "layout height" + lp.height);
			// Commit the layout parameters
			videoSurface.setLayoutParams(lp);
		}
		
	}

	
	private void initializelistners(){
		 /* Do no initilaize view when srt not present */
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        	
            @Override
            public boolean onQueryTextSubmit(String query) {
            	Log.d("nikunj","onquery submit called");
            	searchQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub
            	if(newText.isEmpty()){
            		mListView.setVisibility(View.GONE);
            		mArrayList.clear();
                    adapter.notifyDataSetChanged();
            	}
                return false;
            }
        });
        mSearchView.setOnSearchClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("NIKUNJ", "onclick" + mVideoSource);
				if(controller!=null&&!isSetSearch)controller.pause();
				isSetSearch=(isSetSearch?(false):(isSetSearch));
			}
		});
        mSearchView.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public boolean onClose() {
				mArrayList.clear();
                adapter.notifyDataSetChanged();
                mListView.setVisibility(View.GONE);
                controller.play();
				return false;
			}
		});
        mListView.setOnItemClickListener(mListItemClickListener);
        
	}
    private void initViews() {
    	Log.d("methods","initViews()");
       // mListView = (ListView) findViewById(R.id.list);
        mListView.setVisibility(View.GONE);
        /*MyAdapter myadapter = new MyAdapter(mArrayList);
        
        adapter = new AlphaInAnimationAdapter(myadapter);*/
        adapter.setAbsListView(mListView);
        mListView.setAdapter(adapter);
        
       // mListView.setAdapter(adapter);

        //mSearchView = (SearchView) findViewById(R.id.searchview);
        mSearchView.setSubmitButtonEnabled(true); // to display submit button

        /*srtFile = new File(srtFileName);*/
        if (!srtFile.exists()) {
            /*Toast.makeText(this, srtFile.getName() + " does not exist", Toast.LENGTH_LONG).show();*/
            //mSearchView.setClickable(false);
        	Toast.makeText(this,"Search not enabled for This video", Toast.LENGTH_LONG).show();
            mSearchView.setVisibility(View.INVISIBLE);
            return;
        }

       
        /*int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) mSearchView.findViewById(id);
        textView.setTextColor(Color.WHITE);*/
    }
    private AdapterView.OnItemClickListener mListItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
            String s = mArrayList.get(position);

            int timeStartIndex = s.indexOf("\n") + 1;
            String text = s.substring(timeStartIndex);
            int timeEndIndex = text.indexOf("\n");
            text = s.substring(timeEndIndex + timeStartIndex);
            String timeString = s.substring(timeStartIndex, timeStartIndex + timeEndIndex);
            String time = timeString.substring(0, 8);
            String[] times = time.split(":");

            Toast.makeText(VideoPlayerActivity.this, time, Toast.LENGTH_LONG).show();
            int seekTime = Integer.parseInt(times[0]) * 3600 + Integer.parseInt(times[1]) * 60
                    + Integer.parseInt(times[2]);
            controller.show();
            player.seekTo(seekTime * 1000 - 50);
            controller.play();
        }
    };
	
    
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	Log.d("methods","onTouchEvent()");
        controller.show();
        return false;
    }

    // Implement SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    	Log.d("methods","surfaceChanged()");
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	Log.d("methods","onConfigurationChanged()");
    	// TODO Auto-generated method stub
    	super.onConfigurationChanged(newConfig);
    	setscrneensize();
    	
    }
    
    private void initcontroller() {
    	Log.d("methods","initcontroller()");
    	if(mHolder!=null){
        	player.setDisplay(mHolder);
        	try {
				player.prepareAsync();
			} catch (IllegalStateException e) {
				Log.d("fuck",":(");
				e.printStackTrace();
			}
            
            return;
        }
    	//videoSurface = (SurfaceView) findViewById(R.id.videoSurface);
        SurfaceHolder videoHolder = videoSurface.getHolder();
        videoHolder.addCallback(this);
       /* if(player==null)player = new MediaPlayer();*/
        
        if(controller==null)controller = new VideoControllerView(this);
        //ViewCompat.setTransitionName(videoSurface, EXTRA_VIDEO);
        if(sourceUri==null)sourceUri = getIntent().getData();
        
        try {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(this, sourceUri);
            player.setOnPreparedListener(this);
            player.setScreenOnWhilePlaying(true); // keeps screen on while video is playing.
           // player.setVideoScalingMode(MediaVIDEO_SCALING_MODE_SCALE_TO_FIT);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        /*mVideoSource = getPath(sourceUri);*/
        Log.d("NIKUNJ", "MyVideoView.onCreate " + mVideoSource);
        setscrneensize();
        /*srtFileName = (mVideoSource.substring(0, mVideoSource.lastIndexOf("."))).concat(".srt");
		*/
	}

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
		Log.d("methods","surfaceCreated()");
		mHolder = holder;
    	player.setDisplay(holder);
        player.prepareAsync();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	Log.d("methods","surfaceDestroyed()");
    	mHolder =null;
    	//player =null;
    }
    // End SurfaceHolder.Callback

    // Implement MediaPlayer.OnPreparedListener
    @Override
    public void onPrepared(MediaPlayer mp) {
    	Log.d("methods","MediaPlayer()");
        controller.setMediaPlayer(this);
        controller.setAnchorView((LinearLayout) findViewById(R.id.videooverlaySurfaceContainer));
        //ViewCompat.setTransitionName(videoSurface, EXTRA_VIDEO);
        System.out.println("value if my int"+myInt);
       if(0!=myInt) player.seekTo(myInt-50);
       
       if(requestAudioFocus()){
    	   player.start();
       }else{
    	   System.out.println("focus can not be obtained");
       }
       
        if(!mIsPlaying){
        	controller.pause();
        	controller.show();
        }
        
    }
    // End MediaPlayer.OnPreparedListener

    // Implement VideoMediaController.MediaPlayerControl
    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return player.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return player.getDuration();
    }

    @Override
    public boolean isPlaying() {
        return player.isPlaying();
    }

    @Override
    public void pause() {
        player.pause();
    }

    @Override
    public void seekTo(int i) {
        player.seekTo(i);
    }

    @Override
    public void start() {
        player.start();
    }

    @Override
    public boolean isFullScreen() {
        return false;
    }

    @Override
    public void toggleFullScreen() {
        
    }
    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }
    public Animation PlayAnim( int viewid, Context Con, int animationid, int StartOffset )
    {
        View v = findViewById(viewid);

        if( v != null )
        {
            Animation animation = AnimationUtils.loadAnimation(Con, animationid  );
            animation.setStartOffset(StartOffset);
            v.startAnimation(animation);

            return animation;
        }
        return null;
    }
    // End VideoMediaController.MediaPlayerControl
    class MyAdapter extends BaseAdapter {

        private ArrayList<String> mDataList;

        MyAdapter(ArrayList<String> datalist) {
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
                view = inflater.inflate(R.layout.list_item_sub, null);
                holder.text = (TextView) view.findViewById(R.id.item_text);
                view.setTag(holder);
            }
            ViewHolder holder = (ViewHolder )view.getTag();
            String s = mDataList.get(position);
            int timeStartIndex = s.indexOf("\n") + 1;
            String text = s.substring(timeStartIndex);
            int timeEndIndex = text.indexOf("\n");
            text = s.substring(timeEndIndex + timeStartIndex);
            String timeString = s.substring(timeStartIndex, timeStartIndex + timeEndIndex);
            Log.d("Nikunj", "MyVideoView.mListItemClickListener  " + timeStartIndex + " "
                    + timeEndIndex + " " + timeString);

            holder.text.setText(text);
            return view;
        }

    }

}
