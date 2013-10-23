package org.cafeboy.redis.monitor;

import java.io.File;
import java.io.UTFDataFormatException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cafeboy.redis.monitor.adapter.Contacts;
import org.cafeboy.redis.monitor.adapter.ContactsListAdapter;
import org.cafeboy.redis.monitor.utils.Constants;
import org.cafeboy.redis.monitor.utils.ContactsUtils;
import org.cafeboy.redis.monitor.utils.ZipUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class MyContacts extends Activity {
	private static final String TAG = MyContacts.class.getSimpleName();
	public static Map<Long, Bitmap> imagesCache = new HashMap<Long, Bitmap>();
	private ImageView img = null;
	private ListView listView = null;
	private List<Contacts> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		img = (ImageView) findViewById(R.id.iii);
		listView = (ListView) findViewById(R.id.contactslist);

		list = ContactsUtils.getContactList(this);
		listView.setAdapter(new ContactsListAdapter(this, list));
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				Contacts o = (Contacts) a.getItemAtPosition(position);
				Intent intent = new Intent(MyContacts.this, QRCodeCreatorActivity.class);
				intent.putExtra(Constants.uName, o.getName());
				startActivity(intent);
			}

		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> adapterview, View view, int i, long l) {
				// TODO Auto-generated method stub
				return false;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent intent = new Intent(this, AndroidExplorer.class);
			startActivityForResult(intent, 1);
			break;
		}
		return false;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {

			if (requestCode == 1) {
				String filePath = data.getStringExtra(AndroidExplorer.RESULT_PATH);

				new ChangeImg().execute(filePath);
			}

		} else if (resultCode == Activity.RESULT_CANCELED) {
			Log.e(TAG, "file not selected");
		}
	}

	private class ChangeImg extends AsyncTask<String, Bitmap, Integer> {

		@Override
		protected Integer doInBackground(String... paths) {
			try {
				// 获取联系人
				List<Long> list = ContactsUtils.getRawContactsId(MyContacts.this);
				Long[] ids = new Long[list.size()];
				list.toArray(ids);

				try {
					List<File> l = ZipUtils.GetFileList(paths[0], false, true);
					for (int i = 0; i < ids.length; i++) {
						Long s = ids[i];
						Log.e(TAG, String.valueOf(s));
						File f = l.get(i > l.size() ? i - l.size() : i);
						byte[] bytes = ZipUtils.UpZip(paths[0], f.getPath());
						if (null != bytes) {
							Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
							ContactsUtils.setPersonPhotoBytes(MyContacts.this, bytes, s, true);
							publishProgress(bm);
						}
					}
				} catch (UTFDataFormatException e) {
					Log.e(TAG, e.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		protected void onProgressUpdate(Bitmap... progress) {
			img.setImageBitmap(progress[0]);
		}

		@Override
		protected void onPostExecute(Integer result) {
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onCancelled() {
		}
	}
}