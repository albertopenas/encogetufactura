/*
 * This file is part of myPlan.
 *
 * Plan is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * Plan is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with myPlan.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.conzebit.myplan.android.store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;

import com.conzebit.myplan.android.store.MyPlanDBHelper.CountryInterval;
import com.conzebit.myplan.core.Chargeable;
import com.conzebit.myplan.core.contact.Contact;
import com.conzebit.myplan.core.msisdn.MsisdnTypeService;
import com.conzebit.myplan.core.sms.Sms;

import es.simyo.encogetufactura.EncogeTuFacturaApp;

@SuppressWarnings("deprecation")
class SMSLogStore {

	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;
	
	private static HashMap<String, Contact> contactCache = new HashMap<String, Contact>();

	public static ArrayList<Chargeable> getSMS(Context context, Date startBillingDate, Date endBillingDate) {
		ArrayList<Chargeable> smses = new ArrayList<Chargeable>();

		String selection = null;
		String[] selectionArgs = null;
		if (startBillingDate != null && endBillingDate != null) {
			selection = "date > ? AND date < ?";
	        selectionArgs = new String[] {
	                String.valueOf(startBillingDate.getTime()),
	                String.valueOf(endBillingDate.getTime())
	        };
		} else {
			return smses; // TODO remove to allow sms statistics
		}
        String sortOrder = "date";
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms"), null, selection, selectionArgs, sortOrder);
        
		int typeColumn = cursor.getColumnIndex("type");
		int dateColumn = cursor.getColumnIndex("date");
		int addressColumn = cursor.getColumnIndex("address");
		MsisdnTypeService msisdnTypeService = MsisdnTypeService.getInstance();
		try {
			if (cursor.moveToFirst()) {
				CountryStore countryStore = CountryStore.getInstance(context);
				CountryInterval countryInterval = null;
				do {
					int type = cursor.getInt(typeColumn);
					if (type == MESSAGE_TYPE_INBOX) {
						type = Sms.SMS_TYPE_RECEIVED;
				    } else if (type == MESSAGE_TYPE_SENT) {
				    	type = Sms.SMS_TYPE_SENT;
				    } else {
				    	continue;
				    }
					
					
				    String address = cursor.getString(addressColumn);
				    Contact contact = null;
			        if (address != null) {
			            address = address.trim();
			            if (address.length() > 0) {
			                contact = lookupPerson(address, msisdnTypeService, context);
			            }
			        }
					
					Calendar date = Calendar.getInstance();
					long millis = cursor.getLong(dateColumn);
					date.setTimeInMillis(millis);
					if (countryInterval == null || countryInterval.to < millis) {
						countryInterval = countryStore.getCountryIntervalForDate(millis, EncogeTuFacturaApp.DEFAULT_COUNTRY);
					}
					String countryOfPhone = countryInterval.countryCode;
					smses.add(new Sms(type, contact, date, countryOfPhone));
	
				} while (cursor.moveToNext());
			}
		} finally {
			cursor.close();
		}
        return smses;
    }
	
	private static Contact lookupPerson(String address, MsisdnTypeService msisdnTypeService, Context context) {
		if (!contactCache.containsKey(address)) {
	        Uri personUri = Uri.withAppendedPath(Phones.CONTENT_FILTER_URL, address);
	    	final String[] PHONE_PROJECTION = new String[] {Phones.PERSON_ID, People.NAME, Phones.NUMBER};
	        Cursor phoneCursor = context.getContentResolver().query(personUri, PHONE_PROJECTION, null, null, null);

	        if (phoneCursor.moveToFirst()) {
	            int indexNumber = phoneCursor.getColumnIndex(Phones.NUMBER);
	            String number = phoneCursor.getString(indexNumber);
	        	int indexName = phoneCursor.getColumnIndex(People.NAME);
	            String name = phoneCursor.getString(indexName);
	            phoneCursor.close();
	            Contact contact = ContactStore.createContact(number, name, msisdnTypeService);
	            contactCache.put(address, contact);
	        } else {
	        	Contact contact = ContactStore.createContact(address, address, msisdnTypeService);
	        	contactCache.put(address, contact);
	        }
		}
		return contactCache.get(address);
	}
}