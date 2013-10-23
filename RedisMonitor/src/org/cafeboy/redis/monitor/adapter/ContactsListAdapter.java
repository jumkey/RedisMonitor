package org.cafeboy.redis.monitor.adapter;

import java.util.List;

import org.cafeboy.redis.monitor.MyContacts;
import org.cafeboy.redis.monitor.R;
import org.cafeboy.redis.monitor.utils.ContactsUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsListAdapter extends BaseAdapter {

	private List<Contacts> listData;

	private LayoutInflater layoutInflater;

	public ContactsListAdapter(Context context, List<Contacts> listData) {
		this.listData = listData;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return listData.size();
	}

	@Override
	public Object getItem(int position) {
		return listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Contacts contacts = listData.get(position);
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.contacts_row, null);
			holder = new ViewHolder();
			holder.titleView = (TextView) convertView.findViewById(R.id.title);
			holder.infosView = (TextView) convertView.findViewById(R.id.infos);
			holder.imgView = (ImageView) convertView.findViewById(R.id.img);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!MyContacts.imagesCache.containsKey(contacts.getContactId())) {
			MyContacts.imagesCache.put(contacts.getContactId(), BitmapFactory.decodeResource(convertView.getResources(), R.drawable.ic_contact_picture_holo_light));
			if (contacts.getPhotoId() > 0) {
				// 异步加载图片
				LoadImageTask task = new LoadImageTask(convertView);
				task.execute(contacts.getContactId());
			}
		}
		holder.titleView.setText(contacts.getName());
		holder.infosView.setText(contacts.getVisible() == 0 ? "不可见" : "可见");
		holder.imgView.setImageBitmap(MyContacts.imagesCache.get(contacts.getContactId()));

		return convertView;
	}

	static class ViewHolder {
		TextView titleView;
		TextView infosView;
		ImageView imgView;
	}

	// 加载图片的异步任务
	class LoadImageTask extends AsyncTask<Long, Void, Bitmap> {
		private View resultView;

		LoadImageTask(View resultView) {
			this.resultView = resultView;
		}

		@Override
		protected Bitmap doInBackground(Long... params) {
			Bitmap image = null;
			try {
				image = ContactsUtils.getPhoto(resultView.getContext(), params[0]);
				MyContacts.imagesCache.put(params[0], image); // 把下载好的图片保存到缓存中
			} catch (Exception e) {
				e.printStackTrace();
			}

			return image;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			ContactsListAdapter.this.notifyDataSetChanged();
			// 执行完毕，更新UI
		}
	}
}
