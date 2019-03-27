package com.datalabchina.bll1;

import org.apache.log4j.Logger;

public class TimerRMQ implements Runnable {
	
	private static Logger logger = Logger.getLogger(TimerRMQ.class);
		
	public void run() {
		try {
			while (true) {
				logger.info("ReceiveCount = " + RecvRMQ.lReceiveCount);
				//System.out.println(System.currentTimeMillis() - lastReceiveTime));
				if ((System.currentTimeMillis() - RecvRMQ.lastReceiveTime) >= 1000 * 60 * 10) {
					logger.error("meessage not receive in 10 min. Spider will exit");
					System.exit(0);
				};				
				
				Thread.sleep(1000 * 60 * 5);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}
	}

}
