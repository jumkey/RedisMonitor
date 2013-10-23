package org.cafeboy.redis.monitor.utils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.cafeboy.redis.monitor.adapter.Contacts;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

public class ContactsUtils {

	/**
	 * 获取没有头像的联系人RawContacts
	 */
	public static List<Long> getRawContactsId(Context context) {
		List<Long> list = new ArrayList<Long>();
		ContentResolver c = context.getContentResolver();
		Cursor cursor = c.query(ContactsContract.RawContactsEntity.CONTENT_URI, null, ContactsContract.CommonDataKinds.Photo.PHOTO + " IS NULL AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null, null);

		// 循环遍历
		try {
			while (cursor.moveToNext()) {
				// 获得联系人的ID
				Long contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
				list.add(contactId);
			}
		} finally {
			cursor.close();
		}

		return list;
	}

	/**
	 * 设置头像
	 */
	public static boolean setPersonPhotoBytes(Context context, byte[] bytes, long personId, boolean Sync) {
		ContentResolver c = context.getContentResolver();
		boolean ret = false;
		ContentValues values = new ContentValues();
		int photoRow = -1;
		String where = ContactsContract.Data.RAW_CONTACT_ID + " = " + personId + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
		Cursor cursor = c.query(ContactsContract.Data.CONTENT_URI, null, where, null, null);
		int idIdx = cursor.getColumnIndexOrThrow(ContactsContract.Data._ID);
		if (cursor.moveToFirst()) {
			photoRow = cursor.getInt(idIdx);
		}
		cursor.close();

		values.put(ContactsContract.Data.RAW_CONTACT_ID, personId);
		values.put(ContactsContract.Data.IS_PRIMARY, 1);
		values.put(ContactsContract.Data.IS_SUPER_PRIMARY, 1);
		values.put(ContactsContract.CommonDataKinds.Photo.PHOTO, bytes);
		values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);

		if (photoRow >= 0) {
			final int rowsUpdated = c.update(ContactsContract.Data.CONTENT_URI, values, ContactsContract.Data._ID + " = " + photoRow, null);
			ret = rowsUpdated >= 1;
		} else {
			final Uri uri = c.insert(ContactsContract.Data.CONTENT_URI, values);
			ret = uri != null && !TextUtils.isEmpty(uri.toString());
		}
		if (!Sync) {
			Uri u = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, personId);
			// Uri.withAppendedPath(ContactsContract.RawContacts.CONTENT_URI, String.valueOf(personId));
			values = new ContentValues();
			values.put(ContactsContract.RawContacts.DIRTY, 0);
			c.update(u, values, null, null);
		}
		return ret;
	}

	/**
	 * 获取头像
	 */
	public static Bitmap getContactPhoto(Context context, long personId, int defaultIco) {
		ContentResolver c = context.getContentResolver();
		byte[] data = new byte[0];
		// AND (ContactsContract.Data.IS_SUPER_PRIMARY = 1 OR
		// ContactsContract.Data.IS_PRIMARY = 1)
		String where = ContactsContract.Data.RAW_CONTACT_ID + " = " + personId + " AND " + ContactsContract.Data.MIMETYPE + " = '" + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'";
		Cursor cursor = c.query(ContactsContract.Data.CONTENT_URI, null, where, null, null);
		if (cursor.moveToFirst()) {
			data = cursor.getBlob(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));
		}
		cursor.close();
		if (data == null || data.length == 0) {
			return BitmapFactory.decodeResource(context.getResources(), defaultIco);
		} else
			return BitmapFactory.decodeByteArray(data, 0, data.length);
	}

	public static List<Contacts> getContactList(Context context) {
		ContentResolver c = context.getContentResolver();
		List<Contacts> list = new ArrayList<Contacts>();
		// 获得所有的联系人
		Cursor cur = c.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
		// 循环遍历
		while (cur.moveToNext()) {

			Contacts contacts = new Contacts();
			// 获得联系人的ID号
			long contactId = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
			// 获得联系人姓名
			String disPlayName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			// 获得联系人姓名
			int photoId = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));

			// 查看该联系人有多少个电话号码。如果没有这返回值为0
			int inVisibleGroup = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.IN_VISIBLE_GROUP));
			contacts.setName(disPlayName);
			contacts.setVisible(inVisibleGroup);
			contacts.setContactId(contactId);
			contacts.setPhotoId(photoId);
			list.add(contacts);
		}
		return list;
	}

	public static Bitmap getPhoto(Context context, long contactId) {
		ContentResolver contentResolver = context.getContentResolver();
		Uri contactPhotoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);

		InputStream photoDataStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, contactPhotoUri);
		Bitmap photo = null;
		if (photoDataStream != null) {
			photo = BitmapFactory.decodeStream(photoDataStream);
		}
		return photo;
	}
}
