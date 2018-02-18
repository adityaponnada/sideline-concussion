package edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.orientation;

/*
 *  This file is part of Level (an Android Bubble Level).
 *  <https://github.com/avianey/Level>
 *  
 *  Copyright (C) 2014 Antoine Vianey
 *  
 *  Level is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Level is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Level. If not, see <http://www.gnu.org/licenses/>
 */
public interface OrientationListener {

	public void onOrientationChanged(edu.neu.hinf5300.concussionsidelineresponse.BalanceTest.orientation.Orientation orientation, float pitch, float roll, float balance);
	
	public void onCalibrationSaved(boolean success);
	
	public void onCalibrationReset(boolean success);
	
}
