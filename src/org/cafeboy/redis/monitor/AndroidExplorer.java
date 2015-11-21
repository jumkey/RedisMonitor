package org.cafeboy.redis.monitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidExplorer extends ListActivity {
	public final static String RESULT_PATH = "RESULT_PATH";

	private List<String> item = null;
	private List<String> path = null;
	private String root = Environment.getExternalStorageDirectory().getPath();
	private String parentPath;
	private String currentPath = root;
	private TextView myPath;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.files);
		myPath = (TextView) findViewById(R.id.path);
		getDir(root);
	}

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);
		currentPath = dirPath;

		item = new ArrayList<String>();
		path = new ArrayList<String>();

		File f = new File(dirPath);
		File[] files = f.listFiles(new ZipFileFilter());

		if (!dirPath.equals(root)) {
			item.add(root);
			path.add(root);

			parentPath = f.getParent();
			item.add("../");
			path.add(f.getParent());

		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			path.add(file.getPath());
			if (file.isDirectory())
				item.add(file.getName() + "/");
			else
				item.add(file.getName());
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this, R.layout.files_row, item);
		setListAdapter(fileList);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (!currentPath.equals(root)) {
				getDir(parentPath);
			} else {
				return super.onKeyDown(keyCode, event);
			}

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		File file = new File(path.get(position));

		if (file.isDirectory()) {
			if (file.canRead()) {
				getDir(path.get(position));
			} else {
				new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("[" + file.getName() + "] folder can't be read!").setPositiveButton("OK", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
					}
				}).show();
			}
		} else {
			getIntent().putExtra(RESULT_PATH, file.getPath());
			setResult(RESULT_OK, getIntent());
			finish();
		}
	}

}