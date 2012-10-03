/*
 * This file is part of Simyo - Encoge tu factura.
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
package es.simyo.encogetufactura.activity;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.conzebit.myplan.core.plan.PlanSummary;

import es.simyo.encogetufactura.R;


public class PlanSummaryAdapter extends ArrayAdapter<PlanSummary> {

    public PlanSummaryAdapter(Context context, int textViewResourceId, ArrayList<PlanSummary> planSummaries) {
    	super(context, textViewResourceId, planSummaries);
    }
	
    public View getView(int position, View convertView, ViewGroup parent) { 
        PlanSummary planSummary = super.getItem(position);
        return new PlanSummaryAdapterView(super.getContext(), planSummary);
    }
    
	class PlanSummaryAdapterView extends LinearLayout {        
        public PlanSummaryAdapterView(Context context, PlanSummary planSummary) {
            super(context);
            PlanHelper.getPlanSummaryView(context, this, planSummary, R.layout.summary_line);
        }
	}
}