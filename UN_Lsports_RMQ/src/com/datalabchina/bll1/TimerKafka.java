package com.datalabchina.bll1;

import org.apache.log4j.Logger;

public class TimerKafka implements Runnable {
	
	private static Logger logger = Logger.getLogger(TimerKafka.class);
		
	public void run() {
		try {
			while (true) {
				logger.info("ReceiveCount = " + RecvKafka.lReceiveCount);
				//System.out.println(System.currentTimeMillis() - lastReceiveTime));
				if ((System.currentTimeMillis() - RecvKafka.lastReceiveTime) >= 1000 * 60 * 10) {
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
