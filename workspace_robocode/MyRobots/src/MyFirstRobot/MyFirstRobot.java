package MyFirstRobot;

import NeuralNet.CommonInterface;
import robocode.*;
import robocode.AdvancedRobot;
import robocode.BulletHitEvent;
import robocode.BulletMissedEvent;
import robocode.ScannedRobotEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;


public class MyFirstRobot extends AdvancedRobot {
	//Enemy keeps track of absolute enemy direction
	double enemy = 0;
	boolean lock=false;
	long lastlock=0;
	double distance=0;
	double confidence = 1.0;

	public void run() {
		while(true) {
			if(lock) {
				setAhead(distance-100);
				setTurnRight(enemy-getHeading());
				setTurnRadarRight(getHeading()-getRadarHeading());
				setFire(distance*confidence/30);
				execute();
				if(getTime()-lastlock > 10) lock=false;
			} else {
				setTurnRadarRight(enemy-getHeading()>0?360:-360);
				execute();
			}
		}
	}
	public void onScannedRobot(ScannedRobotEvent e) {
		enemy = getHeading()+e.getBearing();
		distance = e.getDistance();
		lock = true;
		lastlock = getTime();
		
	}
	public void onBulletHit(BulletHitEvent e) {
		confidence=1.0;
	}
	public void onBulletMissed(BulletMissedEvent e) {
		confidence*=0.9;
	}
}