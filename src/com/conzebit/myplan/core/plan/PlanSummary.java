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
package com.conzebit.myplan.core.plan;

import java.util.ArrayList;

public class PlanSummary implements Comparable<PlanSummary> {
	
	private double total = 0;
	private AbstractPlan plan = null;
	
	private ArrayList<PlanChargeable> planCalls;
	
	public PlanSummary(AbstractPlan plan) {
		this.plan = plan;
		this.planCalls = new ArrayList<PlanChargeable>();
	}
	
	public AbstractPlan getPlan() {
		return this.plan;
	}

	public void addPlanCall(PlanChargeable planCall) {
		Double price = planCall.getPrice();
		if (price != null) {
			this.total += price;
		}
		this.planCalls.add(planCall);
	}
	
	public ArrayList<PlanChargeable> getPlanCalls() {
		return this.planCalls;
	}

	public double getTotalPrice() {
		return this.total;
	}
	
	public int compareTo(PlanSummary o) {
		return this.plan.getPlanName().compareToIgnoreCase(o.plan.getPlanName());
	}
}
