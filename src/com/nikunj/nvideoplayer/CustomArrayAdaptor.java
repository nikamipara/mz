package com.nikunj.nvideoplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.brightec.example.mediacontroller.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdaptor extends ArrayAdapter<File> {
	  private final Context context;
	  private final ArrayList<File> values;
	  //private static HashMap<String,Bitmap> thumbnaillist = FileManagerActivity.thumbnaillist;
	  private static int[] color=FileManagerActivity.color;
	  public CustomArrayAdaptor(Context context, ArrayList<File> values) {
	    super(context, R.layout.list_item_file, values);
	    this.context = context;
	    //this.values= makefilearray(values);
	    this.values=values;
	    
	  }
	private ArrayList<File> makefilearray(ArrayList<File> values2) {
		ArrayList<File> result = new ArrayList<File>();
		for(File f:values2){
			if(f.exists()){
				result.add(f);
			}
		}
		return result;
	}
	@Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		 if (rowView == null) {
			 LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		      rowView = inflater.inflate(R.layout.list_item_file, null);
		      // configure view holder
		      ViewHolder viewHolder = new ViewHolder();
		      viewHolder.text = (TextView) rowView.findViewById(R.id.label);
		      viewHolder.image = (ImageView) rowView
		          .findViewById(R.id.icon);
		      rowView.setTag(viewHolder);
		    }
		 int textcolor = color[position%10];
		// fill data
		    ViewHolder holder = (ViewHolder) rowView.getTag();
		    holder.image.setBackgroundColor(textcolor+10);
		    holder.text.setText(values.get(position).getName());
		   // int textcolor = color[randInt(0,9)];
		    Log.d("nikunj","text color is"+textcolor);
		    holder.text.setBackgroundColor(textcolor);
		    holder.text.setTextColor(0xffffffff);
		    setImage(holder.image,values.get(position));
		    return rowView;
		
	    /*LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    rowView = inflater.inflate(R.layout.list_item_file, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    textView.setText(values.get(position).getName());
	    // change the icon for Windows and iPhone
	    setImage(imageView,values.get(position));

	    return rowView;
*/    	  }
	static class imageloader{
		public ImageView imageView;
		public File file;
	}
	private void setImage(ImageView imageView, final File file) {
		// imageView.setImageResource(R.drawable.thumbnail);
		Bitmap thumb = null;
		final String uri = file.getAbsolutePath();
		/*if (thumbnaillist.containsKey(uri)) {
			thumb = thumbnaillist.get(uri);
			if (null != thumb)
				imageView.setBackground(new BitmapDrawable(context.getResources(),
						thumb));
			return;
		} else {*/
		imageloader i = new imageloader();
		i.imageView = imageView;
		i.file = file;
			 new ImageLoadTask().execute(i);
			//imageView.setBackground(R.drawable.thumbnail);
			
			/*Thread t= new Thread(new Runnable() {
				
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
			
			});*/
			
			//t.run();
			
		//}
		

	}
	
	 static class ViewHolder {
		    public TextView text;
		    public ImageView image;
		  }
	 private class ImageLoadTask extends AsyncTask<imageloader, String, Drawable> {
    	 	imageloader i;
	        @Override
	        protected void onPreExecute() {
	            Log.i("ImageLoadTask", "Loading image...");
	        }

			// PARAM[0] IS IMG URL
	        protected Drawable doInBackground(imageloader... param) {
	        	i = param[0];
	        	File file = param[0].file;
	        	String uri = file.getAbsolutePath();
	        	Bitmap thumb1 = null;
	            Log.i("ImageLoadTask", "Attempting to load image URL: " + param[0].file.getAbsolutePath());
	            try {
					 thumb1 = ThumbnailUtils.createVideoThumbnail(
							file.getAbsolutePath(),
							MediaStore.Images.Thumbnails.MINI_KIND);

					Log.d("nikunj", "uri  for image path is" + uri);
					//thumbnaillist.put(uri, thumb1);

				} catch (IllegalArgumentException e) {
					Log.d("nikunj", "something went wrong");
				}
				return new BitmapDrawable(context.getResources(),thumb1);
	        }
	 
	        protected void onProgressUpdate(String... progress) {
	            // NO OP
	        }
	 
	        protected void onPostExecute(Drawable ret) {
	            if (ret != null) {
	                Log.i("ImageLoadTask", "Successfully loaded ");
	                if (this != null) {
	                    // WHEN IMAGE IS LOADED NOTIFY THE ADAPTER
	                	i.imageView.setBackground(ret);/*ackgroundDrawable(ret);*/
	                	//notifyDataSetChanged();
	                }
	            } else {
	                Log.e("ImageLoadTask", "Failed to load "  + " image");
	            }
	        }
	    }
	   
}

