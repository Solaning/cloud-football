package com.kinth.football.picture;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.FastDateFormat;

import com.kinth.football.config.JConstants;
import com.kinth.football.util.FileUtil;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

public class PickPicture {
	
	/**
	 * 选择图片的intent
	 * @param mContext
	 * @return
	 */
	public static Intent genPickPictureIntent(Context mContext){
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){        
			Intent intent=new Intent(Intent.ACTION_GET_CONTENT);//ACTION_OPEN_DOCUMENT  
			intent.addCategory(Intent.CATEGORY_OPENABLE);  
			intent.setType("image/*");  
			return intent;
		}else{                
			//使用旧的
			List<Intent> targets = new ArrayList<Intent>();
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					"image/*");
			List<ResolveInfo> candidates = mContext.getPackageManager()
					.queryIntentActivities(intent, 0);

			for (ResolveInfo candidate : candidates) {
				String packageName = candidate.activityInfo.packageName;
				if (!packageName.equals("com.google.android.apps.photos")
						&& !packageName.equals("com.google.android.apps.plus")
						&& !packageName.equals("com.android.documentsui")) {
					Intent iWantThis = new Intent();
					iWantThis.setType("image/*");
					iWantThis.setAction(Intent.ACTION_GET_CONTENT);
					iWantThis.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
					iWantThis.setDataAndType(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							"image/*");
					iWantThis.setPackage(packageName);
					targets.add(iWantThis);
				}
			}
			Intent chooser = Intent.createChooser(targets.remove(0),
					"Select Picture");
			chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,
					targets.toArray(new Parcelable[targets.size()]));
	        return chooser;
		}  
	}
	
	public static String selectImage(Context context, Uri selectedImage){
//      Log.e(TAG, selectedImage.toString());
        if(selectedImage!=null){
            String uriStr=selectedImage.toString();  
            String path=uriStr.substring(10,uriStr.length());  
            if(path.startsWith("com.sec.android.gallery3d")){
                Log.e("TAG", "It's auto backup pic path:"+selectedImage.toString());  
                return null;  
            }  
        }
        
        return getDataColumn(context, selectedImage, null, null);      
    }
	
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {
	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;  
	  
	    // DocumentProvider  
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider  
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            if ("primary".equalsIgnoreCase(type)) { 
	                return Environment.getExternalStorageDirectory() + "/" + split[1];  
	            }  
	  
	            // TODO handle non-primary volumes  
	        }  
	        // DownloadsProvider  
	        else if (isDownloadsDocument(uri)) {  
	  
	            final String id = DocumentsContract.getDocumentId(uri);  
	            final Uri contentUri = ContentUris.withAppendedId(  
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));  
	  
	            return getDataColumn(context, contentUri, null, null);  
	        }  
	        // MediaProvider  
	        else if (isMediaDocument(uri)) {  
	            final String docId = DocumentsContract.getDocumentId(uri);  
	            final String[] split = docId.split(":");  
	            final String type = split[0];  
	  
	            Uri contentUri = null;  
	            if ("image".equals(type)) {  
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("video".equals(type)) {  
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;  
	            } else if ("audio".equals(type)) {  
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;  
	            }  
	  
	            final String selection = "_id=?";  
	            final String[] selectionArgs = new String[] {  
	                    split[1]  
	            };  
	  
	            return getDataColumn(context, contentUri, selection, selectionArgs);  
	        }  
	    }  else if(isKitKat && !DocumentsContract.isDocumentUri(context, uri)){
	    	return selectImage(context, uri);
	    }
	    // MediaStore (and general)  
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {  
	        // Return the remote address  
	        if (isGooglePhotosUri(uri))  
	            return uri.getLastPathSegment();  
	  
	        return getDataColumn(context, uri, null, null);  
	    }  
	    // File  
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {  
	        return uri.getPath();  
	    }  
	  
	    return null;  
	}  
	  
	/** 
	 * Get the value of the data column for this Uri. This is useful for 
	 * MediaStore Uris, and other file-based ContentProviders. 
	 * 
	 * @param context The context. 
	 * @param uri The Uri to query. 
	 * @param selection (Optional) Filter used in the query. 
	 * @param selectionArgs (Optional) Selection arguments used in the query. 
	 * @return The value of the _data column, which is typically a file path. 
	 */  
	public static String getDataColumn(Context context, Uri uri, String selection,  
	        String[] selectionArgs) {  
	  
	    Cursor cursor = null;  
	    final String column = MediaStore.Images.Media.DATA ;  
	    final String[] projection = {  
	            column  
	    };  
	  
	    try {  
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,  
	                null);  
	        if (cursor != null && cursor.moveToFirst()) {  
	            final int index = cursor.getColumnIndexOrThrow(column);  
	            return cursor.getString(index);  
	        }  
	    }catch(IllegalArgumentException e){
	    	e.printStackTrace();
	    }finally {  
	        if (cursor != null)  
	            cursor.close();  
	    }  
	    return null;  
	}  
	  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is ExternalStorageProvider. 
	 */  
	public static boolean isExternalStorageDocument(Uri uri) {  
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is DownloadsProvider. 
	 */  
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is MediaProvider. 
	 */  
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());  
	}  
	  
	/** 
	 * @param uri The Uri to check. 
	 * @return Whether the Uri authority is Google Photos. 
	 */  
	public static boolean isGooglePhotosUri(Uri uri) {
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());  
	} 
	
	//--------------------------调用摄像头拍照-------------
	public static Intent genCameraShootIntent(String mPhotoPath){
		File mPhotoFile = new File(mPhotoPath);
		if (!mPhotoFile.exists()) {
			FileUtil.createFile(mPhotoFile, true);
		}
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(mPhotoFile));
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}
	
	/**
	 * 生成缓存目录下的一个图片路径
	 * @return
	 */
	public static String genCachePhotoFileName(){
		String fileName = FastDateFormat.getInstance(
				"'IMG'_yyyyMMdd_HHmmss").format(new Date());
		return JConstants.IMAGE_CACHE + File.separator + fileName;// 文件路径
	}
	
}
